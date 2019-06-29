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
        context.response()
                .end(stats.toString());
    }

    public void updateJsonObject() {
        double totalMemory = runtime.totalMemory();
        double freeMemory = runtime.freeMemory();
        stats = new JsonObject()
                .put("haruna_version", haruna.config.getHarunaVersion())
                .put("cpu_usage", Math.round(system.getSystemCpuLoad() * 100) + " %")
                .put("used_memory", convertRam(totalMemory - freeMemory))
                .put("allocated_free", convertRam(freeMemory))
                .put("allocated_reserved", convertRam(totalMemory))
                .put("maximum_allocatable", convertRam((double) runtime.maxMemory()));
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
