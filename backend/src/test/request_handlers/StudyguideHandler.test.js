import { describe, expect, test } from "@jest/globals"
import crypto from "crypto"
import { Account } from "../../main/Account.js"
import { StudyguideHandler } from "../../main/request_handlers/StudyguideHandler.js"
import { StatusCode } from "../../main/utils/StatusCode.js"

function createAccount() {
    const id = crypto.randomUUID()
    return new Account(id, `user-${id}`, "password")
}

function createRequestedGuide(overrides = {}) {
    return {
        id: overrides.id,
        creatorId: overrides.creatorId,
        title: overrides.title ?? "Studyguide",
        description: overrides.description ?? "Description",
        downloaded: overrides.downloaded ?? false,
        favorited: overrides.favorited ?? false,
        questions: overrides.questions ?? [
            {
                question: "Question 1",
                choices: ["Choice A", "Choice B"],
                answers: ["Choice A"]
            }
        ],
        questionCount: overrides.questionCount ?? 1
    }
}

describe("StudyguideHandler", () => {

    describe("UpsertStudyguide", () => {
        test("When Missing Account Or Studyguide Information", async () => {
            const response = await StudyguideHandler.upsertStudyguide(undefined, createRequestedGuide())

            expect(response.success).toBe(false)
            expect(response.status).toBe(StatusCode.BAD_REQUEST)
        })

        test("When Account Does Not Have Edit Permission", async () => {
            const firstRequester = createAccount()
            const secondRequester = createAccount()
            const onlyGuide = createRequestedGuide({
                title: "My Guide",
                favorited: true,
                downloaded: true
            })
            const firstResponse = await StudyguideHandler.upsertStudyguide(firstRequester, onlyGuide)
            onlyGuide.id = firstResponse.id
            onlyGuide.title = "Updated title"
            const secondResponse = await StudyguideHandler.upsertStudyguide(secondRequester, onlyGuide)

            expect(secondResponse.success).toBe(false)
            expect(secondResponse.status).toBe(StatusCode.FORBIDDEN)
            expect(secondResponse.id).toBeUndefined()
        })

        test("When Successful", async () => {
            const requester = createAccount()
            const response = await StudyguideHandler.upsertStudyguide(requester, createRequestedGuide({
                title: "My Guide",
                favorited: true,
                downloaded: true
            }))

            expect(response.success).toBe(true)
            expect(response.status).toBe(StatusCode.CREATED)
            expect(response.id).toEqual(expect.any(String))

            expect(requester.favoritedStudyguides().has(response.id)).toBe(true)
            expect(requester.downloadedStudyguides().has(response.id)).toBe(true)
        })

        test("When Successful Update", async () => {
            const requester = createAccount()
            const guide = createRequestedGuide({
                title: "My Guide",
                favorited: true,
                downloaded: true
            })
            const firstResponse = await StudyguideHandler.upsertStudyguide(requester, guide)
            guide.title = "My Updated Guide"
            guide.favorited = true

            const secondResponse = await StudyguideHandler.upsertStudyguide(requester, createRequestedGuide({
                id: firstResponse.id,
                title: "My Updated Guide",
                favorited: false,
                downloaded: false
            }))

            expect(secondResponse.success).toBe(true)
            expect(secondResponse.status).toBe(StatusCode.OK)
            expect(secondResponse.id).toEqual(firstResponse.id)

            expect(requester.favoritedStudyguides().has(secondResponse.id)).toBe(false)
            expect(requester.downloadedStudyguides().has(secondResponse.id)).toBe(false)
        })
    })

    describe("DeleteStudyguide", () => {
        test("When Missing Account Or Studyguide Information", async () => {
            const response = await StudyguideHandler.deleteStudyguide(undefined, "missing-id")

            expect(response.success).toBe(false)
            expect(response.status).toBe(StatusCode.BAD_REQUEST)
        })

        test("When Requester Does Not Own The Studyguide", async () => {
            const owner = createAccount()
            const requester = createAccount()

            const createResponse = await StudyguideHandler.upsertStudyguide(owner, createRequestedGuide({
                title: "Owned Guide"
            }))

            const response = await StudyguideHandler.deleteStudyguide(requester, createResponse.id)

            expect(response.success).toBe(false)
            expect(response.status).toBe(StatusCode.FORBIDDEN)
            expect(response.message).toContain("does not have permissions")
        })

        test("When Successful", async () => {
            const requester = createAccount()

            const createResponse = await StudyguideHandler.upsertStudyguide(requester, createRequestedGuide({
                title: "Removable Guide"
            }))

            const response = await StudyguideHandler.deleteStudyguide(requester, createResponse.id)

            expect(response.success).toBe(true)
            expect(response.status).toBe(StatusCode.NO_CONTENT)
        })
    })

    describe("FindStudyguides", () => {
        test("When Missing Account Information Or Search Text", async () => {
            const response = await StudyguideHandler.findStudyguides(undefined, "test", 0, 10)

            expect(response.success).toBe(false)
            expect(response.status).toBe(StatusCode.BAD_REQUEST)
        })

        test("When Maximum Result Count Is Reached", async () => {
            const requester = createAccount()
            const uniqueSearch = crypto.randomUUID()

            await StudyguideHandler.upsertStudyguide(requester, createRequestedGuide({
                title: `Alpha Guide 0 ${uniqueSearch}`,
                description: `Contains alpha ${uniqueSearch}`
            }))
            await StudyguideHandler.upsertStudyguide(requester, createRequestedGuide({
                title: `Alpha Guide 1 ${uniqueSearch}`,
                description: `Contains alpha ${uniqueSearch}`
            }))
            await StudyguideHandler.upsertStudyguide(requester, createRequestedGuide({
                title: `Alpha Guide 2 ${uniqueSearch}`,
                description: `Contains alpha ${uniqueSearch}`
            }))

            const response = await StudyguideHandler.findStudyguides(requester, uniqueSearch, 0, 2)

            expect(response.success).toBe(true)
            expect(response.status).toBe(StatusCode.OK)
            expect(response.results).toHaveLength(2)
        })

        test("When Page Or Max Return Size Is Invalid", async () => {
            const requester = createAccount()
            const response = await StudyguideHandler.findStudyguides(requester, "test", 1.5, 10)

            expect(response.success).toBe(false)
            expect(response.status).toBe(StatusCode.BAD_REQUEST)
        })

        test("When Successful", async () => {
            const requester = createAccount()
            const uniqueSearch = crypto.randomUUID()

            await StudyguideHandler.upsertStudyguide(requester, createRequestedGuide({
                title: `Alpha Guide 0 ${uniqueSearch}`,
                description: `Contains alpha ${uniqueSearch}`
            }))
            await StudyguideHandler.upsertStudyguide(requester, createRequestedGuide({
                title: `Alpha Guide 1 ${uniqueSearch}`,
                description: `Contains alpha ${uniqueSearch}`
            }))
            await StudyguideHandler.upsertStudyguide(requester, createRequestedGuide({
                title: `Alpha Guide 2 ${uniqueSearch}`,
                description: `Contains alpha ${uniqueSearch}`
            }))

            const response = await StudyguideHandler.findStudyguides(requester, uniqueSearch, 1, 2)

            expect(response.success).toBe(true)
            expect(response.status).toBe(StatusCode.OK)
            expect(response.results).toHaveLength(1)
            expect(response.results[0].title).toBe(`Alpha Guide 2 ${uniqueSearch}`)
        })
    })
})