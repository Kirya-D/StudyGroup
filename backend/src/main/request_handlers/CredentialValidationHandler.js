import { StatusCode } from "../utils/StatusCode.js"

const MIN_CHARACTERS = 8
const MAX_CHARACTERS = 32
const ALPHANUMERIC = /^[\p{L}\p{N}]+$/u
const WHITESPACE = /\s/
const VALIDATION_MESSAGE = Object.freeze({
    VALID: "",
    USERNAME_TYPE_MESSAGE: "Username must be a string",
    USERNAME_LENGTH_MESSAGE: "Username must be between 8 and 32 characters",
    USERNAME_ALPHANUMERIC_MESSAGE: "Username may only contain letters and numbers",
    PASSWORD_TYPE_MESSAGE: "Password must be a string",
    PASSWORD_LENGTH_MESSAGE: "Password must be between 8 and 32 characters",
    PASSWORD_WHITESPACE_MESSAGE: "Password cannot contain whitespace"
})

/**
 * Checks if the given username is valid and returns a response containing a
 * message that describes any invalidating properties of the username.
 * 
 * A valid username is alphanumeric and between 8 and 32 characters (inclusive)
 * 
 * @param {string} username The username to validate
 * @returns A response for the result
 */
async function validateUsername(username) {
    let response = {
        status: StatusCode.OK,
        message: VALIDATION_MESSAGE.VALID
    }

    if (typeof username !== "string") {
        response.status = StatusCode.BAD_REQUEST
        response.message = VALIDATION_MESSAGE.USERNAME_TYPE_MESSAGE
    } else if (username.length < MIN_CHARACTERS || username.length > MAX_CHARACTERS) {
        response.status = StatusCode.BAD_REQUEST
        response.message = VALIDATION_MESSAGE.USERNAME_LENGTH_MESSAGE
    } else if (!ALPHANUMERIC.test(username)) {
        response.status = StatusCode.BAD_REQUEST
        response.message = VALIDATION_MESSAGE.USERNAME_ALPHANUMERIC_MESSAGE
    }

    return response
}

/**
 * Checks if the given password is valid and returns a response containing a
 * message that describes any invalidating properties of the password.
 * 
 * A valid password is between 8 and 32 characters (inclusive)
 * and does not contain whitespace characters
 * 
 * @param {string} password The password to validate
 * @returns A response for the result
 */
async function validatePassword(password) {
    let response = {
        status: StatusCode.OK,
        message: VALIDATION_MESSAGE.VALID
    }

    if (typeof password !== "string") {
        response.status = StatusCode.BAD_REQUEST
        response.message = VALIDATION_MESSAGE.PASSWORD_TYPE_MESSAGE
    } else if (password.length < MIN_CHARACTERS || password.length > MAX_CHARACTERS) {
        response.status = StatusCode.BAD_REQUEST
        response.message = VALIDATION_MESSAGE.PASSWORD_LENGTH_MESSAGE
    } else if (WHITESPACE.test(password)) {
        response.status = StatusCode.BAD_REQUEST
        response.message = VALIDATION_MESSAGE.PASSWORD_WHITESPACE_MESSAGE
    }

    return response
}

const CredentialValidationHandler = Object.freeze({
    validateUsername: validateUsername,
    validatePassword: validatePassword,
    VALIDATION_MESSAGE: VALIDATION_MESSAGE
})

export { CredentialValidationHandler }

