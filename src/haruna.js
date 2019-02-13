const fastify = require('fastify')()
const enmap = require('enmap')
class Haruna {
    constructor(port, auth, length, dir) {
        this.storage = new enmap({
            name: 'votes',
            fetchAll: true,
            dataDir: dir ? dir : ''
        })
        this.auth = auth
        this.length = length
        /*
        * 
        *
        */
        this.storage.defer.then(() => {
            fastify.post('/vote/', (req, res) => this.onVote(req, res))
            fastify.get('/hasVoted/', (req, res) => this.onCheck(req, res))
            fastify.listen(port, '0.0.0.0', () => console.log(`[Notice] Haruna's Vote Service is now Online, listening @ ${port}`))
            setInterval(() => {
                let counter = 0;
                for (const [key, val] of this.storage) {
                    if (Date.now() >= val.time) this.storage.delete(key)
                }
                console.log(`[Cron Job] Database Purged, removed ${counter} users from vote db`)
            }, 300000)
        })
    }

    onVote(req, res) {
        if (req.headers.authorization !== this.auth) {
            res.status(401).send('Unauthorized')
            console.log('[Notice] Rejected Post Request, Details Below')
            console.log(req.headers)
        } else {
            if (this.storage.has(req.body.user)) this.storage.delete(req.body.user)
            this.storage.set(req.body.user, { time: Date.now() + this.length, isWeekend: req.body.isWeekend})
            res.send('Sucess')
            console.log(`[Notice] New vote stored, user_id: ${req.body.user}, isWeekend ${req.body.isWeekend}`)
        }
    }

    onCheck(req, res) {
        if (req.headers.authorization !== this.auth) {
            res.status(401).send('Unauthorized')
            console.log('[Notice] Rejected Get Request, Details below')
            console.log(req.headers)
        } else {
            if (req.headers.user_id) {
                if (req.headers.checkWeekend) {
                    const user = this.storage.get(req.headers.user_id)
                    res.send(user && user.isWeekend)
                } else {
                    res.send(this.storage.has(req.headers.user_id))
                }
            } else res.send(false)
            console.log('[Notice] A get request served')
        }
    }
}
module.exports = Haruna
