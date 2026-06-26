import { afterAll, beforeEach, describe, expect, test } from "@jest/globals";
import filesystem from "fs";
import path from "path";
import { Account } from "../main/Account.js";
import { Choice } from "../main/Choice.js";
import { Database } from "../main/Database.js";
import { Question } from "../main/Question.js";
import { Studyguide } from "../main/Studyguide.js";

const dbConfig = process.env.TESTING_DB_URL
const defaultStudyguideParams = {
    title: "studyguide",
    description: "description",
    questions: new Set([
        new Question("Question", new Set([
            new Choice("Choice", true)
        ]))
    ]),
    creatorId: 1
}
const setupPath = path.join("src/test/", "database_setup.sql")
const setupQuery = filesystem.readFileSync(setupPath, "utf-8")
const database = new Database()

describe("Database", () => {

    beforeEach(async () => {
        await database.connectToDatabase({config: dbConfig, setupQuery: setupQuery})
    })

    afterAll(() => {
        database.disconnect()
    })

    describe("GetAllAccounts", () => {
        test("When No Accounts in Database", async () => {
            await database.connectToDatabase({config: dbConfig, setupQuery: "DELETE FROM Account"})
            const accountSet = await database.getAllAccounts()
            const accounts = Array.from(accountSet)

            expect(accounts.length).toBe(0)
        })

        test("When One Account in Database", async () => {
            const accountSet = await database.getAllAccounts()
            const accounts = Array.from(accountSet)
            const onlyAccount = accounts.at(0)

            expect(accounts.length).toBe(1)
            expect(onlyAccount.id()).toBe(1)
            expect(onlyAccount.username()).toBe("testUser")
            expect(onlyAccount.password()).toBe("password")
        })

        test("When Multiple Accounts in Database", async () => {
            const accounts = [new Account({id: 2, username: "testUser1", password: "password1"}), new Account({id: 3, username: "testUser2", password: "password2"})]
            accounts.forEach(async (account) => {
                const user = account.username()
                const pass = account.password()
                await database.createAccount(user, pass)
            })
            
            const accountSet = await database.getAllAccounts()
            const retrievedAccounts = Array.from(accountSet)

            expect(retrievedAccounts.length).toBe(3)
            retrievedAccounts.forEach((account, index) => {
                var expectedId = index + 1
                var expectedUsername = "testUser"
                var expectedPassword = "password"
                if (index != 0) {
                    expectedUsername = `${expectedUsername}${index}`
                    expectedPassword = `${expectedPassword}${index}`
                }
                expect(account.id()).toBe(expectedId)
                expect(account.username()).toBe(expectedUsername)
                expect(account.password()).toBe(expectedPassword)
            })
        })

        test.each([
            [false, false],
            [false, true],
            [true, false],
            [true, true]
        ])("When Account Has One Studyguide with Status'", async (favorited, downloaded) => {
            const guide = new Studyguide(defaultStudyguideParams)
            await database.upsertStudyguide(guide, favorited, downloaded)

            const accounts = Array.from(await database.getAllAccounts())
            const onlyAccount = accounts.at(0)
            const expectedFavoritesCount = favorited ? 1 : 0
            const expectedDownloadsCount = downloaded ? 1 : 0

            expect(accounts.length).toBe(1)
            expect(onlyAccount.favoritedStudyguides().size).toBe(expectedFavoritesCount)
            expect(onlyAccount.downloadedStudyguides().size).toBe(expectedDownloadsCount)
        })

        test.each([
            [false, false, false, false],
            [true, false, false, true],
            [false, true, true, false],
            [true, false, true, false],
            [false, true, false, true],
            [true, true, true, true],
        ])("When Account Has Multiple Studyguides with Status'", async (favorited1, downloaded1, favorited2, downloaded2) => {
            const guide1 = new Studyguide(defaultStudyguideParams)
            const guide2 = new Studyguide(defaultStudyguideParams)
            await database.upsertStudyguide(guide1, favorited1, downloaded1)
            await database.upsertStudyguide(guide2, favorited2, downloaded2)

            const accounts = Array.from(await database.getAllAccounts())
            const onlyAccount = accounts.at(0)
            const expectedFavoritesCount = (favorited1 ? 1 : 0) + (favorited2 ? 1 : 0)
            const expectedDownloadsCount = (downloaded1 ? 1 : 0) + (downloaded2 ? 1 : 0)

            expect(accounts.length).toBe(1)
            expect(onlyAccount.favoritedStudyguides().size).toBe(expectedFavoritesCount)
            expect(onlyAccount.downloadedStudyguides().size).toBe(expectedDownloadsCount)
        })

        test.each([
            [false, false],
            [false, true],
            [true, false],
            [true, true]
        ])("When Account Has Studyguides with Status' By Other Creator", async (favorited, downloaded) => {
            const otherGuideParams = {
                title:"Title",
                description:"Description",
                questions: new Set(),
                creatorId: 2
            }
            const guide = new Studyguide(otherGuideParams)
            await database.createAccount("fillerUser", "fillerPassword")
            await database.upsertStudyguide(guide, favorited, downloaded)

            const accounts = Array.from(await database.getAllAccounts())
            const expectedFavoritesCount = favorited ? 1 : 0
            const expectedDownloadsCount = downloaded ? 1 : 0

            expect(accounts.length).toBe(2)
            accounts.forEach(account => {
                if (account.id() == 2) {
                    expect(account.favoritedStudyguides().size).toBe(expectedFavoritesCount)
                    expect(account.downloadedStudyguides().size).toBe(expectedDownloadsCount)
                }
            })
        })
    })

    describe("GetStudyguidesWithSubstring", () => {

        let placeholderSearch = "filler search"
        let page = 0;
        
        beforeEach(() => {
            page = 0
        })

        test.each([
            [null],
            [undefined],
            [10],
            [10.53],
            [true]
        ])("Throws when search is not a string", async (search) => {
            await expect(database.getStudyguidesWithSubstring(search, page)).rejects.toThrow("search must be a string")
        })

        test.each([
            [null],
            [undefined],
            ["10"],
            [10.53],
            [true]
        ])("Throws when pageNum is not an int", async (pageNum) => {
            await expect(database.getStudyguidesWithSubstring(placeholderSearch, pageNum)).rejects.toThrow("pageNum must be an int")
        })

        test.each([
            [null],
            ["10"],
            [10.53],
            [true]
        ])("Throws when maxResultCount is not an int", async (maxResultCount) => {
            await expect(database.getStudyguidesWithSubstring(placeholderSearch, page, maxResultCount)).rejects.toThrow("maxResultCount must be an int")
        })
        
        test("When no studyguides matching criteria", async () => {
            const guides = await database.getStudyguidesWithSubstring("Nothing matches this", page)
            
            expect(guides.size).toBe(0)
        })
        
        test("When one studyguide matches criteria", async () => {
            const guide = new Studyguide(defaultStudyguideParams)

            await database.upsertStudyguide(guide)
            const guides = await database.getStudyguidesWithSubstring("testUser", page)
            
            expect(guides.size).toBe(1)
        })
        
        test("When multiple studyguides matches criteria", async () => {
            const guide1 = new Studyguide(defaultStudyguideParams)
            const guide2 = new Studyguide(defaultStudyguideParams)
            const guide3 = new Studyguide(defaultStudyguideParams)

            await database.upsertStudyguide(guide1)
            await database.upsertStudyguide(guide2)
            await database.upsertStudyguide(guide3)
            const guides = await database.getStudyguidesWithSubstring("testUser", page)

            expect(guides.size).toBe(3)
        })

        test("When more studyguides than maxResultCount matches criteria", async () => {
            const maxResultCount = 50
            for (let i = 0; i < maxResultCount + 5; i++) {
                const guide = new Studyguide(defaultStudyguideParams)
                await database.upsertStudyguide(guide)
            }

            const guides = await database.getStudyguidesWithSubstring("testUser", page, maxResultCount)

            expect(guides.size).toBe(maxResultCount)
        })

        test("When requesting a later 'page'", async () => {
            page = 1
            const maxResultCount = 50
            for (let i = 0; i < maxResultCount + 5; i++) {
                const guide = new Studyguide(defaultStudyguideParams)
                await database.upsertStudyguide(guide)
            }

            const guides = await database.getStudyguidesWithSubstring("testUser", page, maxResultCount)

            expect(guides.size).toBe(5)
        })
    })

    describe("CreateAccount", () => {

        test.each([
            [null],
            [10],
            [15.25],
            [undefined],
            [true]
        ])("Throws when username isn't a string", async (username) => {
            const password = "validPassword"
            await expect(database.createAccount(username, password)).rejects.toThrow("username must be a string")
        })

        test.each([
            [null],
            [10],
            [15.25],
            [undefined],
            [true]
        ])("Throws when password isn't a string", async (password) => {
            const username = "validUsername"
            await expect(database.createAccount(username, password)).rejects.toThrow("password must be a string")
        })
    })

    describe("UpsertStudyguide", () => {

        let page = 0;

        test.each([
            [15.25],
            ["10"],
            [null],
            [undefined],
            [new String("String Object")],
            [true]
        ])("Throws when studyguide isn't a Studyguide object", async (guide) => {
            await expect(database.upsertStudyguide(guide)).rejects.toThrow("studyguide must be a Studyguide object")
        })

        test.each([
            [15.25],
            ["10"],
            [null],
            [new String("String Object")]
        ])("Throws when favorited isn't a boolean", async (favorited) => {
            const guide = new Studyguide(defaultStudyguideParams)
            await expect(database.upsertStudyguide(guide, favorited)).rejects.toThrow("favorited must be an int")
        })

        test.each([
            [15.25],
            ["10"],
            [null],
            [new String("String Object")]
        ])("Throws when downloaded isn't a boolean", async (downloaded) => {
            const guide = new Studyguide(defaultStudyguideParams)
            const favorited = false
            await expect(database.upsertStudyguide(guide, favorited, downloaded)).rejects.toThrow("downloaded must be an int")
        })

        test("Test when one insert", async () => {
            const guide = new Studyguide(defaultStudyguideParams)
            const testingUsername = "testUser"

            const returnedId = await database.upsertStudyguide(guide)

            const guides = Array.from(await database.getStudyguidesWithSubstring(testingUsername, page))

            expect(guides.length).toBe(1)
            expect(guides.at(0).id()).toBe(returnedId)
            expect(guides.at(0)).toEqual(guide)
        })

        test("Test when multiple inserts", async () => {
            const guide1 = new Studyguide(defaultStudyguideParams)
            const guide2 = new Studyguide(defaultStudyguideParams)
            const guide3 = new Studyguide(defaultStudyguideParams)
            const testingUsername = "testUser"

            const returnedId1 = await database.upsertStudyguide(guide1)
            const returnedId2 = await database.upsertStudyguide(guide2)
            const returnedId3 = await database.upsertStudyguide(guide3)

            const guides = Array.from(await database.getStudyguidesWithSubstring(testingUsername, page))

            expect(guides.length).toBe(3)
            expect(guides.at(0).id()).toBe(returnedId1)
            expect(guides.at(0)).toEqual(guide1)
            expect(guides.at(1).id()).toBe(returnedId2)
            expect(guides.at(1)).toEqual(guide2)
            expect(guides.at(2).id()).toBe(returnedId3)
            expect(guides.at(2)).toEqual(guide3)
        })

        test("Test when one update", async () => {
            const originalGuide = new Studyguide(defaultStudyguideParams)
            const updatedGuide = new Studyguide({
                id: 1,
                title: "Better title",
                description: "Longer and more descriptive description",
                questions: new Set(),
                creatorId: 1
            })
            const testingUsername = "testUser"

            const insertedId = await database.upsertStudyguide(originalGuide)
            const updatedId = await database.upsertStudyguide(updatedGuide)

            const guides = Array.from(await database.getStudyguidesWithSubstring(testingUsername, page))

            expect(guides.length).toBe(1)
            const guideIdExpectation = expect(guides.at(0).id())
            guideIdExpectation.toBe(insertedId)
            guideIdExpectation.toBe(updatedId)
            expect(guides.at(0)).toEqual(updatedGuide)
        })

        test("Test when multiple updates", async () => {
            const originalGuide = new Studyguide(defaultStudyguideParams)
            const updatedGuide = new Studyguide({
                id: 1,
                title: "Better title",
                description: "Longer and more descriptive description",
                questions: new Set(),
                creatorId: 1
            })
            const secondUpdatedGuide = new Studyguide({
                id: 1,
                title: "Different title",
                description: "Shorter desc",
                questions: new Set(),
                creatorId: 1
            })
            const testingUsername = "testUser"

            const insertedId = await database.upsertStudyguide(originalGuide)
            let updatedId = await database.upsertStudyguide(updatedGuide)
            updatedId = await database.upsertStudyguide(secondUpdatedGuide)

            const guides = Array.from(await database.getStudyguidesWithSubstring(testingUsername, page))

            expect(guides.length).toBe(1)
            const guideIdExpectation = expect(guides.at(0).id())
            guideIdExpectation.toBe(insertedId)
            guideIdExpectation.toBe(updatedId)
            expect(guides.at(0)).toEqual(secondUpdatedGuide)
        })
    })

    describe("DeleteStudyguide", () => {

        let page = 0;

        test.each([
            [null],
            [undefined],
            [22.23],
            ["fifty-two"],
            [true]
        ])("Throws when studyguideId isn't an int", async (studyguideId) => {
            await expect(database.deleteStudyguide(studyguideId, 1)).rejects.toThrow("studyguideId must be an int")
        })

        test.each([
            [null],
            [undefined],
            [22.23],
            ["fifty-two"]
        ])("Throws when accountId isn't an int", async (accountId) => {
            await expect(database.deleteStudyguide(1, accountId)).rejects.toThrow("accountId must be an int")
        })

        test("Test when no studyguides to delete", async () => {
            await database.deleteStudyguide(1, 1)
            const guides = await database.getStudyguidesWithSubstring("testUser", page)
            
            expect(guides.size).toBe(0)
        })

        test("Test when studyguide isn't by account with accountId", async () => {
            const guide = new Studyguide(defaultStudyguideParams)
            const guideId = 1
            const accountId = 2

            await database.upsertStudyguide(guide)
            await database.deleteStudyguide(guideId, accountId)
            const guides = Array.from(await database.getStudyguidesWithSubstring("testUser", page))
            
            expect(guides.length).toBe(1)
            expect(guides.at(0)).toEqual(guide)
        })

        test("Test when studyguide is by account with accountId", async () => {
            const guide = new Studyguide(defaultStudyguideParams)
            const guideId = 1
            const accountId = 1

            await database.upsertStudyguide(guide)
            await database.deleteStudyguide(guideId, accountId)
            const guides = await database.getStudyguidesWithSubstring("testUser", page)
            
            expect(guides.size).toBe(0)
        })
    })

    describe("UpsertStudyguideStatus", () => {

        test.each([
            [null],
            [undefined],
            [22.23],
            ["fifty-two"],
            [true]
        ])("Throws when accountId isn't an int", async (accountId) => {
            const studyguideId = 1
            const favorited = false
            const downloaded = false
            await expect(database.upsertStudyguideStatus(accountId, studyguideId, favorited, downloaded))
                .rejects.toThrow("accountId must be an int")
        })

        test.each([
            [null],
            [undefined],
            [22.23],
            ["fifty-two"],
            [true]
        ])("Throws when studyguideId isn't an int", async (studyguideId) => {
            const accountId = 1
            const favorited = false
            const downloaded = false
            await expect(database.upsertStudyguideStatus(accountId, studyguideId, favorited, downloaded))
                .rejects.toThrow("studyguideId must be an int")
        })

        test.each([
            [null],
            [undefined],
            [22.23],
            [1],
            ["fifty-two"]
        ])("Throws when favorited isn't a boolean", async (favorited) => {
            const accountId = 1
            const studyguideId = 1
            const downloaded = false
            await expect(database.upsertStudyguideStatus(accountId, studyguideId, favorited, downloaded))
                .rejects.toThrow("favorited must be an int")
        })

        test.each([
            [null],
            [undefined],
            [22.23],
            [1],
            ["fifty-two"]
        ])("Throws when downloaded isn't a boolean", async (downloaded) => {
            const accountId = 1
            const studyguideId = 1
            const favorited = false
            await expect(database.upsertStudyguideStatus(accountId, studyguideId, favorited, downloaded))
                .rejects.toThrow("downloaded must be an int")
        })
    })
})