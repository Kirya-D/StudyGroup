import crypto from "crypto"
import { Account } from "../model/Account.js"
import { Choice } from "../model/Choice.js"
import { Question } from "../model/Question.js"
import { Studyguide } from "../model/Studyguide.js"
import { Primitives } from "../utils/Primitives.js"
import { StatusCode } from "../utils/StatusCode.js"
/** @import { Queryable, RequestStudyguide } from "../utils/Types.js" */

/** @type {Map<string, Studyguide>} */
const uuidStudyguides = new Map()
/** @type {Set<string>} */
const changedStudyguideIds = new Set()

/**
 * Attempts to create or update a studyguide with the given
 * 
 * @param {Account} requester The account that sent the request
 * @param {RequestStudyguide} requestedGuide The studyguide information
 * 
 * @returns A response to the request
 */
async function upsertStudyguide(requester, requestedGuide) {
    let success = false
    let status = undefined
    let guideId = undefined
    let message = ""

    if (!(requester instanceof Account) || requestedGuide == undefined) {
        status = StatusCode.BAD_REQUEST
        message = "Missing account or studyguide information"
    }

    const updatingExistingGuide = requestedGuide.id != undefined && uuidStudyguides.get(requestedGuide.id) != undefined
    const userIsCreator = updatingExistingGuide ? uuidStudyguides.get(requestedGuide.id).creatorId() === requester.id() : true
    if (updatingExistingGuide && !userIsCreator) {
        status = StatusCode.FORBIDDEN
        message = `${requester.username()} does not have permissions to modify ${requestedGuide.title}`
    }

    if (status == undefined) {
        guideId = updatingExistingGuide ? requestedGuide.id : crypto.randomUUID()
        const title = requestedGuide.title
        const description = requestedGuide.description
        const rawQuestions = requestedGuide.questions
        /** @type {Array<Question>} */
        const questions = []
        for (const rawQuestion of rawQuestions) {
            const text = rawQuestion.question
            /** @type {Set<Choice>} */
            const choices = new Set()
            const rawChoices = new Set(rawQuestion.choices)
            const rawAnswers = new Set(rawQuestion.answers)
            for (const rawChoice of rawChoices) {
                const isAnswer = rawAnswers.has(rawChoice)
                const choice = new Choice(rawChoice, isAnswer)
                choices.add(choice)
            }

            const newQuestion = new Question(text, choices)
            questions.push(newQuestion)
        }
        const serverGuide = new Studyguide(guideId, title, description, new Set(questions), requester.id(), questions.length)
        requestedGuide.favorited ? requester.favorite(guideId) : requester.unfavorite(guideId)
        requestedGuide.downloaded ? requester.download(guideId) : requester.undownload(guideId)
        uuidStudyguides.set(guideId, serverGuide)
        changedStudyguideIds.add(guideId)
        status = updatingExistingGuide ? StatusCode.OK : StatusCode.CREATED
        success = true
    }

    return {
        success: success,
        id: guideId,
        status: status,
        message: message
    }
}

/**
 * 
 * @param {Account} requester The account that sent the request
 * @param {crypto.UUID} id The id of the studyguide to delete
 * 
 * @returns A response to the request
 */
async function deleteStudyguide(requester, id) {
    let success = false
    let status = undefined
    let message = ""
    const associatedStudyguide = uuidStudyguides.get(id)

    const requesterIsValid = requester instanceof Account
    const studyguideExists = associatedStudyguide != undefined
    if (!requesterIsValid || !studyguideExists) {
        status = StatusCode.BAD_REQUEST
        message = "Missing account or studyguide information"
    }
    
    if (requester && associatedStudyguide && associatedStudyguide.creatorId() !== requester.id()) {
        status = StatusCode.FORBIDDEN
        message = `${requester.username()} does not have permissions to delete ${associatedStudyguide.title()}`
    }

    if (status == undefined) {
        success = true
        status = StatusCode.NO_CONTENT
        uuidStudyguides.delete(id)
        changedStudyguideIds.add(id)
    }

    return {
        success: success,
        status: status,
        message: message
    }
}


/**
 * Returns the studyguides that contain the search (case-insensitive) in the title or description and are indexed
 * between [(page * maxAmount), (page * maxAmount) + maxAmount] inclusive 
 * 
 * @param {Account} requester The account that sent the request
 * @param {string} search The search query
 * @param {number} page The page to query
 * @param {number} maxAmount The max amount of studyguide to return
 * 
 * @returns A response to the request
 */
