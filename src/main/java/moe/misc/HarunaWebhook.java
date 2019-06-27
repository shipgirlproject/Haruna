package moe.misc;

import moe.Haruna;

public class HarunaWebhook {
    private final Haruna haruna;
    private final String link;
    private final String DBLauth;

    public HarunaWebhook(Haruna haruna, HarunaConfig config) {
        this.haruna = haruna;
        this.link = config.Weebhook;
        this.DBLauth = config.DBLAuth;
    }

}
