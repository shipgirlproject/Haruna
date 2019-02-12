const Haruna = require('./index.js')
const yourPORT = process.env.PORT
const yourDBLPASSWORD = 'I am big weeb'
/*
The line below this comment will determine how long the user will be in DB.
Once that time passes, it will delete that user's entry in the database and will have to
vote again. This is in MS (Miliseconds).
*/
const howLongUserShouldStayinDB = 100000000 
/*
Class Haruna accepts the ff arguments "Haruna(port, password, storagetimeinMS, optionaldirectory)"
the optionaldirectory will let you specify a different path for the database if you want. If not then dont put anything there.
*/
const handler = new Haruna(yourPORT, yourDBLPASSWORD, howLongUserShouldStayinDB)
console.log(handler.storage.size)
