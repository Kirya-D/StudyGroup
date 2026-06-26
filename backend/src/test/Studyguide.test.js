import { describe, expect, test } from "@jest/globals"
import { Choice } from "../main/Choice.js"
import { Question } from "../main/Question.js"
import { Studyguide } from "../main/Studyguide.js"

const validId = -1
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
const validQuestionCount = 0
const validCreatorId = 1

const validStudyguideParams = {
    id: validId,
    title: validTitle,
    description: validDescription,
    questions: validQuestions,
    questionCount: validQuestionCount,
    questionCount: validQuestionCount,
    creatorId: validCreatorId
}

describe("Studyguide", () => {

    describe("Constructor", () => {

        test.each([
            [null],
            [undefined]
        ])("When id is null/undefined", (id) => {
            const studyguide = new Studyguide({
                id: id,
                title: validTitle,
                description: validDescription,
                questions: validQuestions,
                questionCount: validQuestionCount,
                creatorId: validCreatorId
            })

            expect(studyguide.id()).toBe(-1)
        })

        test.each([
            [1.2],
            ["1"],
            [new Object("1")],
            [true]
        ])("Throws when id isn't an int", (id) => {
            expect(() => new Studyguide({
                id: id,
                title: validTitle,
                description: validDescription,
                questions: validQuestions,
                questionCount: validQuestionCount,
                creatorId: validCreatorId
            })).toThrow("id must be an integer")
        })

        test.each([
            [1.2],
            [1],
            [new Object("Studyguide")],
            [true],
            [undefined],
            [null]
        ])("Throws when title isn't a string", (title) => {
            expect(() => new Studyguide({
                id: validId,
                title: title,
                description: validDescription,
                questions: validQuestions,
                questionCount: validQuestionCount,
                creatorId: validCreatorId
            })).toThrow("title must be a string")
        })

        test.each([
            [1.2],
            [1],
            [new Object("Description")],
            [true],
            [undefined],
            [null]
        ])("Throws when description isn't a string", (description) => {
            expect(() => new Studyguide({
                id: validId,
                title: validTitle,
                description: description,
                questions: validQuestions,
                questionCount: validQuestionCount,
                creatorId: validCreatorId
            })).toThrow("description must be a string")
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
            expect(() => new Studyguide({
                id: validId,
                title: validTitle,
                description: validDescription,
                questions: questions,
                creatorId: validCreatorId
            })).toThrow("questions must be a Question set")
        })

        test.each([
            [new Set([1])],
            [new Set(["question"])],
            [new Set([{}])],
            [new Set([true])]
        ])("Throws when all questions aren't Question objects", (questions) => {
            expect(() => new Studyguide({
                id: validId,
                title: validTitle,
                description: validDescription,
                questions: questions,
                creatorId: validCreatorId
            })).toThrow("all questions must be a Question object")
        })

        test.each([
            [null],
            [1.2],
            ["1"],
            [new Object("1")],
            [true]
        ])("Throws when questionCount isn't an int", (questionCount) => {
            expect(() => new Studyguide({
                id: validId,
                title: validTitle,
                description: validDescription,
                questions: validQuestions,
                questionCount: questionCount,
                creatorId: validCreatorId
            })).toThrow("questionCount must be an integer")
        })

        test.each([
            [undefined],
            [null],
            [1.2],
            ["1"],
            [new Object("1")],
            [true]
        ])("Throws when creatorId isn't an int", (creatorId) => {
            expect(() => new Studyguide({
                id: validId,
                title: validTitle,
                description: validDescription,
                questions: validQuestions,
                questionCount: validQuestionCount,
                creatorId: creatorId
            })).toThrow("creatorId must be an integer")
        })

        test("When Successful", () => {
            const studyguide = new Studyguide(validStudyguideParams)

            expect(studyguide.id()).toBe(validId)
            expect(studyguide.title()).toBe(validTitle)
            expect(studyguide.description()).toBe(validDescription)
            expect(studyguide.questions()).toBe(validQuestions)
            expect(studyguide.questionCount()).toBe(validQuestions.size)
            expect(studyguide.creatorId()).toBe(validCreatorId)
        })
    })
})