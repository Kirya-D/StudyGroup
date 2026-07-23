import mssql from "mssql"
import { Database } from "./model/Database.js"
import { Application, Server } from "./model/Server.js"
import { AccountHandler } from "./request_handlers/AccountHandler.js"
import { StudyguideHandler } from "./request_handlers/StudyguideHandler.js"

const MILLISECOND = 1
const SECOND = 1000 * MILLISECOND
const MINUTE = 60 * SECOND
const HOUR = 60 * MINUTE
const DAY = 24 * HOUR
const dbConfig = process.env.NODE_ENV === "production" ? process.env.DB_URL : process.env.TESTING_DB_URL
const db = new Database()
let curAttempt = 0
const maxAttempts = 3

async function tryConnect() {
    await db.connectToDatabase({ config: dbConfig }).then(
        (value) => {
            console.log("Connected to database")
        },
        (reason) => {
            if (reason instanceof mssql.ConnectionError && curAttempt <= maxAttempts) {
                curAttempt++
                tryConnect()
            }
        }
    )
}

await tryConnect()
await AccountHandler.loadAccountsFromDatabase(db)

const port = process.env.PORT
const host = process.env.HOST
Application.startApplication()
Server.startServer(port, host)

async function propogateChangesToDatabase() {
    await AccountHandler.propogateAccountChangesToDatabase(db)
    await StudyguideHandler.propogateStudyguideChangesToDatabase(db)
}

/**
 * @param {Function | undefined} callback To call this loop
 */
function scheduleDailyDatabaseUpdates(callback) {
    if (callback != undefined) {
        callback()
    }

    const currentMidnight = new Date()
    const nextMidnight = new Date(currentMidnight)
    const currentDate = currentMidnight.getDate()
    nextMidnight.setDate(currentDate + 1)
    nextMidnight.setHours(0, 0, 0, 0)

    const delayUntilNextMidnight = nextMidnight.getTime() - currentMidnight.getTime()
    
    setTimeout(() => {
        scheduleDailyDatabaseUpdates(propogateChangesToDatabase)
    }, delayUntilNextMidnight)
}

scheduleDailyDatabaseUpdates()

async function endProcess() {
    Server.shutdownServer()
    await propogateChangesToDatabase()
    await db.disconnect()

    process.exit()
}

process.on("SIGINT", endProcess)
process.on("SIGTERM", endProcess)

export { }

