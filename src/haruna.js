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
                    if (val.time < Date.now()) {
                        this.storage.delete(key)
                        counter++
                    }
                }
                console.log(`[Cron Job] Database Purged, removed ${counter} ${counter > 1 ? 'user' : 'users'} from vote db`)
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
            const duration = Date.now() + this.length
            this.storage.set(req.body.user, { time: duration, isWeekend: req.body.isWeekend})
            res.send('Sucess')
            console.log(`[Notice] New vote stored, duration: ${(Math.floor(duration / 1000 / 60 / 60) - Math.floor(Date.now() / 1000 / 60 / 60)).toFixed(1)} hrs, user_id: ${req.body.user}, isWeekend ${req.body.isWeekend},`)
        }
    }

    onCheck(req, res) {
        if (req.headers.authorization !== this.auth) {
            res.status(401).send('Unauthorized')
            console.log('[Notice] Rejected Get Request, Details below')
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
            if (user) {
                console.log(`[Notice] Checked Vote for user_id ${req.headers.user_id}. Time left in cache ${(Math.floor(user.time / 1000 / 60 / 60) - Math.floor(Date.now() / 1000 / 60 / 60)).toFixed(1)} hr(s)`)
            } else console.log(`[Notice] Checked Vote for user_id ${req.headers.user_id}.`)
        }
    }
}
module.exports = Haruna
