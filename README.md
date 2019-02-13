# Haruna
<p align="center">
  <img src="https://vignette.wikia.nocookie.net/kancolle/images/1/1a/Haruna_Kai_Ni_Summer_Full.png/revision/latest?cb=20160801085517">
</p>

The ShipGirl Project. Haruna, the helping hand of Kashima. ``(c) Kancolle for Haruna.``

Simple webhook vote handler for Discord Bot List https://discordbots.org/ to help with Kashima https://discordbots.org/bot/424137718961012737

Why Haruna? Cause Haruna is cute uwu.

## API Endpoints
### /vote/ "Post"
This is the one that you use for DBL, this is where DBL will send the votes from your bot.

### /hasVoted/ "Get"
This is where you check if someone voted or not.

`Needs the FF headers`

`"authorization": "Same on what you used for your DBL webhook password"`

`"checkWeekend": "(optional) boolean, true or false. If you want to check if the user voted in weekend"`

`"user_id": "ID of the user you want to check"`

Returns true if the user voted, false if not

## How to use this?
`1. Clone this Repo`

`2. Look on start.js and modify things as you see fit`

`3. Put it on a folder separated from your bot or along side your bot`

`4. fire it up via node start.js`

You may want to fire it up with pm2 or something so that it will not close, as well restart it if it crashes due to some reason

`Note: This process will run SEPARATELY from your bot. It's in its own process, so even your bot crashes, the api won't`
