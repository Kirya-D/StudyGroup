import { describe, expect, test } from "@jest/globals"
import crypto from "crypto"
import { Account } from "../../main/model/Account.js"
import { SessionHandler } from "../../main/request_handlers/SessionHandler.js"
import { StatusCode } from "../../main/utils/StatusCode.js"

function createAccount() {
    const id = crypto.randomUUID()
    return new Account(id, `user-${id}`, "password")
}

describe("SessionHandler", () => {

    describe("GetAccountFromRequest", () => {
        test("When No Matching Session Cookie", () => {
            const request = { cookies: {} }

            expect(SessionHandler.getAccountFromRequest(request)).toBeUndefined()
        })

        test("When Matching Session Cookie Exists", async () => {
            const account = createAccount()
            const startResponse = await SessionHandler.startSession(account)

            const request = {
                cookies: {
                    [SessionHandler.cookieName]: startResponse.cookieInfo.value
                }
            }

            expect(SessionHandler.getAccountFromRequest(request)).toBe(account)
        })
    })

    describe("StartSession", () => {
        test("When Successful", async () => {
            const account = createAccount()
            const response = await SessionHandler.startSession(account)

            expect(response.success).toBe(true)
            expect(response.status).toBe(StatusCode.CREATED)
            expect(response.cookieInfo).toEqual(
                expect.objectContaining({
                    name: SessionHandler.cookieName,
                    value: expect.any(String),
                    options: expect.objectContaining({
                        httpOnly: true,
                        secure: false
                    })
                })
            )

            const lookup = SessionHandler.getAccountFromRequest({
                cookies: { [SessionHandler.cookieName]: response.cookieInfo.value }
            })

            expect(lookup).toBe(account)
        })

        test("When Account Is Missing", async () => {
            const response = await SessionHandler.startSession(undefined)

            expect(response.success).toBe(false)
            expect(response.status).toBe(StatusCode.BAD_REQUEST)
            expect(response.cookieInfo).toBeUndefined()
        })
    })

    describe("EndSession", () => {
        test("When Successful", async () => {
            const account = createAccount()
            const startResponse = await SessionHandler.startSession(account)

            const endResponse = await SessionHandler.endSession(startResponse.cookieInfo.value)

            expect(endResponse.success).toBe(true)
            expect(endResponse.status).toBe(StatusCode.NO_CONTENT)
            expect(endResponse.cookieName).toBe(SessionHandler.cookieName)

            const lookup = SessionHandler.getAccountFromRequest({
                cookies: { [SessionHandler.cookieName]: startResponse.cookieInfo.value }
            })

            expect(lookup).toBeUndefined()
        })

        test("When Session ID Is Invalid", async () => {
            const response = await SessionHandler.endSession(123)

            expect(response.success).toBe(false)
            expect(response.status).toBe(StatusCode.BAD_REQUEST)
            expect(response.cookieName).toBe(SessionHandler.cookieName)
        })
    })
})