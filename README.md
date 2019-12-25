# Haruna
<p align="center">
  <img src="https://vignette.wikia.nocookie.net/kancolle/images/6/61/Haruna_Shopping_Full.png/revision/latest/">
</p>

The ShipGirl Project. Haruna, the helping hand of Kashima. `(c) Kancolle for Haruna.`

## Why Haruna ?

> Fast and reliable.

> Standalone server that manages your votes for you.

> Easy to configure, simple to run and REST Based API for your easy voting checks.

> Haruna is a really cute girl OWO

> 20/10 Waifu Material rated by me

## Server Endpoints

### `POST` /newVote
The endpoint that is exposed to Discord Bot List, Refer to the image below.

<p align="center">
  <img src="https://i.imgur.com/TaVWQ5y.png">
</p>

### `GET` /voteInfo
The endpoint which you can use to check for votes.

Headers
```js
{
  "authorization": "the authorization key you have set on Discord Bot List webhook"
}
```

Query String: <String> `user_id`

Returns: A JSON String. `timestamp, isWeekend and timeLeft` will not be available `if the user is false`
```js
{
  "user": "23213512",
  "timestamp": 432483204, 
  "isWeekend": true,
  "timeLeft": 274013
}
```

### `GET` /stats
Returns: Current status of server in JSON string.

## API Wrappers

[Javascript](https://github.com/Deivu/Haruna/tree/master/HarunaWrapper/Javascript-Node.js)

[C#](https://github.com/Deivu/Haruna/tree/master/HarunaWrapper/CSharp-Dotnet)

Or create your own and PR if you want to contribute it.

## How to Host

1. Download `haruna.jar` from Github Releases. [Click me](https://amanogawa.moe/jenkins/job/Haruna/ws/build/libs/)

2. Download `HarunaConfig.json` from github. [Click me](https://github.com/Deivu/Haruna/blob/master/config_example/HarunaConfig.json)

3. Configure `HarunaConfig.json` according to your liking and put it BESIDE haruna.jar

> Additional Geeky Settings that you can configure is here. `<optional>` means it's not a required thing to have.

```
- `RestAuth` is the Discord Bot List Webhook Authorization.
- `DBLAuth` is your token for Discord Bot List.
- `Prefix` used if you want to reverse proxy. Leave it blank if you don't need to use it. <optional>
- `Weebhook` is your Discord Webhook link, <optional>
- `Debug` is if you want to enable debug logs of Haruna <optional> <Defaults to: false>
- `Port` is what port you want this server hosted <optional> <Defaults to: 1024>
- `Threads` is how many threads you want this server to have <optional> <Defaults to: 10>
- `UserTimeout` is how long the user will stay in database in ms <optional> <Defaults to: 43200000>
```

4. Start the server via `java -jar haruna.jar`

5. To verify Haruna is working, navigate to `http://localhost:port_you_specified/` or `http://your_server_ip:the_port_you_specified/`. [Example](http://it-snake.net:1101/)

## Support
**We provide support for usage of this API in our Official Server's #support channel which is [in HERE](https://discordapp.com/invite/FVqbtGu)**

> Made with ❤️ by Saya#0113 
