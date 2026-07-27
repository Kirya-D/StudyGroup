import { describe, expect, test } from "@jest/globals"
import { Choice } from "../../main/model/Choice.js"
import { Question } from "../../main/model/Question.js"
import { Studyguide } from "../../main/model/Studyguide.js"

const validId = "1"
const validTitle = "Studyguide"
const validDescription = "Description"
const validQuestions = new Set([
    new Question(
        "What is 2 + 2?",
        new Set([
            new Choice("3", false),
            new Choice("4", true),
            new Choice("5", false)
        ])
    )
])
const validCreatorId = "1"

describe("Studyguide", () => {

    describe("Constructor", () => {

        test.each([
            [1.2],
            [1],
            [true],
            [null],
            [undefined]
        ])("Throws when id isn't a string", (id) => {
            expect(() => new Studyguide(
                id,
                validTitle,
                validDescription,
                validQuestions,
                validCreatorId
            )).toThrow("id must be a string")
        })

        test.each([
            [1.2],
            [1],
            [new Object("Studyguide")],
            [true],
            [undefined],
            [null]
        ])("Throws when title isn't a string", (title) => {
            expect(() => new Studyguide(
                validId,
                title,
                validDescription,
                validQuestions,
                validCreatorId
            )).toThrow("title must be a string")
        })

        test.each([
            [1.2],
            [1],
            [new Object("Description")],
            [true],
            [undefined],
            [null]
        ])("Throws when description isn't a string", (description) => {
            expect(() => new Studyguide(
                validId,
                validTitle,
                description,
                validQuestions,
                validCreatorId
            )).toThrow("description must be a string")
        })

        test.each([
            [undefined],
            [null],
            [1],
            [1.2],
            ["questions"],
            [true],
            [new Object()]
        ])("Throws when questions isn't a Question set", (questions) => {
            expect(() => new Studyguide(
                validId,
                validTitle,
                validDescription,
                questions,
                validCreatorId
            )).toThrow("questions must be a Question set")
        })

        test.each([
            [new Set([1])],
            [new Set(["question"])],
            [new Set([{}])],
            [new Set([true])]
        ])("Throws when all questions aren't Question objects", (questions) => {
            expect(() => new Studyguide(
                validId,
                validTitle,
                validDescription,
                questions,
                validCreatorId
            )).toThrow("all questions must be a Question object")
        })

        test.each([
            [undefined],
            [null],
            [1.2],
            [1],
            [true]
        ])("Throws when creatorId isn't an int", (creatorId) => {
            expect(() => new Studyguide(
                validId,
                validTitle,
                validDescription,
                validQuestions,
                creatorId
            )).toThrow("creatorId must be a string")
        })

        test("When Successful", () => {
            const studyguide = new Studyguide(validId, validTitle, validDescription, validQuestions, validCreatorId)

            expect(studyguide.id()).toBe(validId)
            expect(studyguide.title()).toBe(validTitle)
            expect(studyguide.description()).toBe(validDescription)
            expect(studyguide.questions()).toBe(validQuestions)
            expect(studyguide.creatorId()).toBe(validCreatorId)
        })
    })
})