package com.diskscope.scanner;

import com.diskscope.models.DriveInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DriveScanner {

    public List<DriveInfo> getAvailableDrives() {

        List<DriveInfo> drives = new ArrayList<>();

        File[] roots = File.listRoots();

        if (roots == null) {
            return drives;
        }

        for (File root : roots) {

            try {

                if (!root.exists() || !root.canRead()) {
                    continue;
                }

                long total = root.getTotalSpace();
                long free = root.getFreeSpace();
                long usable = root.getUsableSpace();

                if (total <= 0) {
                    continue;
                }

                String path = root.getAbsolutePath();

                DriveInfo drive = new DriveInfo(
                    "Local Drive " + path,
                    path,
                    total,
                    free,
                    usable
                );

                drives.add(drive);

            } catch (SecurityException e) {

                System.err.println(
                    "Unable to access drive: " + root
                );
            }
        }

        drives.sort(
            Comparator.comparing(DriveInfo::getPath)
        );

        return drives;
    }

    public DriveInfo getDriveByPath(String path) {

        if (path == null || path.isBlank()) {
            return null;
        }

        for (DriveInfo drive : getAvailableDrives()) {

            if (drive.getPath().equalsIgnoreCase(path)) {
                return drive;
            }
        }

        return null;
    }

    public long getTotalStorage() {

        long total = 0;

        for (DriveInfo drive : getAvailableDrives()) {
            total += drive.getTotalSpace();
        }

        return total;
    }

    public long getTotalUsedStorage() {

        long total = 0;

        for (DriveInfo drive : getAvailableDrives()) {
            total += drive.getUsedSpace();
        }

        return total;
    }

    public long getTotalFreeStorage() {

        long total = 0;

        for (DriveInfo drive : getAvailableDrives()) {
            total += drive.getFreeSpace();
        }

        return total;
    }

    public int getDriveCount() {
        return getAvailableDrives().size();
    }

    public List<DriveInfo> getDrivesSortedByUsage() {

        List<DriveInfo> drives = getAvailableDrives();

        drives.sort(
            Comparator.comparingDouble(
                DriveInfo::getUsagePercentage
            ).reversed()
        );

        return drives;
    }

    public List<DriveInfo> getDrivesSortedByCapacity() {

        List<DriveInfo> drives = getAvailableDrives();

        drives.sort(
            Comparator.comparingLong(
                DriveInfo::getTotalSpace
            ).reversed()
        );

        return drives;
    }
}
