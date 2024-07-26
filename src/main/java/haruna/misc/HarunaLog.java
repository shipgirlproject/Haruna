package haruna.misc;

import haruna.HarunaServer;
import haruna.Sortie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarunaLog {
    private final Logger HarunaLog = LoggerFactory.getLogger(Sortie.class);
    private final HarunaServer harunaServer;

    public HarunaLog(HarunaServer harunaServer) {
        String art = "\n" +
                "                                                  \n" +
                ",--.  ,--.                                        \n" +
                "|  '--'  | ,--,--.,--.--.,--.,--.,--,--,  ,--,--. \n" +
                "|  .--.  |' ,-.  ||  .--'|  ||  ||      \\' ,-.  | \n" +
                "|  |  |  |\\ '-'  ||  |   '  ''  '|  ||  |\\ '-'  | \n" +
                "`--'  `--' `--`--'`--'    `----' `--''--' `--`--' \n" +
                "                                                  \n";
        this.HarunaLog.info(art);
        this.harunaServer = harunaServer;
    }

    public void debug(String debug) {
        if (!harunaServer.config.debug) return;
        HarunaLog.info(debug);
    }

    public void info(String info) {
        HarunaLog.info(info);
    }

    public void warn(String msg) { HarunaLog.warn(msg); }

    public void error(String msg) {
        HarunaLog.error(msg);
    }

    public void error(Throwable error) { HarunaLog.error(error.toString(), error); }

    public void error(String msg, Throwable error) { HarunaLog.error(msg, error); }

}