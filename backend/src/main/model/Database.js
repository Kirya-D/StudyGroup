import filesystem from "fs"
import mssql from "mssql"
import path from "path"
import { Primitives } from "../utils/Primitives.js"
import { Account } from "./Account.js"
import { Question } from "./Question.js"
import { Studyguide } from "./Studyguide.js"

/**
 * 
 * @param {string} filename The name of the file
 * @returns {string} The contents of the file
 */
function getQueryFromFile(filename) {
    const QUERY_DIRECTORY = "././queries"
    const filepath = path.join(QUERY_DIRECTORY, filename)
    const filecontents = filesystem.readFileSync(filepath, "utf-8")

    return filecontents
}

const CREATE_ACCOUNT = getQueryFromFile("create_account.sql")
const CREATE_CHOICE = getQueryFromFile("create_choice.sql")
const CREATE_QUESTION = getQueryFromFile("create_question.sql")
const DELETE_STUDYGUIDE = getQueryFromFile("delete_studyguide.sql")
const GET_ALL_ACCOUNTS = getQueryFromFile("get_all_accounts.sql")
const GET_STUDYGUIDES_WITH_SUBSTRING = getQueryFromFile("get_studyguides_with_substring.sql")
const UPSERT_STUDYGUIDE_STATUS = getQueryFromFile("upsert_studyguide_status.sql")
const UPSERT_STUDYGUIDE = getQueryFromFile("upsert_studyguide.sql")

class Database {

    /** @type {mssql.ConnectionPool | null} */
    #pool = null
    
    /**
     * Connects to the database associated with the given config.
     * 
     * @param {string | mssql.config} config the connection config.
     * @param {string | null} setupQuery The query to run once connected to the database.
     */
    async connectToDatabase({ config, setupQuery = null }) {
        this.#pool = await mssql.connect(config)
        if (setupQuery != null) {
            await this.#pool.query(setupQuery)
        }
    }

    async disconnect() {
        await this.#pool.close()
    }

    /**
     * Returns all accounts in the database
     */
    async getAllAccounts() {
        /** @type {Map<string, Account>} */
        const accounts = new Map()
        const results = await this.#pool.query(GET_ALL_ACCOUNTS)

        results.recordsets[0].forEach(accountInfo => {
            const id = accountInfo.id
            const username = accountInfo.username
            const password = accountInfo.password
            const newAccount = new Account(id, username, password)
            accounts.set(newAccount.id(), newAccount)
        })
        results.recordsets[1].forEach(statusInfo => {
            const account = accounts.get(statusInfo.idAccount)
            if (account != undefined) {
                const guideId = statusInfo.idStudyguide
                statusInfo.favorited ? account.favorite(guideId) : null
                statusInfo.downloaded ? account.download(guideId) : null
            }
        })

        return new Set(accounts.values())
    }

    /**
     * Returns a set of studyguides matching the search criteria.
     * 
     * @param {string} search The search criteria
     * @param {number} pageNum The offset multiplier for the result index
     * @param {number} maxResultCount The max number of results to return. Defaults to 50
     * 
     * @throws {TypeError} If search is not a string or pageNum/maxResultCount are not ints
     */
    async getStudyguidesWithSubstring(search, pageNum, maxResultCount = 50) {
        if (typeof search !== Primitives.STRING) {
            throw new TypeError("search must be a string")
        }
        if (!Number.isInteger(pageNum)) {
            throw new TypeError("pageNum must be an int")
        }
        if (!Number.isInteger(maxResultCount)) {
            throw new TypeError("maxResultCount must be an int")
        }
        /** @type {Set<Studyguide>} */
        const studyguides = new Set()
        const preppedSearch = this.#prepareSearchText(search)
        const result = await this.#pool.request()
            .input("search", mssql.NVarChar(255), preppedSearch)
            .input("offset", mssql.Int(), pageNum)
            .input("maxResults", mssql.Int(), maxResultCount)
            .query(GET_STUDYGUIDES_WITH_SUBSTRING)
        
        result.recordset.forEach(guideInfo => {
            const id = guideInfo.id
            const title = guideInfo.title
            const description = guideInfo.description
            const questions = new Set()
            const creatorId = guideInfo.creatorId
            const questionCount = guideInfo.questionCount
            const newGuide = new Studyguide(id, title, description, questions, creatorId, questionCount)
            studyguides.add(newGuide)
        })

        return studyguides
    }

    /**
     * Returns a version of the string that escapes included wildcard characters
     * and wraps the entire text in wildcard characters
     * 
     * @param {string} rawSearch The raw search text
     */
    #prepareSearchText(rawSearch) {
        const escapedSearch = rawSearch
            .replaceAll("%", "[%]")
            .replaceAll("_", "[_]")
            .replaceAll("[", "[[]")
        
        const wrappedSearch = `%${escapedSearch}%`

