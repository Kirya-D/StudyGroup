import filesystem from "fs"
import mssql from "mssql"
import path from "path"
import { Account } from "./Account.js"
import { Question } from "./Question.js"
import { Studyguide } from "./Studyguide.js"
import { Primitives } from "./utils/Primitives.js"

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

const CREATE_ACCOUNT = getQueryFromFile("create_account.sql") // IMPLEMENTED FUNCTION FOR THIS QUERY
const CREATE_CHOICE = getQueryFromFile("create_choice.sql") // IMPLEMENTED FUNCTION FOR THIS QUERY
const CREATE_QUESTION = getQueryFromFile("create_question.sql") // IMPLEMENTED FUNCTION FOR THIS QUERY
const DELETE_STUDYGUIDE = getQueryFromFile("delete_studyguide.sql") // IMPLEMENTED FUNCTION FOR THIS QUERY
const GET_ALL_ACCOUNTS = getQueryFromFile("get_all_accounts.sql") // IMPLEMENTED FUNCTION FOR THIS QUERY
const GET_STUDYGUIDES_WITH_SUBSTRING = getQueryFromFile("get_studyguides_with_substring.sql") // IMPLEMENTED FUNCTION FOR THIS QUERY
const UPSERT_STUDYGUIDE_STATUS = getQueryFromFile("upsert_studyguide_status.sql") // IMPLEMENTED FUNCTION FOR THIS QUERY
const UPSERT_STUDYGUIDE = getQueryFromFile("upsert_studyguide.sql") // IMPLEMENTED FUNCTION FOR THIS QUERY

class Database {

    /**
     * @type {mssql.ConnectionPool | null}
     */
    #pool
    
