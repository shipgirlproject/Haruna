package moe.misc;

import com.sun.management.OperatingSystemMXBean;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import moe.Haruna;

import java.lang.management.ManagementFactory;

public class HarunaStats {
    private final OperatingSystemMXBean system = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private final Runtime runtime = Runtime.getRuntime();

    private final Haruna haruna;

    private JsonObject stats = new JsonObject();

    public HarunaStats(Haruna haruna) { this.haruna = haruna; }

    public void execute(RoutingContext context) {
        try {
            context.response().end(stats.toString());
        } catch (Exception error) {
            haruna.formatTrace(error.getMessage(), error.getStackTrace());
        }
    }

    public void updateJsonObject() {
        double totalMemory = runtime.totalMemory();
        double freeMemory = runtime.freeMemory();
        int currentVoteSaved = 0;

        try {
            currentVoteSaved = haruna.store.savedCount();
        } catch (Exception error) {
            haruna.formatTrace(error.getMessage(), error.getStackTrace());
        }

        stats = new JsonObject()
                .put("haruna_version", haruna.config.getHarunaVersion())
                .put("saved_data", currentVoteSaved)
                .put("api_requests_received", haruna.requestsReceived)
                .put("cpu_usage", Math.round(system.getSystemCpuLoad() * 100) + " %")
                .put("used_memory", convertRam(totalMemory - freeMemory))
                .put("allocated_free", convertRam(freeMemory))
                .put("allocated_reserved", convertRam(totalMemory))
                .put("maximum_allocatable", convertRam((double) runtime.maxMemory()));

        haruna.harunaLog.debug("Cached JsonStats Object updated.");
    }

    private String convertRam(Double ram) {
        String parsed;
        if (ram < 1000000000) {
            double data = Math.round(ram / 1048576);
            parsed = String.format("%s MB", data);
            return parsed;
        }
        double data = Math.round(ram / 1073741824);
        parsed = String.format("%s GB", data);
        return parsed;
    }
}
