/*
  Starting this server is as easy as
*/
const Haruna = require('../index.js')
const Client = new Haruna(1200, 'Your Password', 7000000)
/*
 Just a bit of console log here so you can see what are the properties
*/
console.log(Client.port)
console.log(Client.auth)
console.log(Client.storage.size)
/*
  Constructor 
  new Haruna(PORT, PASSWORD, CACHELIFETIME, <optional> Custom Directory for DB)
  
  Important!! CACHELIFETIME must be in Milliseconds (ms)
*/
/*
  Properties
  port = port you used to host the server on.
  auth = password for requests in this server.
  storage = "MAP" of the current cached votes.
*/