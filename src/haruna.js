const fastify = require('fastify')
const enmap = require('enmap')
const fetch = require('node-fetch')
const { WebhookClient } = require('discord.js')
const { version } = require('../package.json')
const defaultSettings = {
    port: 8000,
    auth: '',
    length: 43200000,
    token: '',
    dir: './data',
    https: null,
    webhook:''
}

class Haruna {
    /**
     * Starts the server
     * @param {Object} options The options
     * @param {number} [options.port=8080] The port to listen to
     * @param {string} options.auth Authentication key. Will be used when querying the server
     * @param {number} [options.length=43200000] A vote's lifetime in miliseconds. Defaults to 12 hours
     * @param {string} options.token DBL token
     * @param {string} [options.dir='./'] The directory of the database
     * @param {https} options.https HTTPS option for fastify. Omit this if you dont use it
     * @param {webhook} options.webhook The webhook **link**
     */
    constructor(options) {
        this.options = {...defaultSettings, ...options}
        if (!this.options.auth || !this.options.token)
            throw new Error('Authentication key or DBL token not specified')

        if (this.options.https) {
            this.app = fastify({https: this.options.https})
        } else {
            this.app = fastify()
        }
        
        this.storage = new enmap({
            name: 'votes',
            fetchAll: true,
            dataDir: this.options.dir
        })

        this.storage.defer.then(() => {
            this.app.post('/vote', (req, res) => this._onVote(req, res))
            this.app.get('/hasVoted', (req, res) => this._onCheck(req, res))
            this.app.get('/getVotedTime', (req, res) => this._onCheckInfo(req, res))
            this.app.get('/', (req, res) => this._index(req, res))
            this.app.listen(this.options.port)
                .catch(err => err && console.error('[Error] Server failed to start. Details: ', err.message))
                .then(addr => {
                    console.log("[Notice] Haruna's Vote Service is now Online, listening @ ", addr)
                    if (this.options.webhook) this.execWebhook({
                        embeds: [{
                            color: 0x90ee90,
                            description: '✅ **Voting API intialized**',
                            timestamp: new Date(),
                            footer: {
                                text: `Haruna Vote Handler v${version}`
                            }
                        }]
                    }).catch(console.error)
                })

            setInterval(async () => {
                let counter = 0;
                for (const [key, val] of this.storage) {
                    if (val.time < Date.now()) {
                        this.storage.delete(key)
                        counter++
                    }
                }
                console.log(`[Cron Job] Database Purged, removed ${counter} ${counter <= 1 ? 'user' : 'users'} from vote db`)
                if (this.options.webhook) this.execWebhook({
                    embeds: [{
                        color: 0x9B767B,
                        description: `📤 Cleaned **${counter} ${counter <= 1 ? 'user' : 'users'}** from database`,
                        timestamp: new Date(),
                        footer: {
                            text: `💾 ${this.storage.size} stored votes`
                        }
                    }]
                }).catch(console.error)
            }, 300000)
        })
    }

    execWebhook(data) {
        return fetch(this.options.webhook, { method: 'POST', body: JSON.stringify(data), headers: { 'Content-Type': 'application/json' } })
    }

    async _fetch_user(id) {
        const req = await fetch(`https://discordbots.org/api/users/${id}`, {
            headers: { authorization: this.options.token }
        })
        const user = await req.json()
        return user.username + '#' + user.discriminator
    }

    async _send_new_vote_embed(user_id) {
        if (!this.options.webhook) return
        const tag = await this._fetch_user(user_id)
        await this.execWebhook({
            embeds: [{
                color: 0x095562,
                description: `📥 New User: **@${tag}** (${user_id}) voted`,
                timestamp: new Date(),
                footer: {
                    text: `💾 ${this.storage.size} stored votes`
                }
            }]
        })
    }

    _onVote(req, res) {
        if (req.headers.authorization !== this.options.auth) {
            res.status(401).send('Unauthorized')
            console.log('[Notice] Rejected Post Request, Details Below\n', req.headers)
        } else {
            if (this.storage.has(req.body.user)) this.storage.delete(req.body.user)
            const duration = Date.now() + this.options.length
            this.storage.set(req.body.user, { time: duration, isWeekend: req.body.isWeekend})
            res.send('Sucess')
            console.log(`[Notice] New vote stored, Duration: ${Math.floor((duration - Date.now()) / 1000 / 60 / 60)} hrs, user_id: ${req.body.user}, isWeekend: ${req.body.isWeekend}.`)
            this._send_new_vote_embed(req.body.user).catch(console.error)
        }
    }

    _onCheck(req, res) {
        if (req.headers.authorization !== this.options.auth) {
            res.status(401).send('Unauthorized')
            console.log('[Notice] Rejected hasVoted Request, Details below\n', req.headers)
        } else {
            const user = this.storage.get(req.headers.user_id)
            if (req.headers.user_id) {
                if (req.headers.checkWeekend) {
                    res.send(user && user.isWeekend)
                } else {
                    res.send(!!user)
                }
            } else res.send(false)
            console.log(`[Notice] Checked Vote for user_id ${req.headers.user_id}. Time Left: ${user ? `${((user.time - Date.now()) / 1000 / 60 / 60).toFixed(1)} hr(s).`: 'Not in Database.'}`)
        }
    }

    _onCheckInfo(req, res) {
        if (req.headers.authorization !== this.options.auth) {
            res.status(401).send('Unauthorized')
            console.log('[Notice] Rejected getVotedTime Request, Details below\n', req.headers)
        } else {
            const user = this.storage.get(req.headers.user_id)
            if (user && req.headers.user_id) {
                res.send(user.time - Date.now())
            } else res.send(false)
            console.log(`[Notice] Checked Vote Time for user_id ${req.headers.user_id}. Time Left: ${user ? `${((user.time - Date.now()) / 1000 / 60 / 60).toFixed(1)} hr(s).`: 'Not in Database.'}`)
        }
    }

    _index(req, res) {
        res.send(`Haruna Vote Handler v${version}`)
    }
}
module.exports = Haruna