    /**
     * Connects to the database associated with the given config.
     * 
     * @param {string | mssql.config} config the connection config.
     * @param {string | null} setupQuery The query to run once connected to the database.
     */
    async connectToDatabase({config, setupQuery = null}) {
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
        /**
         * @type {Map<int, Account>}
         */
        const accounts = new Map()
        const results = await this.#pool.query(GET_ALL_ACCOUNTS)

        results.recordsets[0].forEach(accountInfo => {
            const accountParams = {
                id: accountInfo.id,
                username: accountInfo.username,
                password: accountInfo.password
            }
            const newAccount = new Account(accountParams)
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
     * @param {int} pageNum The offset multiplier for the result index
     * @param {int} maxResultCount The max number of results to return. Defaults to 50
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
        /**
         * @type {Set<Studyguide>}
         */
        const studyguides = new Set()
        const preppedSearch = this.#prepareSearchText(search)
        const result = await this.#pool.request()
            .input("search", mssql.NVarChar(255), preppedSearch)
            .input("offset", mssql.Int(), pageNum)
            .input("maxResults", mssql.Int(), maxResultCount)
            .query(GET_STUDYGUIDES_WITH_SUBSTRING)
        
        result.recordset.forEach(guideInfo => {
            const guideParams = {
                id: guideInfo.id,
                title: guideInfo.title,
                description: guideInfo.description,
                questions: new Set(),
                questionCount: guideInfo.questionCount,
                creatorId: guideInfo.creatorId
            }
            const newGuide = new Studyguide(guideParams)
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
     * @param {string} username The username
     * @param {string} password The password
     * 
     * @throws {TypeError} If username or password are not strings
     */
    async createAccount(username, password) {
        if (typeof username !== Primitives.STRING) {
            throw new TypeError("username must be a string")
        }
        if (typeof password !== Primitives.STRING) {
            throw new TypeError("password must be a string")
        }

        await this.#pool.request()
            .input("username", mssql.NVarChar(32), username)
            .input("password", mssql.NVarChar(32), password)
            .query(CREATE_ACCOUNT)
    }

    /**
     * Upserts a studyguide in the database with the given information
     * and returns the id of the studyguide in the database.
     * 
     * @param {Studyguide} studyguide The studyguide to upsert
     * @param {boolean} favorited If the account has the studyguide favorited. default false
     * @param {boolean} downloaded If the account has the studyguide downloaded. default false
     * 
     * @returns {int} The id of the studyguide as stored in the database
     * 
     * @throws {TypeError} 
     * - If studyguide isn't a Studyguide object
     * - If favorited or downloaded aren't booleans
     */
    async upsertStudyguide(studyguide, favorited = false, downloaded = false) {
        if (!(studyguide instanceof Studyguide)) {
            throw new TypeError("studyguide must be a Studyguide object")
        }
        if (typeof favorited !== Primitives.BOOLEAN) {
            throw new TypeError("favorited must be an int")
        }
        if (typeof downloaded !== Primitives.BOOLEAN) {
            throw new TypeError("downloaded must be an int")
        }

        const result = await this.#pool.request()
            .input("studyguideId", mssql.Int(), studyguide.id())
            .input("title", mssql.NVarChar(255), studyguide.title())
            .input("description", mssql.NVarChar(255), studyguide.description())
            .input("accountId", mssql.Int(), studyguide.creatorId())
            .output("newStudyguideId", mssql.Int())
            .query(UPSERT_STUDYGUIDE)

        /**
         * @type {int}
         */
        const actualStudyguideId = result.output.newStudyguideId;

        for (const question of studyguide.questions()) {
            const questionId = await this.#createQuestion(question, actualStudyguideId)
            
            for (const choice of question.choices()) {
                await this.#createChoice(choice, questionId)
            }
        }

        await this.upsertStudyguideStatus(studyguide.creatorId(), actualStudyguideId, favorited, downloaded)

        return actualStudyguideId
    }

    /**
     * @param {Question} question The question to add to the database
     * @param {int} studyguideId The id of the studyguide it is a part of
     */
    async #createQuestion(question, studyguideId) {
        const questionResult = await this.#pool.request()
                .input("text", mssql.NVarChar(255), question.text())
                .input("studyguideId", mssql.Int(), studyguideId)
                .output("newId", mssql.Int())
                .query(CREATE_QUESTION)
            
        /**
         * @type {int}
         */
        const questionId = questionResult.output.newId
        return questionId
    }

    /**
     * @param {Choice} choice The choice to add to the database
     * @param {int} questionId The id of the question it is a part of
     */
    async #createChoice(choice, questionId) {
        await this.#pool.request()
            .input("text", mssql.NVarChar(255), choice.text())
            .input("isAnswer", mssql.Bit(), choice.isAnswer())
            .input("questionId", mssql.Int(), questionId)
            .query(CREATE_CHOICE)
    }

    /**
     * Deletes the studyguide associated with studyguideId
     * from the database if the given accountId is the creator's.
     * 
     * @param {int} studyguideId The id of the studyguide to delete
     * @param {int} accountId The id of the account attempting to delete
     * 
     * @throws {TypeError} If studyguideId or accountId are not ints
     */
    async deleteStudyguide(studyguideId, accountId) {
        if (!Number.isInteger(studyguideId)) {
            throw new TypeError("studyguideId must be an int")
        }
        if (!Number.isInteger(accountId)) {
            throw new TypeError("accountId must be an int")
        }

        await this.#pool.request()
            .input("id", mssql.Int(), studyguideId)
            .input("creatorId", mssql.Int(), accountId)
            .query(DELETE_STUDYGUIDE)
    }

    /**
     * Upsert the status of the studyguide for the given account
     * 
     * @param {int} accountId The account id
     * @param {int} studyguideId The studyguide id 
     * @param {boolean} favorited If the account has the studyguide favorited
     * @param {boolean} downloaded If the account has the studyguide downloaded
     * 
     * @throws {TypeError}
     * - If accountId or studyguideId are not ints
     * - If favorited or downloaded are not booleans
     */
    async upsertStudyguideStatus(accountId, studyguideId, favorited, downloaded) {
        if (!Number.isInteger(accountId)) {
            throw new TypeError("accountId must be an int")
        }
        if (!Number.isInteger(studyguideId)) {
            throw new TypeError("studyguideId must be an int")
        }
        if (typeof favorited !== Primitives.BOOLEAN) {
            throw new TypeError("favorited must be an int")
        }
        if (typeof downloaded !== Primitives.BOOLEAN) {
            throw new TypeError("downloaded must be an int")
        }

        await this.#pool.request()
            .input("accountId", mssql.Int(), accountId)
            .input("studyguideId", mssql.Int(), studyguideId)
            .input("favorited", mssql.Bit(), favorited)
            .input("downloaded", mssql.Bit(), downloaded)
            .query(UPSERT_STUDYGUIDE_STATUS)
    }
}

export { Database }

