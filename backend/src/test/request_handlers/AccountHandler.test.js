import { describe, expect, test } from "@jest/globals"
import crypto from "crypto"
import filesystem from "fs"
import path from "path"
import { Database } from "../../main/model/Database.js"
import { AccountHandler } from "../../main/request_handlers/AccountHandler.js"
import { StatusCode } from "../../main/utils/StatusCode.js"

const dbConfig = process.env.TESTING_DB_URL
const setupPath = path.join("src/test/", "database_setup.sql")
const setupQuery = filesystem.readFileSync(setupPath, "utf-8")
const database = new Database()

function createUniqueUsername(prefix = "user") {
    return `${prefix}-${crypto.randomUUID()}`
}

function createNewAccount() {

}

describe("AccountHandler", () => {

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
            if (account != undefined) {
                expect(account.username()).toBe(username)
            }
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

    describe("PropogateAccountChangesToDatabase", () => {

        beforeEach(async () => {
            await database.connectToDatabase({ config: dbConfig, setupQuery: setupQuery })
            AccountHandler.clearStoredChanges()
        })

        afterEach(async () => {
            await database.disconnect()
        })

        test.each([
            [0],
            [1],
            [2]
        ])("When There Is %i New Accounts", async (numAccounts) => {
            for (let i = 0; i < numAccounts; i++) {
                await AccountHandler.createAccount(`NewAccount${i}`, "password")
            }

            await AccountHandler.propogateAccountChangesToDatabase(database)

            for (let i = 0; i < numAccounts; i++) {
                const account = await AccountHandler.getAccountWithUsername(`NewAccount${i}`)
                expect(account).toBeDefined()
            }
        })
    })
})