import crypto from "crypto"
import { Account } from "../model/Account.js"
import { Primitives } from "../utils/Primitives.js"
import { StatusCode } from "../utils/StatusCode.js"
/** @import { Queryable } from "../utils/Types.js" */

/** @type {Map<string, Account>} */
const usernameAccounts = new Map()
/** @type {Set<string>} */
const newAccountUsernames = new Set()

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
 * @param {Queryable} database The database to load from
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
            usernameAccounts.set(username, newAccount)
            newAccountUsernames.add(username)
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

/**
 * Propogates updated account information to the given database
 * 
 * @param {Queryable} database The database to propogate changes to
 */
async function propogateAccountChangesToDatabase(database) {
    const accountsInformation = []

    for (const accountName of newAccountUsernames) {
        const account = usernameAccounts.get(accountName)
        if (account != null) {
            const newInformation = {
                id: account.id(),
                username: accountName,
                password: account.password()
            }
            accountsInformation.push(newInformation)
        }
    }
    
    await database.createAccounts(accountsInformation)
    clearStoredChanges()
}

function clearStoredChanges() {
    newAccountUsernames.clear()
}

const AccountHandler = Object.freeze({
    getAccountWithUsername: getAccountWithUsername,
    getAccountWithCredentials: getAccountWithCredentials,
    loadAccountsFromDatabase: loadAccountsFromDatabase,
    createAccount: createAccount,
    propogateAccountChangesToDatabase: propogateAccountChangesToDatabase,
    clearStoredChanges: clearStoredChanges
})

export { AccountHandler }

