package moe;

import moe.routes.NewVote;
import moe.routes.VoteInfo;

public class Sortie {
    public static void main(String[] args) {
        Haruna haruna = new Haruna();

        haruna.routes(
                new NewVote(haruna),
                new VoteInfo(haruna)
        );
    }
}
