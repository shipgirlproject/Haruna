package haruna.structure;

import java.time.Instant;

public class HarunaUser {
    public String user = null;
    public long timestamp = 0;
    public Boolean weekend = null;

    public long getRemaining() {
        return timestamp - Instant.now().toEpochMilli();
    }
}
