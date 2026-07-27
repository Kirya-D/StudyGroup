import cookieParser from "cookie-parser"
import express from "express"
import helmet from "helmet"
import { Server as httpServer } from "http"
import { AccountHandler } from "../request_handlers/AccountHandler.js"
import { CredentialValidationHandler } from "../request_handlers/CredentialValidationHandler.js"
import { SessionHandler } from "../request_handlers/SessionHandler.js"
import { StudyguideHandler } from "../request_handlers/StudyguideHandler.js"
import { Route } from "../utils/Route.js"
import { StatusCode } from "../utils/StatusCode.js"

/** @type {express.Express | undefined} */
let app = undefined
/** @type {httpServer | undefined} */
let appServer = undefined

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
 * Default response for requests that resulted in unhandled exceptions
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

/**
 * Sets up routing for credential validation
 */
function routeCredentialValidation() {
    app.post(Route.VALIDATE_CREDENTIAL, (req, res) => {
        const toValidate = req.body.value
        const isUsername = req.body.isUsername === "true"
        const method = isUsername ? CredentialValidationHandler.validateUsername : CredentialValidationHandler.validatePassword
        
        method(toValidate).then(
            (response) => respond(res, response.status, response),
            (reason) => failureResponse(res, reason.message)
        )
    })
}

/**
 * Sets up routing for account-related requests.
 */
function routeAccountRequests() {
    app.route(Route.ACCOUNT)
    .post((req, res) => {
        const username = req.body.username
        const password = req.body.password

        AccountHandler.createAccount(username, password).then(
            (response) => respond(res, response.status, response),
            (reason) => failureResponse(res, reason.message)
        )
    })
}

/**
 * Sets up routing for session-related requests.
 */
function routeSessionRequests() {
    app.route(Route.SESSION)
    .post((req, res) => {
        const username = req.body.username
        const password = req.body.password
        const account = AccountHandler.getAccountWithCredentials(username, password)

        if (!account) {
            respond(res, StatusCode.UNAUTHORIZED, {success: false})
        } else {
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
        }
    })
    .delete((req, res) => {
        const sessionId = req.cookies[SessionHandler.cookieName]
        SessionHandler.endSession(sessionId).then(
            (response) => {
                if (response.cookieName ) {
                    res.clearCookie(response.cookieName)
                    response.cookieName = undefined
                }
                respond(res, response.status, response)
            },
            (reason) => failureResponse(res, reason.message)
        )
    })
}

/**
 * Sets up routing for studyguide-related requests.
 */
function routeStudyguideRequests() {
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
                respond(res, response.status, response)
            },
            (reason) => failureResponse(res, reason.message)
        )
    })
}

/**
 * Sets up routing for search-related requests.
 */
function routeSearchRequests() {
    app.get(`${Route.SEARCH}${Route.ACCOUNT}/:username`, (req, res) => {
        respond(res, 200, {})
    })
    const defaultSearch = ""
    const defaultPage = 0
    const defaultMaxSize = 50
    app.get(`${Route.SEARCH}${Route.STUDYGUIDE}{search=${defaultSearch}, page=${defaultPage}, max=${defaultMaxSize}}`, (req, res) => {
        const searchingUser = SessionHandler.getAccountFromRequest(req)
        const search = req.query.search
        const page = parseInt(req.query.page)
        const max = parseInt(req.query.max)
        StudyguideHandler.findStudyguides(searchingUser, search, page, max).then(
            (response) => {
                for (const guideObject of response.results) {
                    const creatorId = guideObject.creatorId
                    const creatorAccount = AccountHandler.getAccountWithId(creatorId)
                    guideObject.creatorUsername = creatorAccount != undefined ? creatorAccount.username() : undefined
                }
                respond(res, response.status, response)
            },
            (reason) => failureResponse(res, reason.message)
        )
    })
}

/**
 * Initializes the express application.
 */
function startApplication() {
    app = express()

    app.enable('trust proxy')
    app.use(cookieParser())
    app.use(helmet())
    app.use(express.json())
    app.use(express.urlencoded())

    routeCredentialValidation()
    routeAccountRequests()
    routeSessionRequests()
    routeStudyguideRequests()
    routeSearchRequests()
}

/**
 * Starts an express server with the given port and host.
 * 
 * @param {string} port The port
 * @param {string} host The host
 */
function startServer(port, host) {
    if (appServer != undefined) {
        shutdownServer()
    }

    appServer = app.listen(port, host, () => {
        console.log(`Server is alive on ${host}:${port}`)
    })
}

/**
 * Shuts down the express server if one exists.
 */
function shutdownServer() {
    if (appServer != undefined) {
        appServer.close(() => {
            console.log("Server has shut down")
        })
    }
}

const Application = Object.freeze({
    startApplication: startApplication,
})

const Server = Object.freeze({
    startServer: startServer,
    shutdownServer: shutdownServer
})

export { Application, Server }

