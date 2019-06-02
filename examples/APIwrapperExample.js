const HarunaRequest = require('../wrapper/HarunaRequest.js')

const Request = new HarunaRequest('http://123.456.78.90:5000', 'sneakpassword')

// Checking for votes
Request.hasVoted('12345discordid')
    .then(console.log)
    .catch(console.error)

// Checking for how long they will be in cache
Request.getVotedTime('696969userid')
    .then(console.log)
    .catch(console.error)
