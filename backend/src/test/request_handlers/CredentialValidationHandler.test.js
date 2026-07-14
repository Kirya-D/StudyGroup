import { describe, expect, test } from "@jest/globals"
import { CredentialValidationHandler } from "../../main/request_handlers/CredentialValidationHandler.js"
import { StatusCode } from "../../main/utils/StatusCode.js"

describe("CredentialValidationHandler", () => {
    describe("validateUsername", () => {
        test("test when username is valid", async () => {
            const response = await CredentialValidationHandler.validateUsername("ValidUser123")

            expect(response.status).toBe(StatusCode.OK)
            expect(response.message).toBe(CredentialValidationHandler.VALIDATION_MESSAGE.VALID)
        })

        test("test when username is not a string", async () => {
            const response = await CredentialValidationHandler.validateUsername(42)

            expect(response.status).toBe(StatusCode.BAD_REQUEST)
            expect(response.message).toBe(CredentialValidationHandler.VALIDATION_MESSAGE.USERNAME_TYPE_MESSAGE)
        })

        test("test when username is too short", async () => {
            const response = await CredentialValidationHandler.validateUsername("short")

            expect(response.status).toBe(StatusCode.BAD_REQUEST)
            expect(response.message).toBe(CredentialValidationHandler.VALIDATION_MESSAGE.USERNAME_LENGTH_MESSAGE)
        })

        test("test when username is too long", async () => {
            const response = await CredentialValidationHandler.validateUsername("a".repeat(33))

            expect(response.status).toBe(StatusCode.BAD_REQUEST)
            expect(response.message).toBe(CredentialValidationHandler.VALIDATION_MESSAGE.USERNAME_LENGTH_MESSAGE)
        })

        test("test when username contains non-alphanumeric characters", async () => {
            const response = await CredentialValidationHandler.validateUsername("user-name")

            expect(response.status).toBe(StatusCode.BAD_REQUEST)
            expect(response.message).toBe(CredentialValidationHandler.VALIDATION_MESSAGE.USERNAME_ALPHANUMERIC_MESSAGE)
        })
    })

    describe("validatePassword", () => {
        test("test when password is valid", async () => {
            const response = await CredentialValidationHandler.validatePassword("ValidPass123")

            expect(response.status).toBe(StatusCode.OK)
            expect(response.message).toBe(CredentialValidationHandler.VALIDATION_MESSAGE.VALID)
        })

        test("test when password is not a string", async () => {
            const response = await CredentialValidationHandler.validatePassword(42)

            expect(response.status).toBe(StatusCode.BAD_REQUEST)
            expect(response.message).toBe(CredentialValidationHandler.VALIDATION_MESSAGE.PASSWORD_TYPE_MESSAGE)
        })

        test("test when password is too short", async () => {
            const response = await CredentialValidationHandler.validatePassword("short")

            expect(response.status).toBe(StatusCode.BAD_REQUEST)
            expect(response.message).toBe(CredentialValidationHandler.VALIDATION_MESSAGE.PASSWORD_LENGTH_MESSAGE)
        })

        test("test when password is too long", async () => {
            const response = await CredentialValidationHandler.validatePassword("a".repeat(33))

            expect(response.status).toBe(StatusCode.BAD_REQUEST)
            expect(response.message).toBe(CredentialValidationHandler.VALIDATION_MESSAGE.PASSWORD_LENGTH_MESSAGE)
        })

        test("test when password contains whitespace", async () => {
            const response = await CredentialValidationHandler.validatePassword("bad password")

            expect(response.status).toBe(StatusCode.BAD_REQUEST)
            expect(response.message).toBe(CredentialValidationHandler.VALIDATION_MESSAGE.PASSWORD_WHITESPACE_MESSAGE)
        })
    })
})