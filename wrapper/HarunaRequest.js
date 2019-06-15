const fetch = require('node-fetch')

class HarunaRequest {
    constructor(base, authentication) {
        Object.defineProperty(this, 'auth', { value: authentication })
        this.baseurl = base
    }

    hasVoted(userID) {
        return this._request('/hasVoted', userID)
    }

    getVotedTime(userID) {
        return this._request('/getVotedTime', userID)
    }

    _request(endpoint, userID) {
        return fetch(this.baseurl + endpoint, {
            headers: { 'authorization': this.auth, 'userid': userID }
        }).then((res, error) => {
            if (error)
                throw error
            if (!res.ok)
                throw new Error('Haruna_API_ERROR: Response received is not ok.')
            if (res.status !== 200)
                throw new Error(`Haruna_API_ERROR: ${res.status}: ${res.body}`)
            return res.json()
        })
    }
}

module.exports = HarunaRequest
