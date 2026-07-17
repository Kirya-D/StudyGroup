import { afterEach, beforeEach, describe, expect, test } from "@jest/globals";
import filesystem from "fs";
import path from "path";
import { Account } from "../../main/model/Account.js";
import { Choice } from "../../main/model/Choice.js";
import { Database } from "../../main/model/Database.js";
import { Question } from "../../main/model/Question.js";
import { Studyguide } from "../../main/model/Studyguide.js";

const dbConfig = process.env.TESTING_DB_URL
const validId = "1"
const validTitle = "studyguide"
const validDescription = "description"
const validQuestions = new Set([
    new Question("Question", new Set([
        new Choice("Choice", true)
    ]))
])
const validCreatorId = "1"
const setupPath = path.join("src/test/", "database_setup.sql")
const setupQuery = filesystem.readFileSync(setupPath, "utf-8")
const database = new Database()

describe("Database", () => {

    beforeEach(async () => {
        await database.connectToDatabase({ config: dbConfig, setupQuery: setupQuery })
    })

    afterEach(() => {
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
            expect(onlyAccount.id()).toBe(validCreatorId)
            expect(onlyAccount.username()).toBe("testUser")
            expect(onlyAccount.password()).toBe("password")
        })

        test("When Multiple Accounts in Database", async () => {
            const accounts = [new Account("2", "testUser1", "password1"), new Account("3", "testUser2", "password2")]
            accounts.forEach(async (account) => {
                const id = account.id()
                const user = account.username()
                const pass = account.password()
                await database.createAccounts([{id: id, username: user, password: pass}])
            })
            
            const accountSet = await database.getAllAccounts()
            const retrievedAccounts = Array.from(accountSet)

            expect(retrievedAccounts.length).toBe(3)
            retrievedAccounts.forEach((account, index) => {
                let expectedId = index + 1
                let expectedUsername = "testUser"
                let expectedPassword = "password"
                if (index != 0) {
                    expectedUsername = `${expectedUsername}${index}`
                    expectedPassword = `${expectedPassword}${index}`
                }
                expect(account.id()).toBe(expectedId.toString())
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
            const guide = new Studyguide(validId, validTitle, validDescription, validQuestions, validCreatorId)
            await database.upsertStudyguides([guide])
            await database.upsertStudyguideStatus([{
                accountId: validCreatorId, studyguideId: validId, favorited: favorited, downloaded: downloaded
            }])

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
            const guide1 = new Studyguide(validId, validTitle, validDescription, validQuestions, validCreatorId)
            const guide2 = new Studyguide("2", validTitle, validDescription, validQuestions, validCreatorId)
            await database.upsertStudyguides([guide1, guide2])
            await database.upsertStudyguideStatus([
                {
                    accountId: validCreatorId,
                    studyguideId: validId,
                    favorited: favorited1,
                    downloaded: downloaded1
                },
                {
                    accountId: validCreatorId,
                    studyguideId: "2",
                    favorited: favorited2,
                    downloaded: downloaded2
                }
            ])

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
            const guide = new Studyguide(validId, validTitle, validDescription, validQuestions, "2")
            await database.createAccounts([{id: "2", username: "fillerUser", password: "fillerPassword"}])
            await database.upsertStudyguides([guide])
            await database.upsertStudyguideStatus([{
                accountId: validCreatorId,
                studyguideId: guide.id(),
                favorited: favorited,
                downloaded: downloaded
            }])

            const accounts = Array.from(await database.getAllAccounts())
            const expectedFavoritesCount = favorited ? 1 : 0
            const expectedDownloadsCount = downloaded ? 1 : 0

            expect(accounts.length).toBe(2)
            accounts.forEach(account => {
                if (account.id() == validCreatorId) {
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
            const guide = new Studyguide(validId, validTitle, validDescription, validQuestions, validCreatorId)

            await database.upsertStudyguides([guide])
            const guides = await database.getStudyguidesWithSubstring(validTitle, page)
            
            expect(guides.size).toBe(1)
        })
        
        test("When multiple studyguides matches criteria", async () => {
            const guide1 = new Studyguide(validId, validTitle, validDescription, validQuestions, validCreatorId)
            const guide2 = new Studyguide("2", validTitle, validDescription, validQuestions, validCreatorId)
            const guide3 = new Studyguide("3", validTitle, validDescription, validQuestions, validCreatorId)

            await database.upsertStudyguides([guide1, guide2, guide3])
            const guides = await database.getStudyguidesWithSubstring(validTitle, page)

            expect(guides.size).toBe(3)
        })

        test("When more studyguides than maxResultCount matches criteria", async () => {
            const maxResultCount = 50
            const guidesArray = []
            for (let i = 0; i < maxResultCount + 5; i++) {
                const guide = new Studyguide(`${i}`, validTitle, validDescription, validQuestions, validCreatorId)
                guidesArray.push(guide)
            }
            await database.upsertStudyguides(guidesArray)

            const searchedGuides = await database.getStudyguidesWithSubstring(validTitle, page, maxResultCount)

            expect(searchedGuides.size).toBe(maxResultCount)
        })

        test("When requesting a later 'page'", async () => {
            page = 1
            const maxResultCount = 50
            const guides = []
            for (let i = 0; i < maxResultCount + 5; i++) {
                const guide = new Studyguide(`${i}`, validTitle, validDescription, validQuestions, validCreatorId)
                guides.push(guide)
            }
            await database.upsertStudyguides(guides)

            const searchedGuides = await database.getStudyguidesWithSubstring(validTitle, page, maxResultCount)

            expect(searchedGuides.size).toBe(5)
        })
    })

    describe("CreateAccount", () => {

        test.each([
            [null],
            [10],
            [15.25],
            [undefined],
            [true]
        ])("Throws when id isn't a string", async (info) => {
            await expect(database.createAccounts(info)).rejects.toThrow("accountsInfo must be an array")
        })

        test.each([
            [null],
            [10],
            [15.25],
            [undefined],
            [true]
        ])("Throws when id isn't a string", async (id) => {
            const username = "validUsername"
            const password = "validPassword"
            await expect(database.createAccounts([{id: id, username: username, password: password}])).rejects.toThrow("id must be a string")
        })

        test.each([
            [null],
            [10],
            [15.25],
            [undefined],
            [true]
        ])("Throws when username isn't a string", async (username) => {
            const id = "id"
            const password = "validPassword"
            await expect(database.createAccounts([{id: id, username: username, password: password}])).rejects.toThrow("username must be a string")
        })

        test.each([
            [null],
            [10],
            [15.25],
            [undefined],
            [true]
        ])("Throws when password isn't a string", async (password) => {
            const id = "id"
            const username = "validUsername"
            await expect(database.createAccounts([{id: id, username: username, password: password}])).rejects.toThrow("password must be a string")
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
            await expect(database.upsertStudyguides([guide])).rejects.toThrow("studyguide must be a Studyguide object")
        })

        test("Test when one insert", async () => {
            const guide = new Studyguide(validId, validTitle, validDescription, validQuestions, validCreatorId)

            await database.upsertStudyguides([guide])

            const guides = Array.from(await database.getStudyguidesWithSubstring(validTitle, page))

            expect(guides.length).toBe(1)
            expect(guides.at(0)).toEqual(guide)
        })

        test("Test when multiple inserts", async () => {
            const guide1 = new Studyguide(validId, validTitle, validDescription, validQuestions, validCreatorId)
            const guide2 = new Studyguide("2", validTitle, validDescription, validQuestions, validCreatorId)
            const guide3 = new Studyguide("3", validTitle, validDescription, validQuestions, validCreatorId)

            await database.upsertStudyguides([guide1, guide2, guide3])

            const guides = Array.from(await database.getStudyguidesWithSubstring(validTitle, page))

            expect(guides.length).toBe(3)
            expect(guides.at(0)).toEqual(guide3)
            expect(guides.at(1)).toEqual(guide2)
            expect(guides.at(2)).toEqual(guide1)
        })

        test("Test when one update", async () => {
            const originalGuide = new Studyguide(validId, validTitle, validDescription, validQuestions, validCreatorId)
            const updatedGuide = new Studyguide(
                validId,
                "Better title",
                "Longer and more descriptive description",
                new Set(),
                validCreatorId
            )

            await database.upsertStudyguides([originalGuide])
            await database.upsertStudyguides([updatedGuide])

            const guides = Array.from(await database.getStudyguidesWithSubstring(updatedGuide.title(), page))

            expect(guides.length).toBe(1)
            const guideIdExpectation = expect(guides.at(0).id())
            expect(guides.at(0)).toEqual(updatedGuide)
        })

        test("Test when multiple updates", async () => {
            const originalGuide = new Studyguide(validId, validTitle, validDescription, validQuestions, validCreatorId)
            const updatedGuide = new Studyguide(
                validId,
                "Better title",
                "Longer and more descriptive description",
                new Set(),
                validCreatorId
            )
            const secondUpdatedGuide = new Studyguide(
                validId,
                "Different title",
                "Shorter desc",
                new Set(),
                validCreatorId
            )

            await database.upsertStudyguides([originalGuide])
            await database.upsertStudyguides([updatedGuide])
            await database.upsertStudyguides([secondUpdatedGuide])

            const guides = Array.from(await database.getStudyguidesWithSubstring("desc", page))

            expect(guides.length).toBe(1)
            const guideIdExpectation = expect(guides.at(0).id())
            expect(guides.at(0)).toEqual(secondUpdatedGuide)
        })
    })

    describe("DeleteStudyguide", () => {

        let page = 0;

        test.each([
            [null],
            [undefined],
            [22.23],
            [52],
            [true],
            ["hello"]
        ])("Throws when studyguideIds isn't an array", async (studyguideIds) => {
            await expect(database.deleteStudyguides(studyguideIds)).rejects.toThrow("studyguideIds must be an array")
        })

        test.each([
            [null],
            [undefined],
            [22.23],
            [52],
            [true]
        ])("Throws when studyguideId isn't a string", async (studyguideId) => {
            await expect(database.deleteStudyguides([studyguideId])).rejects.toThrow("id must be a string")
        })

        test("Test when no studyguides to delete", async () => {
            await database.deleteStudyguides([])
            const guides = await database.getStudyguidesWithSubstring("anything", page)
            
            expect(guides.size).toBe(0)
        })

        test("Test when one studyguide id", async () => {
            const guide = new Studyguide(validId, validTitle, validDescription, validQuestions, validCreatorId)
            const guideId = validId

            await database.upsertStudyguides([guide])
            await database.deleteStudyguides([guideId])
            const guides = await database.getStudyguidesWithSubstring(validTitle, page)
            
            expect(guides.size).toBe(0)
        })

        test("Test when multiple studyguide ids", async () => {
            const guide = new Studyguide(validId, validTitle, validDescription, validQuestions, validCreatorId)
            const guideId1 = validId
            const guideId2 = "2"
            const guideId3 = "3"

            await database.upsertStudyguides([guide])
            await database.deleteStudyguides([guideId1, guideId2, guideId3])
            const guides = await database.getStudyguidesWithSubstring(validTitle, page)
            
            expect(guides.size).toBe(0)
        })
    })

    describe("UpsertStudyguideStatus", () => {
        const validAccountId = "1"
        const validStudyguideId = "1"
        const validFavorited = false
        const validDownloaded = false

        test.each([
            [null],
            [undefined],
            [22.23],
            [20],
            [true]
        ])("Throws when accountId isn't a string", async (accountId) => {
            await expect(database.upsertStudyguideStatus([{
                accountId: accountId,
                studyguideId: validStudyguideId,
                favorited: validFavorited,
                downloaded: validDownloaded
            }])).rejects.toThrow("accountId must be a string")
        })

        test.each([
            [null],
            [undefined],
            [22.23],
            [20],
            [true]
        ])("Throws when studyguideId isn't a string", async (studyguideId) => {
            await expect(database.upsertStudyguideStatus([{
                accountId: validAccountId,
                studyguideId: studyguideId,
                favorited: validFavorited,
                downloaded: validDownloaded
            }])).rejects.toThrow("studyguideId must be a string")
        })

        test.each([
            [null],
            [undefined],
            [22.23],
            [1],
            ["fifty-two"]
        ])("Throws when favorited isn't a boolean", async (favorited) => {
            await expect(database.upsertStudyguideStatus([{
                accountId: validAccountId,
                studyguideId: validStudyguideId,
                favorited: favorited,
                downloaded: validDownloaded
            }])).rejects.toThrow("favorited must be an int")
        })

        test.each([
            [null],
            [undefined],
            [22.23],
            [1],
            ["fifty-two"]
        ])("Throws when downloaded isn't a boolean", async (downloaded) => {
            await expect(database.upsertStudyguideStatus([{
                accountId: validAccountId,
                studyguideId: validStudyguideId,
                favorited: validFavorited,
                downloaded: downloaded
            }])).rejects.toThrow("downloaded must be an int")
        })
    })
})