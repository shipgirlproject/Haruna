package moe.misc;

import moe.Haruna;
import moe.Sortie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This is just a wrapper around INFO logs.
public class HarunaLog {
    private final Logger HarunaLog = LoggerFactory.getLogger(Sortie.class);
    private final Haruna haruna;

    public HarunaLog(Haruna haruna) {
        String art = "\n" +
                "                                                  \n" +
                ",--.  ,--.                                        \n" +
                "|  '--'  | ,--,--.,--.--.,--.,--.,--,--,  ,--,--. \n" +
                "|  .--.  |' ,-.  ||  .--'|  ||  ||      \\' ,-.  | \n" +
                "|  |  |  |\\ '-'  ||  |   '  ''  '|  ||  |\\ '-'  | \n" +
                "`--'  `--' `--`--'`--'    `----' `--''--' `--`--' \n" +
                "                                                  \n";
        this.HarunaLog.info(art);
        this.haruna = haruna;
    }

    public void debug(String debug) {
        if (!haruna.config.Debug) return;
        HarunaLog.info(debug);
    }

    public void error(String error) {
        HarunaLog.error(error);
    }

    public void info(String info) {
        HarunaLog.info(info);
    }
}