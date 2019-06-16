const fastify = require('fastify')
const fetch = require('node-fetch')

const HarunaStore = require('./HarunaStore.js')
const Constant = require('./HarunaConstant.js')
const { version } = require('../package.json')

class Haruna {
    /**
     * Starts the server
     * @param {Object} options The options
     * @param {number} [options.port=8080] The port to listen to
     * @param {string} options.auth Authentication key. Will be used when querying the server
     * @param {number} [options.length=43200000] A vote's lifetime in miliseconds. Defaults to 12 hours
     * @param {string} options.token DBL token
     * @param {boolean} [options.logging=true] Whether to log optional logs
     * @param {string} [options.dir=''] The directory of the database
     * @param {webhook} options.webhook The webhook **link**
     */
    constructor(options) {
        /**
         * Options that is used to initialize Haruna with
         * @type {Object}
         */
        this.options = { ...Constant, ...options }

        if (!this.options.auth || !this.options.token)
            throw new Error('Authentication key or DBL token not specified')
        /**
         * Haruna Store
         * @type {HarunaStore}  
         */
        this.store = new HarunaStore(this)
        /**
         * An Fastify instance
         * @type {external:Fastify}
         */
        this.app = fastify()
        /**
         * Polled Memory Usage
         * @type {number}
         */
        this.rss = process.memoryUsage().rss
        /**
         * Requests received since boot counter
         * @type {number}
         */
        this.requestsReceived = 0
        //
        this._build()
        this._listen()
    }

    get ram() {
        return this.rss < 1024000000 ? `${Math.round(this.rss / 1024 / 1024)} MB` : `${(this.rss / 1024 / 1024 / 1024).toFixed(1)} GB`
    }
    /**
     * Function for logging misc events.
     * @param {string} message Message to log.
     * @returns {void}
     * @private
     */
    log(message) {
        if (!message) return
        if (this.options.logging) console.log(message)
    }
    /**
     * Function for sending webhook messages
     * @param {Object} data Data to send.
     * @returns {Promise<fetch>}
     */
    execWebhook(data) {
        return fetch(this.options.webhook, {
            method: 'POST',
            body: JSON.stringify(data),
            headers: { 'Content-Type': 'application/json' }
        })
    }
    /**
     * Function for fetching a 'tag' of the user
     * @param {string} id User to fetch for.
     * @returns {Promise<string>}
     */
    async fetchUser(id) {
        const req = await fetch(`https://discordbots.org/api/users/${id}`, {
            headers: { authorization: this.options.token }
        })
        const user = await req.json()
        if (!user.username || !user.discriminator) throw user
        return user.username + '#' + user.discriminator
    }
        
    _build() {
        const vote = this._onVote.bind(this)
        const check = this._onCheck.bind(this)
        const checkTime = this._onCheckInfo.bind(this)
        const index = this._index.bind(this)
        this.app.post('/vote', vote)
        this.app.get('/hasVoted', check)
        this.app.get('/getVotedTime', checkTime)
        this.app.get('/', index)
    }

