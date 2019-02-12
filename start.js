const Haruna = require('./index.js')
const yourPORT = process.env.PORT
const yourDBLPASSWORD = 'I am big weeb'
const howLongUserShouldStayinDB = 100000000 // this is in MS do the math yourself
/*
Class Haruna accepts the ff new Haruna(port, password, storagetimeinMS, optionaldirectory)
*/
const handler = new Haruna(yourPORT, yourDBLPASSWORD, howLongUserShouldStayinDB)
console.log(handler.storage.size)
