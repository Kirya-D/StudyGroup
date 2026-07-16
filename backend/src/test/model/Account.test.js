import { describe, expect, test } from "@jest/globals";
import { Account } from "../../main/model/Account.js";

const validId = "1"
const validUsername = "user"
const validPassword = "password"

describe("Account", () => {

    describe("Constructor", () => {

        test.each([
            [1.2],
            [1],
            [true]
        ])("Throws when id isn't a string", (id) => {
            expect(() => new Account(id, validUsername, validPassword))
                .toThrow("id must be a string")
        })

        test.each([
            [1.2],
            [1],
            [new Object("1")],
            [true],
            [undefined],
            [null]
        ])("Throws when username isn't a string", (username) => {
            expect(() => new Account(validId, username, validPassword))
                .toThrow("username must be a string")
        })

        test.each([
            [1.2],
            [1],
            [new Object("1")],
            [true],
            [undefined],
            [null]
        ])("Throws when password isn't a string", (password) => {
            expect(() => new Account(validId, validUsername, password))
                .toThrow("password must be a string")
        })

        test("When Successful", () => {
            const account = new Account(validId, validUsername, validPassword)

            expect(account.id()).toBe(validId)
            expect(account.username()).toBe(validUsername)
            expect(account.password()).toBe(validPassword)
            expect(account.favoritedStudyguides().size).toBe(0)
            expect(account.downloadedStudyguides().size).toBe(0)
        })
    })

    describe("Favorite", () => {

        test.each([
            [undefined],
            [null],
            [1.2],
            [1],
        ])("Throws when id isn't a string", (id) => {
            const account = new Account(validId, validUsername, validPassword)

            expect(() => account.favorite(id)).toThrow("id must be a string")
        })

        test("When Successful", () => {
            const account = new Account(validId, validUsername, validPassword)
            const id = "1"
            account.favorite(id)

            const favorites = account.favoritedStudyguides()

            expect(favorites.size).toBe(1)
            expect(favorites.has(id)).toBe(true)
        })
    })

    describe("Unfavorite", () => {

        test.each([
            [undefined],
            [null],
            [1.2],
            [1],
        ])("Throws when id isn't a string", (id) => {
            const account = new Account(validId, validUsername, validPassword)

            expect(() => account.unfavorite(id)).toThrow("id must be a string")
        })

        test("When Successful", () => {
            const account = new Account(validId, validUsername, validPassword)
            const id = "1"
            account.favorite(id)
            account.unfavorite(id)

            const favorites = account.favoritedStudyguides()

            expect(favorites.size).toBe(0)
            expect(favorites.has(id)).toBe(false)
        })
    })

    describe("Download", () => {

        test.each([
            [undefined],
            [null],
            [1.2],
            [1],
        ])("Throws when id isn't a string", (id) => {
            const account = new Account(validId, validUsername, validPassword)

            expect(() => account.download(id)).toThrow("id must be a string")
        })

        test("When Successful", () => {
            const account = new Account(validId, validUsername, validPassword)
            const id = "1"
            account.download(id)

            const downloads = account.downloadedStudyguides()

            expect(downloads.size).toBe(1)
            expect(downloads.has(id)).toBe(true)
        })
    })

    describe("Undownload", () => {

        test.each([
            [undefined],
            [null],
            [1.2],
            [1],
        ])("Throws when id isn't a string", (id) => {
            const account = new Account(validId, validUsername, validPassword)

            expect(() => account.undownload(id)).toThrow("id must be a string")
        })

        test("When Successful", () => {
            const account = new Account(validId, validUsername, validPassword)
            const id = "1"
            account.download(id)
            account.undownload(id)

            const downloads = account.downloadedStudyguides()

            expect(downloads.size).toBe(0)
            expect(downloads.has(id)).toBe(false)
        })
    })
})