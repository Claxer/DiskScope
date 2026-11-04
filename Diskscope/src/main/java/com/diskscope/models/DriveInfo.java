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
        return totalSpace - freeSpace;
    }

    public double getUsagePercentage() {

        if (totalSpace <= 0) {
            return 0;
        }

        return (getUsedSpace() * 100.0) / totalSpace;
    }
}
