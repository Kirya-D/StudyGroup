import { Primitives } from "../utils/Primitives.js"
import { Question } from "./Question.js"

class Studyguide {

    /** @type {string} */
    #id
    /** @type {string} */
    #title
    /** @type {string} */
    #description
    /** @type {Set<Question>} */
    #questions
    /** @type {string} */
    #creatorId

    /**
     * Initialize a new studyguide object with an id, title, description, creatorId, and question count.
     * 
     * @param {string} id The id.
     * @param {string} title The title
     * @param {string} description The password
     * @param {Set<Question>} questions The questions
     * @param {string} creatorId The creator's account id
     */
    constructor(id, title, description, questions, creatorId) {
        if (typeof id !== Primitives.STRING) {
            throw new TypeError("id must be a string")
        }
        if (typeof title !== Primitives.STRING) {
            throw new TypeError("title must be a string")
        }
        if (typeof description !== Primitives.STRING) {
            throw new TypeError("description must be a string")
        }
        if (!(questions instanceof Set)) {
            throw new TypeError("questions must be a Question set")
        }
        let allQuestionsValid = true
        questions.forEach(question => {
            if (!(question instanceof Question)) {
                allQuestionsValid = false
            }
        })
        if (!allQuestionsValid) {
            throw new TypeError("all questions must be a Question object")
        }
        if (typeof creatorId !== Primitives.STRING) {
            throw new TypeError("creatorId must be a string")
        }

        this.#id = id
        this.#title = title
        this.#description = description
        this.#questions = questions
        this.#creatorId = creatorId
    }

    id() {
        return this.#id
    }

    title() {
        return this.#title
    }

    description() {
        return this.#description
    }

    questions() {
        return this.#questions
    }

    creatorId() {
        return this.#creatorId
    }
}

export { Studyguide }

