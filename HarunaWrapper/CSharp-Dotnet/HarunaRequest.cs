using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using Newtonsoft.Json.Linq;

namespace Haruna {
    public class HarunaRequest {
        const string Url = "https://amanogawa.moe/haruna";
        const string Auth = "my_secret_password";

        public async Task<JObject> GetVoteAsync(ulong user_id) => await FetchAsync("/voteInfo", user_id);

        public async Task<JObject> GetStatsAsync() => await FetchAsync("/stats");

        async Task<JObject> FetchAsync(string endPoint, ulong userId = 0) {
            string baseUrl = Url + endPoint;

            if (userId != 0)
                baseUrl += "?user_id=" + userId;
            
            try {
                using (HttpClient client = new HttpClient()) {
                    client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue(Auth);

                    using (HttpResponseMessage res = await client.GetAsync(baseUrl)) {
                        using (HttpContent content = res.Content) {
                            string data = await content.ReadAsStringAsync();

                            if (content != null)
                                return JObject.Parse(data);
                        }
                    }
                }
            } catch (Exception e) {
                Console.WriteLine(e);
            }

            return null;
        }
    }
}
