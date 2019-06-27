package moe.structure;

import java.time.Instant;

public class HarunaUser {
    public String user = null;
    public long timestamp = 0;
    public Boolean weekend = null;

    public int getRemaining() {
        return Instant.ofEpochMilli(timestamp).compareTo(Instant.now());
    }
}
