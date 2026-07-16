import crypto from "crypto"
import express from "express"
import { Account } from "../Account.js"
import { Primitives } from "../utils/Primitives.js"
import { StatusCode } from "../utils/StatusCode.js"

const sessionIdCookieName = "session_id"

/**
 * @type {Map<string, Account>}
 */
const sessionAccounts = new Map()

/**
 * Returns the account of the user who made the request based on the session_id from the request
 * @param {express.Request} request 
 * @returns The account of the user who made the request
 */
function getAccountFromRequest(request) {
    const sessionId = request.cookies[sessionIdCookieName]
    return sessionAccounts.get(sessionId)
}

/**
 * @param {boolean} toggle Create or Delete a session
 * @param {string} sessionId The id of the session to create or delete
 * @param {Account | undefined} account The account associated with the sessionId to create. Not necessary when toggle === false
 * 
 * @returns A response to the request
 */
function toggleSession(toggle, sessionId, account = undefined) {
    let success = false
    let status = undefined
    let message = ""

    if (typeof toggle !== Primitives.BOOLEAN) {
        status = StatusCode.INTERNAL_SERVER_ERROR
    }

    const missingNecessaryAccount = toggle && account == undefined
    if (typeof sessionId !== Primitives.STRING || missingNecessaryAccount) {
        status = StatusCode.BAD_REQUEST
        message = "Missing ID or Account"
    }

    if (status == undefined) {
        success = true
        if (toggle) {
            sessionAccounts.set(sessionId, account)
            status = StatusCode.CREATED
        } else {
            sessionAccounts.delete(sessionId)
            status = StatusCode.NO_CONTENT
        }
    }

    return {
        success: success,
        status: status,
        message: message
    }
}

/**
 * Attempts to create a new session for the given account and creates a sessionid cookie on the response if successful.
 * 
 * @param {express.Response} res The http response
 * @param {Account} account The account
 * 
 * @returns A response to the request
 */
async function startSession(account) {
    const sessionId = crypto.randomUUID()
    const requestResponse = toggleSession(true, sessionId, account)
    let cookieInfo = undefined

    if (requestResponse.success) {
        cookieInfo = {
            name: sessionIdCookieName,
            value: sessionId,
            options: {
                httpOnly: true,
                secure: false,
            }
        }
    }
    const fullResponse = {
        success: requestResponse.success,
        status: requestResponse.status,
        message: requestResponse.message,
        cookieInfo: cookieInfo
    }

    return fullResponse
}

/**
 * Attempts to end the session with the given id
 * 
 * @param {string} sessionId The session id to end
 * 
 * @returns A response to the request
 */
async function endSession(sessionId) {
    const requestResponse = toggleSession(false, sessionId)
    const fullResponse = {
        success: requestResponse.success,
        status: requestResponse.status,
        message: requestResponse.message,
        cookieName: sessionIdCookieName
    }

    return fullResponse
}

const SessionHandler = Object.freeze({
    cookieName: sessionIdCookieName,
    getAccountFromRequest: getAccountFromRequest,
    startSession: startSession,
    endSession: endSession
})

export { SessionHandler }

