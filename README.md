# Haruna
<p align="center">
  <img src="https://vignette.wikia.nocookie.net/kancolle/images/1/1a/Haruna_Kai_Ni_Summer_Full.png/revision/latest?cb=20160801085517">
</p>

The ShipGirl Project. Haruna, the helping hand of Kashima. ``(c) Kancolle for Haruna.``

Simple webhook vote handler for [Discord Bot List](https://discordbots.org/) to help with [Kashima](https://discordbots.org/bot/424137718961012737)

Why Haruna? Cause Haruna is cute uwu.

This API / webhook handler is ~~oversimplified~~simple to use. As long as you can start it, it will handle everything for you to save you the hassle of creating your own vote handler.
> Now even comes with [`server.js`](https://github.com/deivu/haruna/blob/master/server.js), all you need to run to have your vote handler ready!

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
  <String> user_id - User ID to check
  <Boolean> checkWeekend - (Optional) CHeck if vote multiplier on this user is enabled (weekend special)
Return value:
  <Boolean> - `true` if user voted or vote multiplier is enabled, `false` if user did not vote.
```

### `GET` /getVotedTime
To check how long the user will stay in database.
```
HTTP Headers:
  <String> authorization - Authorization key
  <String> user_id - User ID to check
Return value:
  <Number|Boolean> - The duration of how long the user will be in cache (in ms), `false` if the user haven't voted.
```

## Some Documentation?
[Code Documentation]()

## Example Code in starting the API
Check [`server.js`](https://github.com/deivu/haruna/blob/master/server.js)


## How to use this?
1. Clone this repo
2. Make a file `config.json` that contains options for Haruna
3. Run [`server.js`](https://github.com/deivu/haruna/blob/master/server.js)
4. If you think [`server.js`](https://github.com/deivu/haruna/blob/master/server.js) is not enough, then you should use your own parent process, or maybe running it on your bot (not recommended)

## Notes
* You might want to run it with [`pm2`](http://pm2.keymetrics.io/) or system services so any unexpected restarts will be handled and better logging
* This is a standalone API, so as long as the parent process is alive, it should not die.