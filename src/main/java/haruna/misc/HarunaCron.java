package haruna.misc;

import haruna.HarunaServer;

public class HarunaCron {
    private final HarunaServer harunaServer;

    public HarunaCron (HarunaServer harunaServer) { this.harunaServer = harunaServer; }

    public void execute() {
        try {
            int cleaned = harunaServer.store.clean();
            sendEmbed(cleaned, false);
            harunaServer.harunaLog.debug("Cleaner Executed. Cleaned " + cleaned + " users from DB.");
        } catch (Exception error) {
            harunaServer.harunaLog.error(error);
            sendEmbed(0, true);
        }
    }

    private void sendEmbed(int amount, Boolean errored) {
        if (errored) {
            harunaServer.rest.sendEmbed(
                    0xdd666c,
                    "\\⚠ **Prune failed**. Check logs for more info.",
                    "⏲ || Haruna's Cron Job"
            );
            return;
        }
        if (!harunaServer.config.debug) return;
        harunaServer.rest.sendEmbed(
                0x66362d,
                "\\➖ Pruned **" + amount + "** of saved data in database",
                "⏲ || Haruna's Cron Job"
        );
    }
}