async function findStudyguides(requester, search, page, maxAmount) {
    const caseInsensitiveSearch = search?.toLowerCase()
    let success = false
    const found = []
    let status = undefined
    let message = ""

    const missingInformation = typeof caseInsensitiveSearch !== Primitives.STRING
    const expectedNumbersNotNums = typeof page != Primitives.NUMBER || typeof maxAmount != Primitives.NUMBER
    const expectedNumbersNotInts = !Number.isInteger(page) || !Number.isInteger(maxAmount)
    if (missingInformation) {
        status = StatusCode.BAD_REQUEST
        message = "Missing search text"
    } else if (expectedNumbersNotNums || expectedNumbersNotInts) {
        status = StatusCode.BAD_REQUEST
        message = "Invalid page number or return size"
    }

    if (status == undefined) {
        success = true
        status = StatusCode.OK
        /** @type {Array<RequestStudyguide>} */
        const skipFirst = page * maxAmount
        let skipped = 0 
        for (const guide of uuidStudyguides.values()) {
            const caseInsensitiveTitle = guide.title().toLowerCase()
            const caseInsensitiveDescription = guide.description().toLowerCase()
            const foundInTitle = caseInsensitiveTitle.includes(caseInsensitiveSearch)
            const foundInDescription = foundInTitle ? true : caseInsensitiveDescription.includes(caseInsensitiveSearch)

            if (foundInTitle || foundInDescription) {
                if (skipped < skipFirst) {
                    skipped += 1
                    continue
                }
                const guideId = guide.id()
                const questionsObject = []
                for (const question of guide.questions()) {
                    const jsonified = {
                        answers: [...question.choices()].filter((c) => c.isAnswer()).map((c) => c.text()),
                        choices: [...question.choices()].map((c) => c.text()),
                        question: question.text(),
                        questionType: "FREE_RESPONSE"
                    }
                    if (jsonified.choices.length > 1) {
                        jsonified.questionType = "MULTIPLE_CHOICE"
                    }
                    questionsObject.push(jsonified)
                }
                const guideInfo = {
                    id: guideId,
                    creatorUsername: undefined,
                    creatorId: guide.creatorId(),
                    title: guide.title(),
                    description: guide.description(),
                    downloaded: requester?.downloadedStudyguides().has(guideId) ?? false,
                    favorited: requester?.favoritedStudyguides().has(guideId) ?? false,
                    uploaded: true,
                    questions: questionsObject,
                }
                found.push(guideInfo)
            }

            if (found.length >= maxAmount) {
                break
            }
        }
    }

    return {
        success: success,
        results: found,
        status: status,
        message: message
    }
}

/**
 * Attempt to load all studyguides from databae
 * 
 * @param {Queryable} database The database to load from
 */
async function loadStudyguidesFromDatabase(database) {
    uuidStudyguides.clear()
    const dbStudyguides = await database.getAllStudyguides()
    for (const guide of dbStudyguides) {
        const id = guide.id()
        uuidStudyguides.set(id, guide)
    }
}

/**
 * Propogates updated studyguide information to the given database
 * 
 * @param {Queryable} database The database to propogate changes to
 */
async function propogateStudyguideChangesToDatabase(database) {    
    /** @type {UpsertGuide[]} */
    const upsertedGuides = []
    /** @type {string[]} */
    const deletedGuideIds = []

    for (const id of changedStudyguideIds) {
        const associatedGuide = uuidStudyguides.get(id)
        if (associatedGuide) {
            upsertedGuides.push(associatedGuide)
        } else {
            deletedGuideIds.push(id)
        }
    }

    await database.deleteStudyguides(deletedGuideIds)
    await database.upsertStudyguides(upsertedGuides)
    
    clearStoredChanges()
}

function clearStoredChanges() {
    changedStudyguideIds.clear()
}

const StudyguideHandler = Object.freeze({
    upsertStudyguide: upsertStudyguide,
    deleteStudyguide: deleteStudyguide,
    findStudyguides: findStudyguides,
    loadStudyguidesFromDatabase: loadStudyguidesFromDatabase,
    propogateStudyguideChangesToDatabase: propogateStudyguideChangesToDatabase,
    clearStoredChanges: clearStoredChanges
})

export { StudyguideHandler }

