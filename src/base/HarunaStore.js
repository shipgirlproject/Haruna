/*
MIT License

Copyright (c) 2019 Deivu (Saya) and takase1121 (Takase)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
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