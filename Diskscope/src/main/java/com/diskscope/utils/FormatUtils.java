package com.diskscope.utils;

public class FormatUtils {

    public static String formatBytes(long bytes) {

        if (bytes <= 0) {
            return "0 B";
        }

        String[] units = {"B", "KB", "MB", "GB", "TB"};

        int index = (int) (Math.log(bytes) / Math.log(1024));

        index = Math.min(index, units.length - 1);

        double value = bytes / Math.pow(1024, index);

        return String.format("%.2f %s", value, units[index]);
    }

    public static double getPercentage(long used, long total) {

        if (total <= 0) {
            return 0;
        }

        return (used * 100.0) / total;
    }
}
