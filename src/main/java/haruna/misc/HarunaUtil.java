package haruna.misc;

import haruna.HarunaServer;
import haruna.Sortie;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HarunaUtil {
    public String convertRam(Double ram) {
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

    public String getLocation() throws URISyntaxException {
        File file = new File(Sortie.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        return file.getPath().replace(file.getName(), "");
    }
}
