package com.diskscope.scanner;

import com.diskscope.models.DriveInfo;

import java.io.File;
import java.util.ArrayList;
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

                if (!root.exists()) {
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

        return drives;
    }

}
