package shipgirl.misc;

import shipgirl.Haruna;
import shipgirl.Sortie;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HarunaUtil {
    private final Haruna haruna;

    public HarunaUtil(Haruna haruna) {
        this.haruna = haruna;
    }

    public void formatTrace(String message, StackTraceElement[] traces) {
        List<String> trace = Arrays.stream(traces)
                .map(v -> v.toString() + "\n")
                .collect(Collectors.toList());
        trace.add(0, message + "\n");
        haruna.harunaLog.error(trace.toString());
    }

    public String getLocation() {
        String dir = null;
        try {
            File file = new File(Sortie.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            dir = file.getPath().replace(file.getName(), "");
        } catch (Exception error) {
            formatTrace(error.getMessage(), error.getStackTrace());
        }
        return dir;
    }
}
