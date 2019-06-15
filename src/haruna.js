global.Promise = require('bluebird')
const fastify = require('fastify')
const enmap = require('enmap')
const fetch = require('node-fetch')
const { version } = require('../package.json')
const defaultSettings = {
    port: 8000,
    auth: '',
    length: 43200000,
    token: '',
    logging: true,
    dir: '',
    webhook: ''
}

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
        this.options = { ...defaultSettings, ...options }
        if (!this.options.auth || !this.options.token)
            throw new Error('Authentication key or DBL token not specified')

        /**
         * An Fastify instance
         * @type {external:Fastify}
         */
        this.app = fastify()
        /**
         * An Enmap instance of currently stored votes
         * @type {external:Enmap}
         */
        this.storage = new enmap({
            name: 'votes',
            fetchAll: true,
            dataDir: this.options.dir
        })

        this.storage.defer.then(() => {
            this.app.post('/vote', this._onVote.bind(this))
            this.app.get('/hasVoted', this._onCheck.bind(this))
            this.app.get('/getVotedTime', this._onCheckInfo.bind(this))
            this.app.get('/', this._index.bind(this))
            this.app.listen(this.options.port, '0.0.0.0')
                .then((addr, error) => {
                    if (error) {
                        console.error('[Error] Server failed to start. Details: ', error.message)
                        return process.exit()
                    }
                    console.log('[Notice] Haruna\'s Vote Service is now Online, listening @ ', addr)
                    console.log('[Notice] Haruna\'s Version: ', version)
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
                const counter = this.storage.sweep(val => val.time < Date.now())
                this.log(`[Cron Job] Database Purged, removed ${counter} ${counter <= 1 ? 'user' : 'users'} from vote db`)
                if (this.options.webhook)
                    await this.execWebhook({
                        embeds: [{
                            color: 0x9B767B,
                            description: `📤 Cleaned **${counter} ${counter <= 1 ? 'user' : 'users'}** from database`,
                            timestamp: new Date(),
                            footer: {
                                text: `💾 ${this.storage.size} stored votes`
                            }
                        }]
                    }).catch(() => null)
            }, 300000)
        })
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
    /**
     * Function for sending a new vote embed.
     * @param {string} id User that voted
     * @returns {Promise<void>}
     * @private
     */
    async _send_new_vote_embed(user_id) {
        if (!this.options.webhook) return
        const tag = await this.fetchUser(user_id)
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
    /**
     * Function that handles the new votes
     * @param {Object} req The Request Object.
     * @param {Object} res The Response Object.
     * @returns {Promise<any>}
     * @private
     */
    async _onVote(req, res) {
        try {
            if (req.headers.authorization !== this.options.auth) {
                res.code(401)
                this.log(`[Notice] Rejected Post Request with IP ${req.ip}`)
                return 'Unauthorized'
            }
            if (this.storage.has(req.body.user)) this.storage.delete(req.body.user)
            const duration = Date.now() + this.options.length
            this.storage.set(req.body.user, { time: duration, isWeekend: req.body.isWeekend })
            this.log(`[Notice] New vote stored, Duration: ${Math.floor((duration - Date.now()) / 1000 / 60 / 60)} hrs, user_id: ${req.body.user}, isWeekend: ${req.body.isWeekend}.`)
            await this._send_new_vote_embed(req.body.user).catch(() => null)
            return 'Success'
        } catch (error) {
            console.error(error)
            res.code(500)
            return 'Failed'
        }
    }
    /**
     * Function that handles the hasVoted endpoint
     * @param {Object} req The Request Object.
     * @param {Object} res The Response Object.
     * @returns {Promise<any>}
     * @private
     */
    async _onCheck(req, res) {
        try {
            if (req.headers.authorization !== this.options.auth) {
                res.code(401)
                this.log(`[Notice] Rejected hasVoted Request with IP ${req.ip}`)
                return 'Unauthorized'
            }
            const user = this.storage.get(req.headers.userid)
            this.log(`[Notice] Checked Vote for user_id ${req.headers.userid}. Time Left: ${user ? `${((user.time - Date.now()) / 1000 / 60 / 60).toFixed(1)} hr(s).` : 'Not in Database.'}`)
            if (!req.headers.userid) return false
            return req.headers.checkWeekend ? (user && user.isWeekend) : !!user
        } catch (error) {
            console.error(error)
            res.code(500)
            return 'Failed'
        }
    }
    /**
     * Function that handles the getVotedTime endpoint
     * @param {Object} req The Request Object.
     * @param {Object} res The Response Object.
     * @returns {Promise<any>}
     * @private
     */
    async _onCheckInfo(req, res) {
        try {
            if (req.headers.authorization !== this.options.auth) {
                res.code(401)
                this.log(`[Notice] Rejected getVotedTime Request with IP ${req.ip}`)
                return 'Unauthorized'
            }
            const user = this.storage.get(req.headers.userid)
            this.log(`[Notice] Checked Vote Time for user_id ${req.headers.userid}. Time Left: ${user ? `${((user.time - Date.now()) / 1000 / 60 / 60).toFixed(1)} hr(s).` : 'Not in Database.'}`)
            return (user && req.headers.userid) ? user.time - Date.now() : false
        } catch (error) {
            console.error(error)
            res.code(500)
            return 'Failed'
        }
    }
    /**
     * Function that handles landing page.
     * @param {Object} req The Request Object.
     * @param {Object} res The Response Object.
     * @returns {Promise<any>}
     * @private
     */
    async _index(req, res) {
        res.type('text/html')
        return `
<p align="center"> 
  <font face="Trebuchet MS" color="#708090">Haruna: The Discord Bot List Webhook Handler</font>
  <br>
  <font size="3" face="Trebuchet MS" color="#708090">Haruna's Version ${version}</font>
  <br>
  <a href="https://github.com/Deivu/Haruna">
    <font size="3" face="Trebuchet MS" color="#708090">Click this for Github Source</font>
  </a>
</p>
<p align="center">
  <img src="https://vignette.wikia.nocookie.net/kancolle/images/6/61/Haruna_Shopping_Full.png/revision/latest/">
</p>`
    }
}

module.exports = Haruna
