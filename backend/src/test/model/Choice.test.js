import { describe, expect, test } from "@jest/globals"
import { Choice } from "../../main/model/Choice.js"

const validText = "Choice"
const validIsAnswer = true

describe("Choice", () => {

    describe("Constructor", () => {

        test.each([
            [1.2],
            [1],
            [new Object("Choice")],
            [true],
            [undefined],
            [null]
        ])("Throws when text isn't a string", (text) => {
            expect(() => new Choice(text, validIsAnswer))
                .toThrow("text must be a string")
        })

        test.each([
            [1.2],
            [1],
            [new Object(true)],
            ["true"],
            [undefined],
            [null]
        ])("Throws when isAnswer isn't a boolean", (isAnswer) => {
            expect(() => new Choice(validText, isAnswer))
                .toThrow("isAnswer must be a boolean")
        })

        test("When Successful", () => {
            const choice = new Choice(validText, validIsAnswer)

            expect(choice.text()).toBe(validText)
            expect(choice.isAnswer()).toBe(validIsAnswer)
        })
    })
})