const shipgirl = require('../index.js')
const yourPORT = 69
const yourDBLPASSWORD = 'I am big Weeb'
const howLongUserShouldStayinDB = 100000000 // this is in MS do the math yourself
const directoryifyouwant = './data'

new shipgirl.Haruna(yourPORT, yourDBLPASSWORD, howLongUserShouldStayinDB, directoryifyouwant)

/*
To check for votes, if hosted on localhost, localhost:yourport
headers should have "authorization": "same password as the webhook for post" and "user_id": "id of the user you want to check"
this endpoint returns true or false only
*/