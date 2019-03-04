const fastify = require('fastify')()
const enmap = require('enmap')
const fetch = require('node-fetch')
const { WebhookClient } = require('discord.js')
const { version } = require('../package.json')
class Haruna {
    constructor(port, auth, length, dbl_token, dir) {
        if (!port || !auth || !length || !dbl_token)
            throw new Error('One of the required constructors are missing, Please make sure they are set correctly and try again')
        this.storage = new enmap({
            name: 'votes',
            fetchAll: true,
            dataDir: dir ? dir : ''
        })
        this.port = port
        this.auth = auth
        this.length = length
        this.dbl_token = dbl_token
        /*
        * 
        *
        */
        this.storage.defer.then(() => {
            fastify.post('/vote/', (req, res) => this._onVote(req, res))
            fastify.get('/hasVoted/', (req, res) => this._onCheck(req, res))
            fastify.get('/getVotedTime/', (req, res) => this._onCheckInfo(req, res))
            fastify.listen(port, '0.0.0.0', () => console.log(`[Notice] Haruna's Vote Service is now Online, listening @ ${port}`))
            setInterval(() => {
                let counter = 0;
                for (const [key, val] of this.storage) {
                    if (val.time < Date.now()) {
                        this.storage.delete(key)
                        counter++
                    }
                }
                console.log(`[Cron Job] Database Purged, removed ${counter} ${counter <= 1 ? 'user' : 'users'} from vote db`)
                if (this.webhook) this.webhook.send({
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

    setWebhook(id, token) {
        if (this.webhook) throw new Error('Webhook already running')
        this.webhook = new WebhookClient(id, token)
        if (this.webhook) this.webhook.send({
			embeds: [{
                color: 0x90ee90,
                description: '✅ **Voting API intialized**',
                timestamp: new Date(),
                footer: {
                    text: `Haruna Vote Handler v${version} `
                }
			}]
		}).catch(console.error)
    }

    async _fetch_user(id) {
        const req = await fetch(`https://discordbots.org/api/users/${id}`, {
            headers: { authorization: this.dbl_token }
        })
        const user = await req.json()
        return user.username + '#' + user.discriminator
    }

    async _send_new_vote_embed(user_id) {
        if (!this.webhook) return 
        const tag = await this._fetch_user(user_id)
        await this.webhook.send({
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
        if (req.headers.authorization !== this.auth) {
            res.status(401).send('Unauthorized')
            console.log('[Notice] Rejected Post Request, Details Below')
            console.log(req.headers)
        } else {
            if (this.storage.has(req.body.user)) this.storage.delete(req.body.user)
            const duration = Date.now() + this.length
            this.storage.set(req.body.user, { time: duration, isWeekend: req.body.isWeekend})
            res.send('Sucess')
            console.log(`[Notice] New vote stored, Duration: ${Math.floor((duration - Date.now()) / 1000 / 60 / 60)} hrs, user_id: ${req.body.user}, isWeekend: ${req.body.isWeekend}.`)
            this._send_new_vote_embed(req.body.user).catch(console.error)
        }
    }

    _onCheck(req, res) {
        if (req.headers.authorization !== this.auth) {
            res.status(401).send('Unauthorized')
            console.log('[Notice] Rejected hasVoted Request, Details below')
            console.log(req.headers)
        } else {
            const user = this.storage.get(req.headers.user_id)
            if (req.headers.user_id) {
                if (req.headers.checkWeekend) {
                    res.send(user && user.isWeekend)
                } else {
                    res.send(user ? true : false)
                }
            } else res.send(false)
            console.log(`[Notice] Checked Vote for user_id ${req.headers.user_id}. Time Left: ${user ? `${((user.time - Date.now()) / 1000 / 60 / 60).toFixed(1)} hr(s).`: 'Not in Database.'}`)
        }
    }

    _onCheckInfo(req, res) {
        if (req.headers.authorization !== this.auth) {
            res.status(401).send('Unauthorized')
            console.log('[Notice] Rejected getVotedTime Request, Details below')
            console.log(req.headers)
        } else {
            const user = this.storage.get(req.headers.user_id)
            if (user && req.headers.user_id) {
                res.send(user.time - Date.now())
            } else res.send(false)
            console.log(`[Notice] Checked Vote Time for user_id ${req.headers.user_id}. Time Left: ${user ? `${((user.time - Date.now()) / 1000 / 60 / 60).toFixed(1)} hr(s).`: 'Not in Database.'}`)
        }
    }
}
module.exports = Haruna
