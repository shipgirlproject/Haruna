const fetch = require('node-fetch');
const abort = require('abort-controller');
/**
 * HarunaRequest Class
 * @param {string} url Host where Haruna Server is hosted.
 * @param {string} auth Your "RestAuth" in configuration.
 *
 * @example
 * const Haruna = new HarunaRequest("http://localhost:1024", "sneaky_password");
 */
class HarunaRequest {
    constructor(url, auth) {
        this.url = url;
        Object.defineProperty(this, 'auth', {
            value: auth
        });
    }
    /**
     * @param {string} user_id user_id of the user to check
     * @returns {Promise<JSON>} Will always contain the key "user" that can be either `false` or the `user_id` of the user who voted.
     * @example
     * const Haruna = new HarunaRequest("http://localhost:1024", "sneaky_password");
     * Haruna.getVote("12222233333333444444").then(console.log);
     */
    getVote(user_id) {
        if (!user_id) throw new Error('No user_id specified.');
        return this._fetch('/voteInfo', user_id);
    }
    /**
     * @returns {Promise<JSON>} The current state of the server.
     * @example
     * const Haruna = new HarunaRequest("http://localhost:1024", "sneaky_password");
     * Haruna.getStats().then(console.log);
     */
    getStats() {
        return this._fetch('/stats');
    }

    _fetch(endpoint, user_id) {
        const url = new URL(this.url + endpoint);
        if (user_id) url.search = new URLSearchParams({
            user_id
        });

        const controller = new abort();
        const timeout = setTimeout(() => controller.abort(), 20000);

        return fetch(url.toString(), {
                headers: {
                    'authorization': this.auth
                },
                signal: controller.signal
            })
            .then((res) => {
                if (res.status !== 200) throw new Error(`Status Code: ${res.status}`)
                return res.json()
            })
            .finally(() => clearTimeout(timeout));
    }
}

module.exports = HarunaRequest;