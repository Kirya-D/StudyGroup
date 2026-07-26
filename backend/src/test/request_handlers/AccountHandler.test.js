import { beforeEach, describe, expect, test } from "@jest/globals"
import crypto from "crypto"
import { AccountHandler } from "../../main/request_handlers/AccountHandler.js"
import { StatusCode } from "../../main/utils/StatusCode.js"
import { MockDatabase, resetMockDatabaseState } from "../MockDatabase.js"

function createUniqueUsername(prefix = "user") {
    return `${prefix}-${crypto.randomUUID()}`
}

describe("AccountHandler", () => {

    describe("GetAccountWithId", () => {
        test("When No Matching Id Exists", () => {
            const account = AccountHandler.getAccountWithId("does-not-exist")

            expect(account).toBeUndefined()
        })

        test("When Matching Id Exists", async () => {
            const username = createUniqueUsername("existing")
            await AccountHandler.createAccount(username, "password")

            const account = AccountHandler.getAccountWithUsername(username)
            const accountId = account.id()
            const accountFromId = AccountHandler.getAccountWithId(accountId)

            expect(accountFromId).toBeDefined()
        })
    })

    describe("GetAccountWithUsername", () => {
        test("When No Matching Username Exists", () => {
            const account = AccountHandler.getAccountWithUsername("does-not-exist")

            expect(account).toBeUndefined()
        })

        test("When Matching Username Exists", async () => {
            const username = createUniqueUsername("existing")
            await AccountHandler.createAccount(username, "password")

            const account = AccountHandler.getAccountWithUsername(username)

            expect(account).toBeDefined()
        })
    })

    describe("GetAccountWithCredentials", () => {
        test("When Credentials Do Not Match", async () => {
            const username = createUniqueUsername("bad-creds")
            await AccountHandler.createAccount(username, "password")

            const account = AccountHandler.getAccountWithCredentials(username, "wrong-password")

            expect(account).toBeUndefined()
        })

        test("When Credentials Match", async () => {
            const username = createUniqueUsername("good-creds")
            await AccountHandler.createAccount(username, "password")

            const account = AccountHandler.getAccountWithCredentials(username, "password")

            expect(account).toBeDefined()
            if (account != undefined) {
                expect(account.username()).toBe(username)
            }
        })
    })

    describe("CreateAccount", () => {
        test("When Successful", async () => {
            const username = createUniqueUsername("new")
            const response = await AccountHandler.createAccount(username, "password")

            expect(response.success).toBe(true)
            expect(response.status).toBe(StatusCode.CREATED)

            const account = AccountHandler.getAccountWithUsername(username)
            expect(account).toBeDefined()
            if (account != undefined) {
                expect(account.username()).toBe(username)
            }
        })

        test("When Username Is Already In Use", async () => {
            const username = createUniqueUsername("duplicate")
            await AccountHandler.createAccount(username, "password")

            const response = await AccountHandler.createAccount(username, "password")

            expect(response.success).toBe(false)
            expect(response.status).toBe(StatusCode.CONFLICT)
            expect(response.message).toContain("already in-use")
        })

        test("When Username Is Invalid", async () => {
            const response = await AccountHandler.createAccount(42, "password")

            expect(response.success).toBe(false)
            expect(response.status).toBe(StatusCode.BAD_REQUEST)
        })

        test("When Password Is Invalid", async () => {
            const response = await AccountHandler.createAccount("valid-user", 42)

            expect(response.success).toBe(false)
            expect(response.status).toBe(StatusCode.BAD_REQUEST)
        })
    })

    describe("LoadAccountsFromDatabase", () => {

        beforeEach(() => {
            resetMockDatabaseState()
        })

        test("When there is 1 account loaded", async () => {
            const id = "id-1"
            const username = "username"
            const password = "password"
            MockDatabase.createAccounts([{ id: id, username: username, password: password }])
            await AccountHandler.loadAccountsFromDatabase(MockDatabase)
            const associatedAccount = AccountHandler.getAccountWithUsername(username)

            expect(associatedAccount).toBeDefined()
            expect(associatedAccount.id()).toBe(id)
            expect(associatedAccount.username()).toBe(username)
            expect(associatedAccount.password()).toBe(password)
        })
    })

    describe("PropogateAccountChangesToDatabase", () => {

        beforeEach(() => {
            AccountHandler.clearStoredChanges()
            resetMockDatabaseState()
        })

        test.each([
            [0],
            [1],
            [2]
        ])("When There Is %i New Accounts", async (numAccounts) => {
            for (let i = 0; i < numAccounts; i++) {
                await AccountHandler.createAccount(`NewAccount${i}`, "password")
            }

            await AccountHandler.propogateAccountChangesToDatabase(MockDatabase)

            for (let i = 0; i < numAccounts; i++) {
                const account = await AccountHandler.getAccountWithUsername(`NewAccount${i}`)
                expect(account).toBeDefined()
            }
        })
    })
})