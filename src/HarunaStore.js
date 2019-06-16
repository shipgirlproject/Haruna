const fs = require('fs')
const level = require('level')

class HarunaStore {
    constructor(Haruna) {
        console.log('[Notice] Initializing Haruna\'s Database')
        Object.defineProperty(this, 'haruna', { value: Haruna })
        Object.defineProperty(this, 'location', { value: process.cwd() })
        if (!fs.existsSync(this.location + '/db')) fs.mkdirSync(this.location + '/db')
        if (!fs.existsSync(this.location + '/db/haruna')) fs.mkdirSync(this.location + '/db/haruna')
        Object.defineProperty(this, 'db', { value: level(this.location + '/db/haruna', { valueEncoding: 'json' }) })
        console.log('[Notice] Haruna\'s Database Initialized')
    }

    put(id, value) {
        return this.db.put(id, value)
    }

    async get(id) {
        let result
        try {
            result = await this.db.get(id);
        } catch (error) {
            if (!error.notFound) throw error;
            result = false;
        }
        return result;
    }

    _clean(callback) {
        let isError = false
        let index = 0
        const batch = this.db.batch()
        const stream = this.db.createReadStream()
            .on('data', data => {
                if (data.value.time < Date.now()) {
                    batch.del(data.key) 
                    index++
                }
            })
            .on('error', error => {
                isError = true
                this._errored(error)
                stream.destroy()
            })
            .on('close', () => {
                if (!isError) {
                    batch.write()
                        .then(() => callback(index))
                        .catch(error => this._errored(error))
                } else {
                    batch.clear()
                }
            })
    }

    _errored(error) {
        console.error(error)
        this.haruna.execWebhook({
            embeds: [{
                color: 0x9B767B,
                description: `⚠ Error in executing the Batch Clean ${error.toString()}`,
                timestamp: new Date(),
                footer: {
                    text: 'Haruna Store'
                }
            }]
        }).catch(console.error)
    }
}
module.exports = HarunaStore