    _listen() {
        this.app.listen(this.options.port, '0.0.0.0')
            .then(addr => {
                console.log('[Notice] Haruna\'s Vote Service is now Online, listening @ ', addr)
                console.log('[Notice] Haruna\'s Version: ', version)
                setInterval(() => {
                    this.store._clean(amount => {
                        this.log(`[Cron Job] Database Purged, removed ${amount} ${amount <= 1 ? 'user' : 'users'} from vote db`)
                        if (!this.options.webhook) return
                        this.execWebhook({
                            embeds: [{
                                color: 0x9B767B,
                                description: `📤 Cleaned **${amount} ${amount <= 1 ? 'user' : 'users'}** from database`,
                                timestamp: new Date(),
                                footer: {
                                    text: '💾 Haruna\'s Cleaning Job'
                                }
                            }]
                        }).catch(() => null)
                    })
                    this.rss = process.memoryUsage().rss
                }, 300000)
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
            .catch(error => {
                console.error('[Error] Server failed to start. Details: ', error)
                process.exit()
            })
    }

    async _sendEmbed(user_id) {
        if (!this.options.webhook) return
        const tag = await this.fetchUser(user_id)
        await this.execWebhook({
            embeds: [{
                color: 0x095562,
                description: `📥 New User: **@${tag}** (${user_id}) voted`,
                timestamp: new Date(),
                footer: {
                    text: '💾 New Vote Stored'
                }
            }]
        })
    }

    async _onVote(req, res) {
        this.requestsReceived++
        try {
            if (req.headers.authorization !== this.options.auth) {
                res.code(401)
                this.log(`[Notice] Rejected Post Request with IP ${req.ip}`)
                return 'Unauthorized'
            }
            const duration = Date.now() + this.options.length
            await this.store.put(req.body.user, { time: duration, isWeekend: req.body.isWeekend })
            this.log(`[Notice] New vote stored, Duration: ${Math.floor((duration - Date.now()) / 1000 / 60 / 60)} hrs, user_id: ${req.body.user}, isWeekend: ${req.body.isWeekend}.`)
            await this._sendEmbed(req.body.user).catch(() => null)
            return 'Success'
        } catch (error) {
            console.error(error)
            res.code(500)
            return 'Failed'
        }
    }

    async _onCheck(req, res) {
        this.requestsReceived++
        try {
            if (req.headers.authorization !== this.options.auth) {
                res.code(401)
                this.log(`[Notice] Rejected hasVoted Request with IP ${req.ip}`)
                return 'Unauthorized'
            }
            if (!req.query.id) {
                res.code(400)
                return 'The "id" was not found in query string'
            }
            const user = await this.store.get(req.query.id)
            this.log(`[Notice] Checked Vote for user_id ${req.query.id}. Time Left: ${user ? `${((user.time - Date.now()) / 1000 / 60 / 60).toFixed(1)} hr(s).` : 'Not in Database.'}`)
            return req.headers.checkWeekend ? (user && user.isWeekend) : !!user
        } catch (error) {
            console.error(error)
            res.code(500)
            return 'Failed'
        }
    }

    async _onCheckInfo(req, res) {
        this.requestsReceived++
        try {
            if (req.headers.authorization !== this.options.auth) {
                res.code(401)
                this.log(`[Notice] Rejected getVotedTime Request with IP ${req.ip}`)
                return 'Unauthorized'
            }
            if (!req.query.id) {
                res.code(400)
                return 'The "id" was not found in query string'
            }
            const user = await this.store.get(req.query.id)
            this.log(`[Notice] Checked Vote Time for user_id ${req.query.id}. Time Left: ${user ? `${((user.time - Date.now()) / 1000 / 60 / 60).toFixed(1)} hr(s).` : 'Not in Database.'}`)
            return (user && req.query.id) ? user.time - Date.now() : false
        } catch (error) {
            console.error(error)
            res.code(500)
            return 'Failed'
        }
    }

    async _index(req, res) {
        this.requestsReceived++
        res.type('text/html')
        return `
<p align="center"> 
  <font face="Trebuchet MS" color="#708090">Haruna: The Discord Bot List Webhook Handler</font>
  <br>
  <font size="3" face="Trebuchet MS" color="#708090">Haruna's Version ${version}</font>
  <br>
  <font size="3" face="Trebuchet MS" color="#708090">Current RAM Usage: ${this.ram} || Request(s) Received Since Boot: ${this.requestsReceived}</font>
  <br>
  <a href="https://github.com/Deivu/Haruna">
  <font size="3" face="Trebuchet MS" color="#708090">Click this for Github Source</font>
</a>
</p>
<p align="center">
  <img src="https://vignette.wikia.nocookie.net/kancolle/images/6/61/Haruna_Shopping_Full.png/revision/latest/" height="549" width="200">
</p>`
    }
}

module.exports = Haruna
