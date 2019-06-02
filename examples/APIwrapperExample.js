const HarunaRequest = require('../wrapper/HarunaRequest.js')

// THE LINK HERE IS YOUR SERVER IP OR DOMAIN + PORT WHERE THE API IS HOSTED.
// THE PASSWORD IS THE PASSWORD YOU USED IN DISCORD BOT LIST WEBHOOK.
const Request = new HarunaRequest('http://123.456.78.90:5000', 'sneakpassword')

// Checking for votes
Request.hasVoted('12345discorduserid')
    .then(console.log)
    .catch(console.error)

// Checking for how long they will be in cache
Request.getVotedTime('696969discorduserid')
    .then(console.log)
    .catch(console.error)
