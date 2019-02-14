/*
  API Usage Example for checking votes using node-fetch npm pacakage
*/
const fetch = require('node-fetch')
fetch('http://{hostname}:{port}/hasVoted/', {
    headers: { 'authorization': 'Your Password', 'user_id': '1234567890' }
}).then((response) => {
    if (response.ok) {
        response.json().then(console.log)
    }
})