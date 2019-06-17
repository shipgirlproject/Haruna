const fs = require('fs')
const { join } = require('path')
const level = require('level')

const HarunaStore = require('./base/HarunaStore.js')

class LevelStore extends HarunaStore {
    get name() { return 'Level Store' }

    async init() {
        if (!fs.existsSync(join(this.location, 'db'))) fs.mkdirSync(join(this.location, 'db'))
        if (!fs.existsSync(join(this.location, 'db', 'haruna'))) fs.mkdirSync(join(this.location, 'db', 'haruna'))
        Object.defineProperty(this, 'db', { value: level(join(this.location, 'db', 'haruna'), { valueEncoding: 'json' }) })
    }

    async put(id, value) {
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
}
module.exports = LevelStore