import { Primitives } from "../utils/Primitives.js"
import { Choice } from "./Choice.js"

class Question {
    /** @type {string} */
    #text
    /** @type {Set<Choice>} */
    #choices

    /**
     * Initialize a new question object with text, choices, and answers
     * 
     * @param {string} text The title
     * @param {Set<Choice>} choices The choices
     */
    constructor(text, choices) {
        if (typeof text !== Primitives.STRING) {
            throw new TypeError("text must be a string")
        }
        if (!(choices instanceof Set)) {
            throw new TypeError("choices must be a Choice set")
        }
        let allChoicesValid = true
        choices.forEach(choice => {
            if (!(choice instanceof Choice)) {
                allChoicesValid = false
            }
        })
        if (!allChoicesValid) {
            throw new TypeError("all choices must be a Choice object")
        }

        this.#text = text
        this.#choices = choices
    }

    text() {
        return this.#text
    }

    choices() {
        return this.#choices
    }
}

export { Question }

