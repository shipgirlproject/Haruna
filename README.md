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

`"user_id": "ID of the user you want to check"`

Returns true if the user voted, false if not
