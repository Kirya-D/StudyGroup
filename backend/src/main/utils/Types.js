import { Account } from "../model/Account.js"
import { Studyguide } from "../model/Studyguide.js"

/**
 * @typedef {object} Queryable
 * @property {() => Promise<Set<Account>>} getAllAccounts
 * @property {(accountsInfo: AccountInfo[]) => Promise<void>} createAccounts
 * @property {(guides: Studyguide[]) => Promise<void>} upsertStudyguides
 * @property {(guideIds: string[]) => Promise<void>} deleteStudyguides
 * @property {(search: string, pageNum: number, maxResultCount: number = 50) => Promise<Set<Studyguide>>} getStudyguidesWithSubstring
 */

/**
 * An Account's basic information (id and credentials)
 * @typedef {object} AccountInfo
 * @property {string} id The account id
 * @property {string} username The account username
 * @property {string} password The account password
 */

/**
 * Information about the relationship between an account and a studyguide
 * @typedef {object} StatusInfo
 * @property {string} accountId The account id
 * @property {string} studyguideId The studyguide id
 * @property {boolean} favorited The studyguide's favorited status for the account
 * @property {boolean} downloaded The studyguide's downloaded status for the account
 */

/**
 * The expected json structure of a question from a request
 * @typedef {object} RequestQuestion
 * @property {string} question The text
 * @property {Array<string>} choices The choices
 * @property {Array<string>} answers The answers
 */

/**
 * The expected json structure of a studyguide from a request
 * @typedef {object} RequestStudyguide
 * @property {string | undefined} id The id of the studyguide
 * @property {string | undefined} creatorId The id of the creator of the studyguide
 * @property {string} title The title
 * @property {string} description The description
 * @property {boolean} downloaded If the creator has the studyguide downloaded
 * @property {boolean} favorited If the creator has the studyguide favorited
 * @property {Array<RequestQuestion>} questions The questions
 * @property {number} questionCount The number of questions
 */

export { }

