# Haruna
<p align="center">
  <img src="https://vignette.wikia.nocookie.net/kancolle/images/1/1a/Haruna_Kai_Ni_Summer_Full.png/revision/latest?cb=20160801085517">
</p>

The ShipGirl Project. Haruna, the helping hand of Kashima. ``(c) Kancolle for Haruna.``

Simple webhook vote handler for Discord Bot List https://discordbots.org/ to help with Kashima https://discordbots.org/bot/424137718961012737

Why Haruna? Cause Haruna is cute uwu.

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

## How to use this?
`1. Clone this Repo`

`2. Look on examples folder and look in server.js then change stuffs as you see fit.`

`3. Put it on a folder separated from your bot or along side your bot`

`4. Then just use the server.js in examples folder to start this api. (Provided that you modified it to your needs)`

`5. Look on client.js in examples folder to see how you send a request to use this api.`

You may want to fire it up with pm2 or something so that it will not close, as well restart it if it crashes due to some reason

`Note: This process will run SEPARATELY from your bot. It's in its own process, so even your bot crashes, the api won't`
