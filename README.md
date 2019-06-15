# Haruna
<p align="center">
  <img src="https://vignette.wikia.nocookie.net/kancolle/images/6/61/Haruna_Shopping_Full.png/revision/latest/">
</p>

The ShipGirl Project. Haruna, the helping hand of Kashima. ``(c) Kancolle for Haruna.``

Simple webhook vote handler for [Discord Bot List](https://discordbots.org/) to help with [Kashima](https://discordbots.org/bot/424137718961012737)

Why Haruna? Cause Haruna is cute uwu.

This API / webhook handler is ~~oversimplified~~simple to use. As long as you can start it, it will handle everything for you to save you the hassle of creating your own vote handler.

### Use case of this API.
> You want a voting server that is running independently from your bot, so that even your bot crashes, yey votes will still register

> You want a non blocking voting check "Heck this is an API lmao"

> You don't want to make your own webserver api cause it is a hassle, Yes this is perfect then

> You want a fast and tested API, yes, I tested this on prod lmao

### Now more easier to host
> Now even comes with [`server.js`](https://github.com/deivu/haruna/blob/master/server.js), all you need to run to have your vote handler ready!

> Now also comes with an API wrapper [`HarunaRequest.js`](https://github.com/Deivu/Haruna/blob/master/wrapper/HarunaRequest.js) for newbies that cant code a request.

> Just copy the repo and put it on your server, then start server.js after you configured the config files, ain't that easy?

### Scroll Down at the bottom for Installation Notes, Support for using this and some more Info.

## API Endpoints
### `POST` /vote
This is the one that you use for DBL, this is where DBL will send the votes from your bot.

<p align="center">
  <img src="https://i.imgur.com/fBhIdVC.jpg">
</p>

### `GET` /hasVoted
To check if someone voted or not.
```
HTTP Headers:
  <String> authorization - Authorization key
  <String> userid - User ID to check
  <Boolean> checkWeekend - (Optional) CHeck if vote multiplier on this user is enabled (weekend special)
Return value:
  <Boolean> - `true` if user voted or vote multiplier is enabled, `false` if user did not vote.
```

### `GET` /getVotedTime
To check how long the user will stay in database.
```
HTTP Headers:
  <String> authorization - Authorization key
  <String> userid - User ID to check
Return value:
  <Number|Boolean> - The duration of how long the user will be in cache (in ms), `false` if the user haven't voted.
```

## Some Documentation?
[Code Documentation](https://deivu.github.io/Haruna?api)

## Example Code in starting the API
Check [`server.js`](https://github.com/deivu/haruna/blob/master/server.js)

## Example Code in Querying the API?
Check [`RequestWithoutWrapper.js`](https://github.com/Deivu/Haruna/blob/master/examples/RequestWithoutWrapper.js)

## API Wrapper?
Check [`HarunaRequest.js`](https://github.com/Deivu/Haruna/blob/master/wrapper/HarunaRequest.js)

## How to use this?
Step 1: Clone this repo

Step 2: Make a file `config.json`/`config.js` that contains options for Haruna (Options are passed to the constructor, check [here](https://deivu.github.io/Haruna?api))

> Config file can be both `.js` or `.json`. There are examples for them, see [configExample.json](https://github.com/deivu/haruna/blob/master/configExample.json) and [configExample.js](https://github.com/deivu/haruna/blob/master/configExample.js)

Step 3: Run [`server.js`](https://github.com/deivu/haruna/blob/master/server.js)

Step 4: Check [`RequestWithoutWrapper.js`](https://github.com/Deivu/Haruna/blob/master/examples/RequestWithoutWrapper.js) if you dont know how to make a simple HTTP request smh.

Step 5: If you think [`server.js`](https://github.com/deivu/haruna/blob/master/server.js) is not enough, then you should use your own parent process, or maybe running it on your bot (not recommended)

Step 6: Read the [`examples`](https://github.com/Deivu/Haruna/tree/master/examples) so that you can see 2 ways to query your request to this api

## Support
**We provide support for usage of this API in our Official Server [in HERE](https://discordapp.com/invite/FVqbtGu)**

Ask on **#Bot-Support** channel and make sure you indicate support for this API.

## Notes
* You might want to run Haruna with [`pm2`](http://pm2.keymetrics.io/) or native services so any unexpected restarts will be handled and better logging
* This is a standalone API, so as long as the parent process is alive, disconnections should be handled automatically.
