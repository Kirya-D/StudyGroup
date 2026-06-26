import { describe, expect, test } from "@jest/globals"
import { Question } from "../main/Question.js"
import { Choice } from "../main/Choice.js"

const validText = "Question?"
const validChoices = new Set([
    new Choice("Choice", true)
])

describe("Question", () => {

    describe("Constructor", () => {

        test.each([
            [1.2],
            [1],
            [new Object("Question?")],
            [true],
            [undefined],
            [null]
        ])("Throws when text isn't a string", (text) => {
            expect(() => new Question(text, validChoices))
                .toThrow("text must be a string")
        })

        test.each([
            [undefined],
            [null],
            [1],
            [1.2],
            ["choices"],
            [true],
            [new Object()]
        ])("Throws when choices isn't a Choice set", (choices) => {
            expect(() => new Question(validText, choices))
                .toThrow("choices must be a Choice set")
        })

        test.each([
            [new Set([1])],
            [new Set(["choice"])],
            [new Set([{}])],
            [new Set([true])]
        ])("Throws when all choices aren't Choice objects", (choices) => {
            expect(() => new Question(validText, choices))
                .toThrow("all choices must be a Choice object")
        })

        test("When Successful", () => {
            const question = new Question(validText, validChoices)

            expect(question.text()).toBe(validText)
            expect(question.choices()).toBe(validChoices)
        })
    })
})