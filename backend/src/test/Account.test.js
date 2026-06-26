import { describe, expect, test } from "@jest/globals";
import { Account } from "../main/Account.js";

const validId = -1
const validUsername = "user"
const validPassword = "password"
const validAccountParams = {
    id: validId,
    username: validUsername,
    password: validPassword
}

describe("Account", () => {

    describe("Constructor", () => {

        test.each([
            [null],
            [undefined]
        ])("When id is null/undefined", (id) => {
            const account = new Account({ id: id, username: validUsername, password: validPassword })
            
            expect(account.id()).toBe(-1)
        })

        test.each([
            [1.2],
            ["1"],
            [new Object("1")],
            [true]
        ])("Throws when id isn't an int", (id) => {
            expect(() => new Account({ id: id, username: validUsername, password: validPassword }))
                .toThrow("id must be an integer")
        })

        test.each([
            [1.2],
            [1],
            [new Object("1")],
            [true],
            [undefined],
            [null]
        ])("Throws when username isn't a string", (username) => {
            expect(() => new Account({ id: validId, username: username, password: validPassword }))
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
            expect(() => new Account({ id: validId, username: validUsername, password: password }))
                .toThrow("password must be a string")
        })

        test("When Successful", () => {
            const account = new Account(validAccountParams)

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
            ["1"],
        ])("Throws when id isn't an int", (id) => {
            const account = new Account(validAccountParams)

            expect(() => account.favorite(id)).toThrow("id must be an int")
        })

        test("When Successful", () => {
            const account = new Account(validAccountParams)
            const id = 1
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
            ["1"],
        ])("Throws when id isn't an int", (id) => {
            const account = new Account(validAccountParams)

            expect(() => account.unfavorite(id)).toThrow("id must be an int")
        })

        test("When Successful", () => {
            const account = new Account(validAccountParams)
            const id = 1
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
            ["1"],
        ])("Throws when id isn't an int", (id) => {
            const account = new Account(validAccountParams)

            expect(() => account.download(id)).toThrow("id must be an int")
        })

        test("When Successful", () => {
            const account = new Account(validAccountParams)
            const id = 1
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
            ["1"],
        ])("Throws when id isn't an int", (id) => {
            const account = new Account(validAccountParams)

            expect(() => account.undownload(id)).toThrow("id must be an int")
        })

        test("When Successful", () => {
            const account = new Account(validAccountParams)
            const id = 1
            account.download(id)
            account.undownload(id)

            const downloads = account.downloadedStudyguides()

            expect(downloads.size).toBe(0)
            expect(downloads.has(id)).toBe(false)
        })
    })
})