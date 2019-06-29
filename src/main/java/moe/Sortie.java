package moe;

import moe.routes.NewVote;
import moe.routes.VoteInfo;

public class Sortie {
    public static void main(String[] args) {
        // to avoid Java 11 errors.
        System.setProperty("vertx.disableDnsResolver", "true");
        Haruna haruna = new Haruna();
        haruna.routes(
                new NewVote(haruna),
                new VoteInfo(haruna)
        );
        haruna.listen();
    }
}
