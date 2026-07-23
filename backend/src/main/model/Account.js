import { Primitives } from "../utils/Primitives.js"

class Account {

    /** @type {string} */
    #id
    /** @type {string} */
    #username
    /** @type {string} */
    #password
    /** @type {Set<string>} */
    #favoritedStudyguides
    /** @type {Set<string>} */
    #downloadedStudyguides

    /**
     * Initialize a new account object with an id, username, and password.
     * 
     * @param {string} id The id
     * @param {string} username The username
     * @param {string} password The password
     */
    constructor(id, username, password) {
        if (typeof id !== Primitives.STRING) {
            throw new TypeError("id must be a string")
        }
        if (typeof username !== Primitives.STRING) {
            throw new TypeError("username must be a string")
        }
        if (typeof password !== Primitives.STRING) {
            throw new TypeError("password must be a string")
        }

        this.#id = id
        this.#username = username
        this.#password = password
        this.#favoritedStudyguides = new Set()
        this.#downloadedStudyguides = new Set()
    }

    id() {
        return this.#id
    }

    username() {
        return this.#username
    }

    password() {
        return this.#password
    }

    favoritedStudyguides() {
        return new Set(this.#favoritedStudyguides.values())
    }

    downloadedStudyguides() {
        return new Set(this.#downloadedStudyguides.values())
    }

    /**
     * Adds the id to the favorited studyguides
     * @param {string} id The id of the studyguide
     * @throws {TypeError} If id is not a string
     */
    favorite(id) {
        if (typeof id !== Primitives.STRING) {
            throw new TypeError("id must be a string")
        }
        this.#favoritedStudyguides.add(id)
    }

    /**
     * Removes the id from the favorited studyguides
     * @param {string} id The id of the studyguide
     * @throws {TypeError} If id is not a string
     */
    unfavorite(id) {
        if (typeof id !== Primitives.STRING) {
            throw new TypeError("id must be a string")
        }
        this.#favoritedStudyguides.delete(id)
    }

    /**
     * Adds the id to the downloaded studyguides
     * @param {string} id The id of the studyguide
     * @throws {TypeError} If id is not a string
     */
    download(id) {
        if (typeof id !== Primitives.STRING) {
            throw new TypeError("id must be a string")
        }
        this.#downloadedStudyguides.add(id)
    }

    /**
     * Removes the id from the downloaded studyguides
     * @param {string} id The id of the studyguide
     * @throws {TypeError} If id is not a string
     */
    undownload(id) {
        if (typeof id !== Primitives.STRING) {
            throw new TypeError("id must be a string")
        }
        this.#downloadedStudyguides.delete(id)
    }
}

export { Account }

