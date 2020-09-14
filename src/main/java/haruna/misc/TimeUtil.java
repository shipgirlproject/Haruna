package haruna.misc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 s1mpl3x <jan[at]over9000.eu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.time.format.DateTimeFormatter;

/**
 * Util class for time handling.
 *
 * @author Jan Straub
 */
public class TimeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Convert a millisecond duration to a string format
     *
     * @param millis
     *      A duration to convert to a string form
     * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
     */
    public static String getDurationBreakdown(long millis, final boolean showMS) {
        if (millis <= 0) return "-";

        final long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        final long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMillis(seconds);

        final StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days);
            sb.append("d ");
        }
        if (hours > 0) {
            sb.append(String.format("%02d", hours));
            sb.append("h ");
        }
        if (minutes > 0) {
            sb.append(String.format("%02d", minutes));
            sb.append("m ");
        }
        if (seconds > 0) {
            sb.append(String.format("%02d", seconds));
            sb.append("s ");
        }
        if ((seconds <= 0) && (millis > 0) && showMS) {
            sb.append(String.format("%02d", millis));
            sb.append("ms");
        }
        return sb.toString();
    }

    public static String getSimpleTimeFormat(long millis) {
        if (millis <= 0) return "-";

        final long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        final long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMillis(seconds);

        final StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days);
            sb.append(" Day(s)");
        } else if (hours > 0) {
            sb.append(String.format("%02d", hours));
            sb.append(" Hour(s)");
        } else if (minutes > 0) {
            sb.append(String.format("%02d", minutes));
            sb.append(" Minute(s)");
        } else if (seconds > 0) {
            sb.append(String.format("%02d", seconds));
            sb.append(" Second(s)");
        } else {
            sb.append("< 1 Second");
        }
        return sb.toString();
    }

    public static String getStringFromMillis(final Number millis) {
        return FORMATTER.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(millis.longValue()), ZoneId.systemDefault()));
    }
}
