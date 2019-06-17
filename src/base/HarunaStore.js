class HarunaStore {
    constructor(Haruna) {
        console.log('[Notice] Initializing Haruna\'s Database')
        Object.defineProperty(this, 'haruna', { value: Haruna })
        Object.defineProperty(this, 'location', { value: process.cwd() })
        this.init()
            .then(() => console.log('[Notice] Haruna\'s Database Initialized'))
    }

    get name() { return 'Base Store' }

    async init() {
        console.log('[Warning] If you see this message, that means the data store is not properly implemented. Please contact the author.')
    }

    async put(id, value) {
        console.log('[Warning] If you see this message, that means the data store is not properly implemented. Please contact the author.')
    }

    async get(id) {
        console.log('[Warning] If you see this message, that means the data store is not properly implemented. Please contact the author.')
    }

    _clean(callback) {
        console.log('[Warning] If you see this message, that means the data store is not properly implemented. Please contact the author.')
    }

    _errored(error) {
        console.error(error)
        this.haruna.execWebhook({
            embeds: [{
                color: 0x9B767B,
                description: `⚠ Error in executing the Batch Clean ${error}`,
                timestamp: new Date(),
                footer: {
                    text: this.name
                }
            }]
        })
            .catch(console.error)
    }
}
module.exports = HarunaStore