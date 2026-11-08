package kz.astanait.visitor.util;

import java.util.Locale;

public final class SizeFormatter {
    private static final String[] UNITS = {"B", "KB", "MB", "GB", "TB"};
    private static final int STEP = 1024;

    private SizeFormatter() {
    }

    public static String format(long bytes) {
        if (bytes < STEP) {
            return bytes + " B";
        }
        double value = bytes;
        int unitIndex = 0;
        while (value >= STEP && unitIndex < UNITS.length - 1) {
            value /= STEP;
            unitIndex++;
        }
        return String.format(Locale.US, "%.1f %s", value, UNITS[unitIndex]);
    }
}
