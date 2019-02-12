const fastify = require('fastify')()
const enmap = require('enmap')
class Haruna {
    constructor(port, auth, length, dir) {
        this.storage = new enmap({
            name: 'votes',
            fetchAll: true,
            dataDir: dir ? dir : ''
        })
        this.auth = auth;
        this.length = length;
        /*
        * 
        *
        */
        this.storage.defer.then(() => {
            fastify.post('/vote/', (req, res) => this.onVote(req, res))
            fastify.get('/hasVoted/', (req, res) => this.onCheck(req, res))
            fastify.listen(port, '0.0.0.0', () => console.log(`[Notice] Haruna's Vote Service is now Online, listening @ ${port}`))
            setInterval(() => {
                for (const [key, val] of this.storage) {
                    if (Date.now() >= val.time) this.storage.delete(key)
                }
            }, 300000)
        })
    }

    onVote(req, res) {
        if (req.headers.authorization !== this.auth) {
            res.status(401).send('Unauthorized')
            console.log(`[Notice] Rejected Post Request\n${req.headers}`)
        } else {
            if (this.storage.has(req.body.user)) this.storage.delete(req.body.user)
            this.storage.set(req.body.user, { time: Date.now() + this.length, isWeekend: req.body.isWeekend})
            res.send('Sucess')
            console.log('[Notice] New vote stored')
        }
    }

    onCheck(req, res) {
        if (req.headers.authorization !== this.auth) {
            res.status(401).send('Unauthorized')
            console.log(`[Notice] Rejected Get Request\n${req.headers}`)
        } else {
            if (req.headers.user_id) {
                res.send(this.storage.has(req.headers.user_id))
                console.log('[Notice] A get request served')
            } else res.send('No user_id supplied')
        }
    }
}
module.exports = Haruna
