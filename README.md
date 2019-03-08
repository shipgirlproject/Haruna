# Haruna
<p align="center">
  <img src="https://vignette.wikia.nocookie.net/kancolle/images/1/1a/Haruna_Kai_Ni_Summer_Full.png/revision/latest?cb=20160801085517">
</p>

The ShipGirl Project. Haruna, the helping hand of Kashima. ``(c) Kancolle for Haruna.``

Simple webhook vote handler for Discord Bot List https://discordbots.org/ to help with Kashima https://discordbots.org/bot/424137718961012737

Why Haruna? Cause Haruna is cute uwu.

This api / webhook vote handler is oversimplified. As long as you can start it, it will handle everything for you to save you the hassle of creating your own vote handler.

## API Endpoints
### /vote/ "POST"
This is the one that you use for DBL, this is where DBL will send the votes from your bot.

<p align="center">
  <img src="https://i.imgur.com/fBhIdVC.jpg">
</p>

### /hasVoted/ "GET"
This is where you check if someone voted or not.

`Needs the FF headers`

`"authorization": "Same on what you used for your DBL webhook password"`

`"checkWeekend": "(optional) boolean, true or false. If you want to check if the user voted in weekend"`

`"user_id": "ID of the user you want to check"`

Returns "TRUE" if the user voted, "FALSE" if not.

### /getVotedTime/ "GET"
This is if you want to check how long the user will stay in database.

`Needs the FF headers`

`"authorization": "Same on what you used for your DBL webhook password"`

`"user_id": "ID of the user you want to check"`

Returns the "MS" of how long the user will be in cache, "FALSE" if the user haven't voted.

## Some Documentation?
Class Haruna

Constructor
```js
new Haruna(dbl_webhook_port, dbl_webhook_pw, user_lifetime_in_db_in_ms, dbl_token, db_location<optional>)
```
Properties
```
port: port you used in the api
auth: auth you specified for both GET and POST requests
length: user lifetime you specifed in MS before they get deleted
dbl_token: your bot token in DBL (only used to verify who is the user who voted)
```

Methods
```js
setWebhook(webhook_id, webhook_token) 
 > Sets the webhook where Haruna will send a notifcation when someone votes, when the vote service is online and when she executes her cron job for clearing users. Returns Nothing and not a promise
```

## Example Code in starting the API
```js
const Haruna = require('./index.js')
const handler = new Haruna('open_dblwebhook_port', 'dblwebhook_password', 'user_lifetime_in_db', 'dbl_token')
handler.setWebhook('discord_webhook_id', 'discord_webhook_token')
```


## How to use this?
`1. Clone this Repo`

`2. Put it on a folder separated from your bot or along side your bot (If you have a server, If not then cloning it to a hosting like glitch will do, skip this step if thats the case)`

`3. Copy and modify the example code ABOVE as you see fit for starting the api`

`4. Look on client.js in examples folder to see how you send a request to use this api.`

You may want to fire it up with pm2 or something so that it will not close, as well restart it if it crashes due to some reason

`Note: This process will run SEPARATELY from your bot. It's in its own process, so even your bot crashes, the api won't`
