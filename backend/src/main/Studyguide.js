import { Question } from "./Question.js"
import { Primitives } from "./utils/Primitives.js"

class Studyguide {

    /**
     * @type {int}
     */
    #id
    /**
     * @type {string}
     */
    #title
    /**
     * @type {string}
     */
    #description
    /**
     * @type {Set<Question>}
     */
    #questions
    /**
     * @type {int}
     */
    #questionCount
    /**
     * @type {int}
     */
    #creatorId

    /**
     * @returns -1
     */
    #placeholderId() {
        return -1
    }

    /**
     * Initialize a new studyguide object with an id, title, description, and creatorId.
     * 
     * @param {int} id The id. default null
     * @param {string} title The title
     * @param {string} description The password
     * @param {Set<Question>} questions The questions
     * @param {int} questionCount The number of questions. default 0
     * @param {int} id The creator's account id
     */
    constructor({ id = null, title, description, questions, questionCount = 0, creatorId }) {
        if (id == null) {
            id = this.#placeholderId()
        }
        if (!Number.isInteger(id)) {
            throw new TypeError("id must be an integer")
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
        var allQuestionsValid = true
        questions.forEach(question => {
            if (!(question instanceof Question)) {
                allQuestionsValid = false
            }
        })
        if (!allQuestionsValid) {
            throw new TypeError("all questions must be a Question object")
        }
        if (!Number.isInteger(questionCount)) {
            throw new TypeError("questionCount must be an integer")
        }
        if (!Number.isInteger(creatorId)) {
            throw new TypeError("creatorId must be an integer")
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

    questionCount() {
        return this.#questions.size == 0 ? this.#questionCount : this.#questions.size
    }

    creatorId() {
        return this.#creatorId
    }
}

export { Studyguide }

