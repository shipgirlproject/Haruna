package haruna.misc;

import com.sun.management.OperatingSystemMXBean;
import io.vertx.core.json.JsonObject;
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
        try {
            double totalMemory = runtime.totalMemory();
            double freeMemory = runtime.freeMemory();
            stats = new JsonObject()
                    .put("haruna_version", harunaServer.config.version)
                    .put("saved_data", harunaServer.store.savedCount())
                    .put("api_requests_received", harunaServer.requestsReceived)
                    .put("program_uptime", TimeUtil.getSimpleTimeFormat(this.harunaServer.runtime.getUptime()))
                    .put("cpu_usage", Math.round(system.getProcessCpuLoad() * 100) + " %")
                    .put("used_memory", harunaServer.harunaUtil.convertRam(totalMemory - freeMemory))
                    .put("allocated_free", harunaServer.harunaUtil.convertRam(freeMemory))
                    .put("allocated_reserved", harunaServer.harunaUtil.convertRam(totalMemory))
                    .put("maximum_allocatable", harunaServer.harunaUtil.convertRam((double) runtime.maxMemory()))
                    .put("stats_last_updated", Instant.now().toEpochMilli());
        } catch (Exception error) {
            harunaServer.harunaLog.error(error);
            return;
        }
        harunaServer.harunaLog.debug("Cached JsonStats Object updated.");
    }
}
