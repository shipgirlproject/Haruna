const fetch = require('node-fetch')

class HarunaRequest {
    constructor(base, authentication) {
        Object.defineProperty(this, 'auth', { value: authentication });
        this.base = base;
    }

    hasVoted(userID) {
        return this._request('/hasVoted', userID);
    }

    getVotedTime(userID) {
        return this._request('/getVotedTime', userID);
    }

    async _request(endpoint, userID) {
        if (!userID)
            throw new Error('UserID not specified.');

        const url = new URL(this.base + endpoint);
        url.search = new URLSearchParams({ id: userID });
        const res = await fetch(url.toString(), {
            headers: { 'authorization': this.auth }
        });

        if (res.status !== 200) 
            throw new Error(`Haruna_API_Error. Status Code: ${res.status}`);

        return res.json();
    }
}

module.exports = HarunaRequest
