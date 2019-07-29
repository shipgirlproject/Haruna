### HarunaRequest

Configure Url & Auth in HarunaRequest.cs first.

Example Usage
```cs
HarunaRequest haruna = new HarunaRequest();
JObject result = await haruna.GetVoteAsync(Context.User.Id);
string user = result["user"].ToString();
bool vote = (user == Context.User.Id.ToString());

if (!vote)
    Console.WriteLine("User not voted");
else
    Console.WriteLine("User voted" + result);
```