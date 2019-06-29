package moe.misc;

import moe.Haruna;

import java.awt.*;

public class HarunaCron {
    private final Haruna haruna;

    public HarunaCron (Haruna haruna) { this.haruna = haruna; }

    public void execute() {
        try {
            int cleaned = haruna.store.clean();
            sendEmbed(cleaned, false);
        } catch (Exception error) {
            haruna.formatTrace(error.getMessage(), error.getStackTrace());
            sendEmbed(0, true);
        }
    }

    private void sendEmbed(int amount, Boolean errored) {
        if (errored) {
            haruna.rest.sendEmbed(
                    0xdd666c,
                    "\\⚠ **Prune failed**. Check logs for more info.",
                    "⏲ || Haruna's Cron Job"
            );
            return;
        }
        haruna.rest.sendEmbed(
                0x66362d,
                "\\➖ Pruned **" + amount + "** of saved data in database",
                "⏲ || Haruna's Cron Job"
        );
    }
}
