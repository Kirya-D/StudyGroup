import crypto from "crypto"
import { Account } from "../Account.js"
import { Choice } from "../Choice.js"
import { Question } from "../Question.js"
import { Studyguide } from "../Studyguide.js"
import { Primitives } from "../utils/Primitives.js"
import { StatusCode } from "../utils/StatusCode.js"

/**
 * @typedef RequestQuestion
 * @property {string} question The text
 * @property {Array<string>} choices The choices
 * @property {Array<string>} answers The answers
 */

/**
 * @typedef RequestStudyguide
 * @property {string | undefined} id The id of the studyguide
 * @property {string | undefined} creatorId The id of the creator of the studyguide
 * @property {string} title The title
 * @property {string} description The description
 * @property {boolean} downloaded If the creator has the studyguide downloaded
 * @property {boolean} favorited If the creator has the studyguide favorited
 * @property {Array<RequestQuestion>} questions The questions
 * @property {number} questionCount The number of questions
 */

/** @type {Map<string, Studyguide>} */
const uuidStudyguides = new Map()

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
        uuidStudyguides.set(serverGuide.id(), serverGuide)
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
    }

    return {
        success: success,
        status: status,
        message: message
    }
}


/**
 * Returns the studyguides that contain the search in the title or description and are indexed
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
    let success = false
    const found = []
    let status = undefined
    let message = ""

    const missingInformation = !(requester instanceof Account) || typeof search !== Primitives.STRING
    const invalidInformation = !Number.isInteger(page) || !Number.isInteger(maxAmount)
    if (missingInformation) {
        status = StatusCode.BAD_REQUEST
        message = "Missing account information or search text"
    } else if (invalidInformation) {
        status = StatusCode.BAD_REQUEST
        message = "Missing page or max return size"
    }

    if (status == undefined) {
        success = true
        status = StatusCode.OK
        /** @type {Array<RequestStudyguide>} */
        const skipFirst = page * maxAmount
        let skipped = 0 
        for (const guide of uuidStudyguides.values()) {
            const foundInTitle = guide.title().includes(search)
            const foundInDescription = foundInTitle ? true : guide.description().includes(search)

            if (foundInTitle || foundInDescription) {
                if (skipped < skipFirst) {
                    skipped += 1
                    continue
                }
                const guideId = guide.id()
                const guideInfo = {
                    id: guideId,
                    creatorId: guide.creatorId(),
                    title: guide.title(),
                    description: guide.description(),
                    downloaded: requester.downloadedStudyguides().has(guideId),
                    favorited: requester.favoritedStudyguides().has(guideId),
                    questions: undefined,
                    questionCount: guide.questionCount()
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

const StudyguideHandler = Object.freeze({
    upsertStudyguide: upsertStudyguide,
    deleteStudyguide: deleteStudyguide,
    findStudyguides: findStudyguides,
})

export { StudyguideHandler }

