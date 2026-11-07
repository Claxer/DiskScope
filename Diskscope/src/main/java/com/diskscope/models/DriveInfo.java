package com.diskscope.models;

public class DriveInfo {

    private String name;
    private String path;
    private long totalSpace;
    private long freeSpace;
    private long usableSpace;

    public DriveInfo(String name, String path, long totalSpace,
                     long freeSpace, long usableSpace) {

        this.name = name;
        this.path = path;
        this.totalSpace = totalSpace;
        this.freeSpace = freeSpace;
        this.usableSpace = usableSpace;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public long getFreeSpace() {
        return freeSpace;
    }

    public long getUsableSpace() {
        return usableSpace;
    }

    public long getUsedSpace() {
        return Math.max(0, totalSpace - freeSpace);
    }

    public double getUsagePercentage() {

        if (totalSpace <= 0) {
            return 0;
        }

        return (getUsedSpace() * 100.0) / totalSpace;
    }

    public long getUnavailableSpace() {
        return Math.max(0, freeSpace - usableSpace);
    }

    public boolean isAlmostFull() {
        return getUsagePercentage() >= 90;
    }

    public boolean isFull() {
        return getUsagePercentage() >= 95;
    }

    public boolean hasAvailableSpace() {
        return usableSpace > 0;
    }

    public String getStorageStatus() {

        double usage = getUsagePercentage();

        if (usage >= 95) {
            return "Critical";
        }

        if (usage >= 85) {
            return "Warning";
        }

        return "Healthy";
    }

    public String getFormattedUsage() {
        return String.format("%.1f%%", getUsagePercentage());
    }

    public String getFormattedTotalSpace() {
        return formatBytes(totalSpace);
    }

    public String getFormattedUsedSpace() {
        return formatBytes(getUsedSpace());
    }

    public String getFormattedFreeSpace() {
        return formatBytes(freeSpace);
    }

    public String getFormattedUsableSpace() {
        return formatBytes(usableSpace);
    }

    private String formatBytes(long bytes) {

        if (bytes <= 0) {
            return "0 B";
        }

        String[] units = {
            "B", "KB", "MB", "GB", "TB", "PB"
        };

        double size = bytes;
        int unit = 0;

        while (size >= 1024 && unit < units.length - 1) {
            size /= 1024;
            unit++;
        }

        return String.format("%.2f %s", size, units[unit]);
    }
}
