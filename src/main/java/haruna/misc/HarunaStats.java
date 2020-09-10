package haruna.misc;

import com.sun.management.OperatingSystemMXBean;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import haruna.HarunaServer;

import java.lang.management.ManagementFactory;
import java.time.Instant;

public class HarunaStats {
    private final OperatingSystemMXBean system;
    private final Runtime runtime;
    private final HarunaServer harunaServer;
    private JsonObject stats;

    public HarunaStats(HarunaServer harunaServer) {
        this.system = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.runtime = Runtime.getRuntime();
        this.harunaServer = harunaServer;
        this.stats = new JsonObject();
    }

    public JsonObject getStats() { return stats; }

    public void updateJsonObject() {
        double totalMemory = runtime.totalMemory();
        double freeMemory = runtime.freeMemory();
        int currentVoteSaved = 0;
        try {
            currentVoteSaved = harunaServer.store.savedCount();
        } catch (Exception error) {
            harunaServer.harunaLog.error(error);
            return;
        }
        stats = new JsonObject()
                .put("haruna_version", harunaServer.config.HarunaVersion)
                .put("saved_data", currentVoteSaved)
                .put("api_requests_received", harunaServer.requestsReceived)
                .put("program_uptime", TimeUtil.getDurationBreakdown(this.harunaServer.runtime.getUptime(), true))
                .put("cpu_usage", Math.round(system.getSystemCpuLoad() * 100) + " %")
                .put("used_memory", harunaServer.harunaUtil.convertRam(totalMemory - freeMemory))
                .put("allocated_free", harunaServer.harunaUtil.convertRam(freeMemory))
                .put("allocated_reserved", harunaServer.harunaUtil.convertRam(totalMemory))
                .put("maximum_allocatable", harunaServer.harunaUtil.convertRam((double) runtime.maxMemory()))
                .put("stats_last_updated", Instant.now().toEpochMilli());
        harunaServer.harunaLog.debug("Cached JsonStats Object updated.");
    }
}
