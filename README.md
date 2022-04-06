# Haruna
<p align="center">
  <img src="https://vignette.wikia.nocookie.net/kancolle/images/6/61/Haruna_Shopping_Full.png/revision/latest/">
</p>

The ShipGirl Project; Haruna. `(c) Kancolle for Haruna.`

# Why Haruna ?
<p align="center">
  <img src="https://i.imgur.com/7Yiqs4D.png">
</p>

> Fast and reliable.

> Run and forget, Haruna can run with **99.9% uptime**.

> Rest based API for your easy vote checks **without opening a webserver** on your bot.

> Webhook based API is also available if you are someone who needs both the **API** and **Realtime** vote handling.

# Documentation

## Rest API

### `POST` /newVote
The endpoint that is exposed to Top.gg (Discord Bot List); Refer to the image below.

<p align="center">
  <img src="https://i.imgur.com/TaVWQ5y.png">
</p>

### `GET` /voteInfo
The endpoint which you can use to check for votes.

Headers: 
```js
{
  "authorization": "the authorization key you have set on Top.gg (Discord Bot List) webhook"
}
```

Query String: `user_id`

Returns: JSON string.
```js
// User Voted
{
  "user": "23213512",
  "timestamp": 432483204, 
  "isWeekend": true,
  "timeLeft": 274013
}
// User Didn't Vote
{
  "user": false
}
```

### `GET` /stats
Returns: Current status of server in JSON string.

## Webhook API [1.4.0 and later]

> If enabled, it will send a POST request containing the user who voted in the URL of your choice.

> For checking the request validity, you can check the authorization header as it will send the same **RestAuth** you have set on your config

Returns: JSON string.
```js
{
  "user": "389138132391230131031903",
  "isWeekend": true
}
```

# Haruna's API Wrappers

[Java](https://github.com/Deivu/Haruna/tree/master/HarunaWrapper/java)

[Javascript](https://github.com/Deivu/Haruna/tree/master/HarunaWrapper/Javascript-Node.js)

[C#](https://github.com/Deivu/Haruna/tree/master/HarunaWrapper/CSharp-Dotnet)

> you can also create your own and PR if you want to contribute it.

# How to Host

## Manually

1. Download the latest `haruna.jar` from Github Releases. [Click me](https://github.com/Deivu/Haruna/releases)

2. Download `HarunaConfig.json` from github. [Click me](https://github.com/Deivu/Haruna/blob/master/config_example/HarunaConfig.json)

3. Configure `HarunaConfig.json` according to your liking and put it BESIDE haruna.jar

4. Start the server via `java -jar haruna.jar`

5. To verify Haruna is working, navigate to `http://localhost:port_you_specified/` or `http://your_server_ip:the_port_you_specified/`. [Example](http://it-snake.net:1101/)

## Docker

1. Clone this repository.

2. Configure `HarunaConfig.json` that you will find on `config_example/` folder.

3. In a command line, run: `docker build -f Dockerfile -t deivu/haruna .` on the repository folder.

4. Start the server via `docker run -d deivu/haruna`.

# Haruna's config file example & explanation.
```js
{
  "RestAuth": "weeb_handler", 
  "DBLAuth": "JRIrjrqwrpURJQWOPRj_rfnQEUi_KRqop",
  "Prefix": "/webserver",
  "Weebhook": "https://discordapp.com/api/webhooks/84293482482420424024802/sneaky_token_OWO",
  "PostWeebhook": "localhost:6969",
  "Debug": true,
  "Port": 9696,
  "Threads": 8,
  UserTimeout: 432000000
}
```
- `RestAuth` is the Discord Bot List Webhook Authorization.
- `DBLAuth` is your token for Discord Bot List.
- `Prefix` used if you want to reverse proxy. Leave it blank if you don't need to use it (Optional)
- `Weebhook` is your Discord Webhook link (Optional)
- `PostWeebhook` is where Haruna will try to send a POST request of the user who voted [Refer Here](https://github.com/Deivu/Haruna#webhook-api-v140-and-later) (Optional)
- `Debug` is if you want to enable debug logs of Haruna (Optional, Default: false)
- `Port` is what port you want this server hosted (Optional, Default: 1024)
- `Threads` is how many threads you want this server to have (Optional, Default: Your CPU Thread Count)
- `UserTimeout` is how long the user will stay in database in ms (Optional, Default: 43200000)

# Support
**We provide support for usage of this API in our Official Server's #support channel which is [in HERE](https://discordapp.com/invite/FVqbtGu)**

> Made with ❤️ by Saya#0113 
