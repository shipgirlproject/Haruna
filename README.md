# Haruna
<p align="center">
  <img src="https://vignette.wikia.nocookie.net/kancolle/images/6/61/Haruna_Shopping_Full.png/revision/latest/">
</p>

The ShipGirl Project. Haruna, the helping hand of Kashima. `(c) Kancolle for Haruna.`

> This is the older, JavaScript and Node.js based Haruna. The newer ones uses Java and all the development will be continued there. There is no guarantee if this branch will be maintained.

Simple webhook vote handler for [Discord Bot List](https://discordbots.org/) to help with [Kashima](https://discordbots.org/bot/424137718961012737)

This API / webhook handler is ~~oversimplified~~ simple to use. As long as you can start it, it will handle everything for you to save you the hassle of creating your own vote handler.

### Why Haruna?

> Fast and reliable.

> Stand alone process that dont interfere with your bot.

> Automatic Management of Votes, haha yes you don't even need to do anything other than to configure this.

> Really cute girl OWO.

> And above of all, Haruna is a waifu material.

### Now more easier to host
> Now even comes with [`server.js`](https://github.com/deivu/haruna/blob/master/server.js), all you need to run to have your vote handler ready! (after you configured your settings)

> Just copy the repo and put it on your server, then start server.js after you configured the config files, ain't that easy?

> Configuration Files Examples is at config_examples folder of this repo. Drag one of those to the server.js directory, and fill it up with the needed settings.

### Scroll Down at the bottom for Installation, API Wrapper, and Support for using this.

## API Endpoints
### `POST` /vote
This is the one that you use for DBL, this is where DBL will send the votes from your bot.

<p align="center">
  <img src="https://i.imgur.com/fBhIdVC.jpg">
</p>

### `GET` /hasVoted
To check if someone voted or not.
```
Query String:
  <String> id: 'user_id'
HTTP Headers:
  <String> authorization - Authorization key
  <Boolean> checkWeekend - (Optional) CHeck if vote multiplier on this user is enabled (weekend special)
Return value:
  <Boolean> - `true` if user voted or vote multiplier is enabled, `false` if user did not vote.
```

### `GET` /getVotedTime
To check how long the user will stay in database.
```
Query String:
  <String> id: 'user_id'
HTTP Headers:
  <String> authorization - Authorization key
Return value:
  <Number|Boolean> - The duration of how long the user will be in cache (in ms), `false` if the user haven't voted.
```

## Some Documentation?
[Code Documentation](https://deivu.github.io/Haruna?api)

## Example Code in starting the API
Check [`server.js`](https://github.com/deivu/haruna/blob/master/server.js)

## API Wrapper?
Check [`HarunaRequest.js`](https://github.com/Deivu/Haruna/blob/master/wrapper/HarunaRequest.js)
```js
// Example Code
const HarunaWrapper = require('./wrapper/HarunaRequest.js');
const Haruna = new HarunaWrapper('http://example.com:1234', 'password')
Haruna.hasVoted('user_id').then(console.log)
Haruna.getVotedTime('user_id').then(console.log)
```

## How to use this?
Step 1: Clone this repo

Step 2: Make a file `config.json`/`config.js` that contains options for Haruna (Options are passed to the constructor, check [here](https://deivu.github.io/Haruna?api)) Configuration Files Examples is at config_examples folder of this repo.

Step 3: Run [`server.js`](https://github.com/deivu/haruna/blob/master/server.js)

Step 4: [`HarunaRequest.js`](https://github.com/Deivu/Haruna/blob/master/wrapper/HarunaRequest.js) is the wrapper around the /GET requests of this api. Ez Pz to use isn't it? Example code is at the `API Wrapper?` section of this readme


## Support
**We provide support for usage of this API in our Official Server [in HERE](https://discordapp.com/invite/FVqbtGu)**

Ask on **#Bot-Support** channel and make sure you indicate support for this API.

## Notes
* You might want to run Haruna with [`pm2`](http://pm2.keymetrics.io/) or native services so any unexpected restarts will be handled and better logging
* This is a standalone API, so as long as the parent process is alive, disconnections should be handled automatically.
