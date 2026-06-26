import { Primitives } from "./utils/Primitives.js"

class Account {

    /**
     * @returns -1
     */
    #placeholderId() {
        return -1
    }

    /**
     * @type {int}
     */
    #id
    /**
     * @type {string}
     */
    #username
    /**
     * @type {string}
     */
    #password
    /**
     * @type {Set<int>}
     */
    #favoritedStudyguides
    /**
     * @type {Set<int>}
     */
    #downloadedStudyguides

    /**
     * Initialize a new account object with an id, username, and password.
     * 
     * @param {int | null} id The id
     * @param {string} username The username
     * @param {string} password The password
     */
    constructor({ id = null, username, password }) {
        if (id == null) {
            id = this.#placeholderId()
        }
        if (!Number.isInteger(id)) {
            throw new TypeError("id must be an integer")
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
     * @param {int} id The id of the studyguide
     * @throws {TypeError} If id is not an integer 
     */
    favorite(id) {
        if (!Number.isInteger(id)) {
            throw new TypeError("id must be an int")
        }
        this.#favoritedStudyguides.add(id)
    }

    /**
     * Removes the id from the favorited studyguides
     * @param {int} id The id of the studyguide
     * @throws {TypeError} If id is not an integer 
     */
    unfavorite(id) {
        if (!Number.isInteger(id)) {
            throw new TypeError("id must be an int")
        }
        this.#favoritedStudyguides.delete(id)
    }

    /**
     * Adds the id to the downloaded studyguides
     * @param {int} id The id of the studyguide
     * @throws {TypeError} If id is not an integer 
     */
    download(id) {
        if (!Number.isInteger(id)) {
            throw new TypeError("id must be an int")
        }
        this.#downloadedStudyguides.add(id)
    }

    /**
     * Removes the id from the downloaded studyguides
     * @param {int} id The id of the studyguide
     * @throws {TypeError} If id is not an integer 
     */
    undownload(id) {
        if (!Number.isInteger(id)) {
            throw new TypeError("id must be an int")
        }
        this.#downloadedStudyguides.delete(id)
    }
}

export { Account }

