/*
  API Usage Example for checking votes via /hasVoted/ endpoint using node-fetch npm pacakage
*/
const fetch = require('node-fetch')
fetch('http://{hostname}:{port}/hasVoted/', {
    headers: { 'authorization': 'Your Password', 'userid': '1234567890' }
}).then((response) => {
    if (response.ok) {
        response.json().then(console.log) // Logs False if user didn't vote, TRUE if user did vote.
    }
})
/*
  API Usage Example for checking votes via /getVotedTime/ endpoint using node-fetch npm pacakage
*/
fetch('http://{hostname}:{port}/getVotedTime/', {
    headers: { 'authorization': 'Your Password', 'userid': '1234567890' }
}).then((response) => {
    if (response.ok) {
        response.json().then(console.log) // Logs FALSE if user didn't vote, or the time left (in MS) in their vote if they voted.
    }
})