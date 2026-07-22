/** @import { Queryable } from "../main/utils/Types.js" */

import { Account } from "../main/model/Account.js"
import { Studyguide } from "../main/model/Studyguide.js"

/** @type {Map<string, Account>} */
const mockAccounts = new Map()
/** @type {Map<string, Studyguide>} */
const mockGuides = new Map()

/** @type {Queryable} */
const MockDatabase = {
    async getAllAccounts() {
        return new Set(accounts.values())
    },

    async createAccounts(accountsInfo) {
        for (const account of accountsInfo) {
            const accountObject = new Account(account.id, account.username, account.password)
            mockAccounts.set(account.username, accountObject)
        }
    },

    async upsertStudyguides(guides) {
        for (const guide of guides) {
            mockGuides.set(guide.id(), guide)
        }
    },

    async deleteStudyguides(guideIds) {
        for (const id of guideIds) {
            mockGuides.delete(id)
        }
    },

    async getStudyguidesWithSubstring(search, pageNum, maxResultCount = 50) {
        const startNum = pageNum * maxResultCount
        const endNum = startNum + maxResultCount
        const unslicedResults = []
        for (const guide of mockGuides.values()) {
            const matchTitle = guide.title().includes(search)
            const matchDescription = matchTitle ? true : guide.description().includes(search)

            if (matchTitle || matchDescription) {
                unslicedResults.push(guide)
            }
        }

        const slicedResults = unslicedResults.slice(startNum, endNum)
        return new Set(slicedResults)
    }
}

function resetMockDatabaseState() {
    mockAccounts.clear()
    mockGuides.clear()
}

export { MockDatabase, resetMockDatabaseState }

