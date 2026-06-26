import { Primitives } from "./utils/Primitives.js"

class Choice {

    /**
     * @type {string}
     */
    #text
    /**
     * @type {boolean}
     */
    #isAnswer

    /**
     * Initializes a new Choice with the given text and answer status
     * 
     * @param {string} text The text
     * @param {boolean} isAnswer If this choice is an answer or not
     */
    constructor(text, isAnswer) {
        if (typeof text !== Primitives.STRING) {
            throw new TypeError("text must be a string")
        }
        if (typeof isAnswer !== Primitives.BOOLEAN) {
            throw new TypeError("isAnswer must be a boolean")
        }

        this.#text = text
        this.#isAnswer = isAnswer
    }

    text() {
        return this.#text
    }

    isAnswer() {
        return this.#isAnswer
    }
}

export { Choice }