        return wrappedSearch
    }
    
    /**
     * Create an account with the given credentials
     * 
     * @param {string} uuid The uuid
     * @param {string} username The username
     * @param {string} password The password
     * 
     * @throws {TypeError} If uuid, username, or password are not strings
     */
    async createAccount(uuid, username, password) {
        if (typeof uuid !== Primitives.STRING) {
            throw new TypeError("uuid must be a string")
        }
        if (typeof username !== Primitives.STRING) {
            throw new TypeError("username must be a string")
        }
        if (typeof password !== Primitives.STRING) {
            throw new TypeError("password must be a string")
        }

        await this.#pool.request()
            .input("uuid", mssql.NVarChar(36), uuid)
            .input("username", mssql.NVarChar(32), username)
            .input("password", mssql.NVarChar(32), password)
            .query(CREATE_ACCOUNT)
    }

    /**
     * Upserts the studyguides in the database with the given information.
     * 
     * @param {Studyguide[]} guides The guides and the associated information to upsert
     * 
     * @throws {TypeError} If an entry in guides isn't a Studyguide object
     */
    async upsertStudyguides(guides) {
        if (!Array.isArray(guides)) {
            throw new TypeError("guides must be an array")
        }

        for (const studyguide of guides) {
            if (!(studyguide instanceof Studyguide)) {
                throw new TypeError("studyguide must be a Studyguide object")
            }

            try {
                const guideId = studyguide.id()
                const creatorId = studyguide.creatorId()
                await this.#pool.request()
                    .input("studyguideId", mssql.NVarChar(36), guideId)
                    .input("title", mssql.NVarChar(255), studyguide.title())
                    .input("description", mssql.NVarChar(255), studyguide.description())
                    .input("accountId", mssql.NVarChar(36), creatorId)
                    .query(UPSERT_STUDYGUIDE)
                
                const questions = studyguide.questions()
                for (const question of questions) {
                    const questionId = await this.#createQuestion(question, guideId)
                    const choices = question.choices()
                    
                    for (const choice of choices) {
                        await this.#createChoice(choice, questionId)
                    }
                }
            } catch (error) {
                if (error instanceof Error) {
                    console.log(error.messag)
                }
            }
        }
    }

    /**
     * @param {Question} question The question to add to the database
     * @param {number} studyguideId The id of the studyguide it is a part of
     */
    async #createQuestion(question, studyguideId) {
        const questionResult = await this.#pool.request()
                .input("text", mssql.NVarChar(255), question.text())
                .input("studyguideId", mssql.NVarChar(36), studyguideId)
                .output("newId", mssql.NVarChar(36))
                .query(CREATE_QUESTION)
            
        /** @type {number} */
        const questionId = questionResult.output.newId
        return questionId
    }

    /**
     * @param {Choice} choice The choice to add to the database
     * @param {number} questionId The id of the question it is a part of
     */
    async #createChoice(choice, questionId) {
        await this.#pool.request()
            .input("text", mssql.NVarChar(255), choice.text())
            .input("isAnswer", mssql.Bit(), choice.isAnswer())
            .input("questionId", mssql.NVarChar(36), questionId)
            .query(CREATE_CHOICE)
    }

    /**
     * Deletes the studyguides associated with studyguideIds from the database.
     * 
     * @param {string[]} studyguideIds The id of the studyguides to delete
     * 
     * @throws {TypeError} If studyguideIds is not an array
     * @throws {TypeError} If an entry in studyguideIds is not a string
     */
    async deleteStudyguides(studyguideIds) {
        if (!Array.isArray(studyguideIds)) {
            throw new TypeError("studyguideIds must be an array")
        }
        if (studyguideIds.length === 0) {
            return
        }
        
        for (const id of studyguideIds) {
            if (typeof id !== Primitives.STRING) {
                throw new TypeError("id must be a string")
            }

            try {
                await this.#pool.request()
                    .input("id", mssql.NVarChar(36), id)
                    .query(DELETE_STUDYGUIDE)
            } catch (error) {
                if (error instanceof Error) {
                    console.log(error.messag)
                }
            }
        }
    }

    /**
     * @typedef StatusInfo
     * @property {string} accountId The account id
     * @property {string} studyguideId The studyguide id
     * @property {boolean} favorited The studyguide's favorited status for the account
     * @property {boolean} downloaded The studyguide's downloaded status for the account
     */

    /**
     * Upsert the status of the studyguide for the given account
     * 
     * @param {StatusInfo[]} info The status info to upsert
     * 
     * @throws {TypeError} If an accountId or studyguideId in info are not ints
     * @throws {TypeError} If a favorited or downloaded in info are not booleans
     */
    async upsertStudyguideStatus(info) {
        if (!Array.isArray(info)) {
            throw new TypeError("info must be an array")
        }
        if (info.length === 0) {
            return
        }

        for (const rowInfo of info) {
            if (typeof rowInfo.accountId !== Primitives.STRING) {
                throw new TypeError("accountId must be a string")
            }
            if (typeof rowInfo.studyguideId !== Primitives.STRING) {
                throw new TypeError("studyguideId must be a string")
            }
            if (typeof rowInfo.favorited !== Primitives.BOOLEAN) {
                throw new TypeError("favorited must be an int")
            }
            if (typeof rowInfo.downloaded !== Primitives.BOOLEAN) {
                throw new TypeError("downloaded must be an int")
            }

            try {
                await this.#pool.request()
                    .input("accountId", mssql.NVarChar(36), rowInfo.accountId)
                    .input("studyguideId", mssql.NVarChar(36), rowInfo.studyguideId)
                    .input("favorited", mssql.Bit(), rowInfo.favorited)
                    .input("downloaded", mssql.Bit(), rowInfo.downloaded)
                    .query(UPSERT_STUDYGUIDE_STATUS)
            } catch (error) {
                if (error instanceof Error) {
                    console.log(error.messag)
                }
            }
        }
    }
}

export { Database }

