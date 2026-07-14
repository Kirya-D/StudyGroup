import cookieParser from "cookie-parser"
import express from "express"
import helmet from "helmet"
import mssql from "mssql"
import { Database } from "./Database.js"
import { AccountHandler } from "./request_handlers/AccountHandler.js"
import { SessionHandler } from "./request_handlers/SessionHandler.js"
import { StudyguideHandler } from "./request_handlers/StudyguideHandler.js"
import { Route } from "./utils/Route.js"
import { StatusCode } from "./utils/StatusCode.js"

const dbConfig = process.env.DB_URL
const db = new Database()
let curAttempt = 0
const maxAttempts = 3

async function tryConnect() {
    await db.connectToDatabase({ config: dbConfig }).then(
        (value) => {
            console.log("Connected to database")
        },
        (reason) => {
            if (reason instanceof mssql.ConnectionError && curAttempt <= maxAttempts) {
                curAttempt++
                tryConnect()
            }
        }
    )
}

await tryConnect()
await AccountHandler.loadAccountsFromDatabase(db)

const app = express()
const port = process.env.PORT
const host = process.env.HOST

app.enable('trust proxy')
app.use(cookieParser())
app.use(helmet())
app.use(express.json())


/**
 * Send response to client.
 * 
 * @param {express.Response} response The response object
 * @param {number} status The status number
 * @param {string} json The response body content
 */
function respond(response, status, json) {
    json.status = undefined
    response.status(status).json(json)
}

/**
 * Default response factory for requests that resulted in unhandled exceptions
 * @param {express.Response} res 
 * @param {string} reason 
 */
function failureResponse(res, reason) {
    const failResponse = {
        success: false,
        message: reason
    }
    respond(res, StatusCode.INTERNAL_SERVER_ERROR, failResponse)
}

app.route(Route.SESSION)
    .post((req, res) => {
        const username = req.body.username
        const password = req.body.password
        const account = AccountHandler.getAccountWithCredentials(username, password)

        SessionHandler.startSession(account).then(
            (response) => {
                if (response.cookieInfo) {
                    const name = response.cookieInfo.name
                    const val = response.cookieInfo.value
                    const options = response.cookieInfo.options
                    res.cookie(name, val, options)
                }
                respond(res, response.status, response)
            },
            (reason) => failureResponse(res, reason.message)
        )
    })
    .delete((req, res) => {
        const sessionId = req.cookies[SessionHandler.cookieName]
        SessionHandler.endSession(sessionId).then(
            (response) => {
                if (response.cookieName) {
                    res.clearCookie(cookieName)
                }
                respond(res, response.status, response)
            },
            (reason) => failureResponse(res, reason.message)
        )
    })
app.route(Route.ACCOUNT)
    .post((req, res) => {
        const username = req.body.username
        const password = req.body.password

        AccountHandler.createAccount(username, password).then(
            (response) => respond(res, response.status, response),
            (reason) => failureResponse(res, reason.message)
        )
    })
app.route(`${Route.STUDYGUIDE}{id}`)
    .post((req, res) => {
        const uploader = SessionHandler.getAccountFromRequest(req)
        const studyguide = req.body.studyguide
        StudyguideHandler.upsertStudyguide(uploader, studyguide).then(
            (response) => respond(res, response.status, response),
            (reason) => failureResponse(res, reason.message)
        )
    })
    .delete((req, res) => {
        const deleter = SessionHandler.getAccountFromRequest(req)
        const id = req.query.id
        StudyguideHandler.deleteStudyguide(deleter, id).then(
            (response) => {
                console.log(response.success)
                console.table(response)
                respond(res, response.status, response)
            },
            (reason) => failureResponse(res, reason.message)
        )
    })
app.get(`${Route.SEARCH}${Route.ACCOUNT}/:username`, (req, res) => {
    respond(res, 200, {})
})
app.get(`${Route.SEARCH}${Route.STUDYGUIDE}/:search{page=0, max=50}`, (req, res) => {
    const searchingUser = SessionHandler.getAccountFromRequest(req)
    const search = req.params.search
    const page = parseInt(req.query.page)
    const max = parseInt(req.query.max)
    StudyguideHandler.findStudyguides(searchingUser, search, page, max).then(
        (response) => respond(res, response.status, response),
        (reason) => failureResponse(res, reason.message)
    )
})

app.listen(port, host, () => {
    console.log(`Server is alive on ${host}:${port}`)
})

export { }

