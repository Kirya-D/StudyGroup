import crypto from "crypto"
import { Account } from "../Account.js"
import { Database } from "../Database.js"
import { Primitives } from "../utils/Primitives.js"
import { StatusCode } from "../utils/StatusCode.js"

/** @type {Map<string, Account>} */
const usernameAccounts = new Map()

/**
 * Returns the account that has the given username if any exists, otherwise undefined
 * 
 * @param {string} username The username to match
 * @returns The account whose username matches the given
 */
function getAccountWithUsername(username) {
    return usernameAccounts.get(username)
}

/**
 * Returns the account that has the given credentials if any exists, otherwise undefined
 * 
 * @param {string} username The username to match
 * @param {string} password The password to match
 * 
 * @returns The account whose credentials matches the given
 */
function getAccountWithCredentials(username, password) {
    let associatedAccount = undefined

    const fromUsername = getAccountWithUsername(username)
    if (fromUsername != undefined && fromUsername.password() === password) {
        associatedAccount = fromUsername
    }

    return associatedAccount
}

/**
 * Attempt to load all accounts from databae
 * 
 * @param {Database} database The database to load from
 */
async function loadAccountsFromDatabase(database) {
    usernameAccounts.clear()
    const dbAccounts = await database.getAllAccounts()
    for (const acc of dbAccounts) {
        const username = acc.username()
        usernameAccounts.set(username, acc)
    }
}

/**
 * Attempts to create a new account with the given username and password
 * 
 * @param {string} username The username to use
 * @param {string} password The password to use
 * 
 * @returns The response information to return to the client
 */
async function createAccount(username, password) {
    let status = undefined
    let usernameIsAvailable = true
    let success = false
    let message = ""

    if (typeof username !== Primitives.STRING || typeof password !== Primitives.STRING) {
        status = StatusCode.BAD_REQUEST
    }

    if (status == undefined) {
        usernameIsAvailable = !usernameAccounts.has(username)
        if (usernameIsAvailable) {
            const id = crypto.randomUUID()
            const newAccount = new Account(id, username, password)
            usernameAccounts.set(newAccount.username(), newAccount)
            success = true
            status = StatusCode.CREATED
        } else {
            status = StatusCode.CONFLICT
            message = "username is already in-use, try a different one"
        }
    }

    return {
        success: success,
        status: status,
        message: message
    }
}

const AccountHandler = Object.freeze({
    getAccountWithUsername: getAccountWithUsername,
    getAccountWithCredentials: getAccountWithCredentials,
    loadAccountsFromDatabase: loadAccountsFromDatabase,
    createAccount: createAccount
})

export { AccountHandler }

