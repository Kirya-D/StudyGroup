import express from "express"
import { Database } from "./Database.js"

const app = express()
const port = process.env.PORT
const host = process.env.HOST
const dbConfig = process.env.DB_URL

const db = new Database()
await db.connectToDatabase({config: dbConfig})

app.set('trust proxy', 1);

app.listen(port, host, () => {
    console.log(`hello on ${port}:${host}`)
})