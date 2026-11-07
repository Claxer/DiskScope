package com.diskscope.scanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FolderScanner {

    public ScanResult scanFolder(File rootFolder) {

        if (rootFolder == null || !rootFolder.exists()
                || !rootFolder.isDirectory()
                || !rootFolder.canRead()) {
            throw new IllegalArgumentException(
                "Invalid or inaccessible folder."
            );
        }

        Path root = rootFolder.toPath();

        List<FileRecord> files = new ArrayList<>();
        List<FolderRecord> folders = new ArrayList<>();
        Map<String, Integer> fileTypes = new HashMap<>();
        Map<Path, Long> folderSizes = new HashMap<>();

        long[] totalSize = {0};
        long[] totalFiles = {0};
        long[] totalFolders = {0};
        long[] inaccessibleItems = {0};

        try {

            Files.walkFileTree(root, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(
                        Path dir,
                        BasicFileAttributes attrs) {

                    totalFolders[0]++;

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(
                        Path file,
                        BasicFileAttributes attrs) {

                    if (attrs.isRegularFile()) {

                        long size = attrs.size();

                        totalFiles[0]++;
                        totalSize[0] += size;

                        String extension = getExtension(
                            file.getFileName().toString()
                        );

                        fileTypes.merge(
                            extension, 1, Integer::sum
                        );

                        files.add(new FileRecord(
                            file.getFileName().toString(),
                            file.toString(),
                            size,
                            extension
                        ));

                        Path parent = file.getParent();

                        if (parent != null) {
                            folderSizes.merge(
                                parent, size, Long::sum
                            );
                        }
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(
                        Path file,
                        IOException exception) {

                    inaccessibleItems[0]++;

                    System.err.println(
                        "Unable to scan: " + file
                    );

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(
                        Path dir,
                        IOException exception) {

                    if (exception != null) {
                        inaccessibleItems[0]++;
                    }

                    long size = folderSizes.getOrDefault(
                        dir, 0L
                    );

                    folders.add(new FolderRecord(
                        dir.getFileName() == null
                            ? dir.toString()
                            : dir.getFileName().toString(),
                        dir.toString(),
                        size
                    ));

                    Path parent = dir.getParent();

                    if (parent != null) {
                        folderSizes.merge(
                            parent, size, Long::sum
                        );
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {

            throw new RuntimeException(
                "Folder scanning failed: " + e.getMessage(), e
            );
        }

        files.sort(
            Comparator.comparingLong(
                FileRecord::getSize
            ).reversed()
        );

        folders.sort(
            Comparator.comparingLong(
                FolderRecord::getSize
            ).reversed()
        );

        return new ScanResult(
            root.toAbsolutePath().toString(),
            totalSize[0],
            totalFiles[0],
            totalFolders[0],
            inaccessibleItems[0],
            files,
            folders,
            fileTypes
        );
    }

    public List<FileRecord> findLargestFiles(
            File folder, int limit) {

        if (limit <= 0) {
            return new ArrayList<>();
        }

        ScanResult result = scanFolder(folder);

        return new ArrayList<>(
            result.getFiles().subList(
                0,
                Math.min(limit, result.getFiles().size())
            )
        );
    }

    private String getExtension(String fileName) {

        int dot = fileName.lastIndexOf('.');

        if (dot <= 0 || dot == fileName.length() - 1) {
            return "Other";
        }

        return fileName.substring(dot + 1)
            .toLowerCase(Locale.ROOT);
    }

    public static String formatBytes(long bytes) {

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

    // ==============================
    // SCAN RESULT
    // ==============================

    public static class ScanResult {

        private final String folderPath;
        private final long totalSize;
        private final long totalFiles;
        private final long totalFolders;
        private final long inaccessibleItems;

        private final List<FileRecord> files;
        private final List<FolderRecord> folders;
        private final Map<String, Integer> fileTypes;

        public ScanResult(
                String folderPath,
                long totalSize,
                long totalFiles,
                long totalFolders,
                long inaccessibleItems,
                List<FileRecord> files,
                List<FolderRecord> folders,
                Map<String, Integer> fileTypes) {

            this.folderPath = folderPath;
            this.totalSize = totalSize;
            this.totalFiles = totalFiles;
            this.totalFolders = totalFolders;
            this.inaccessibleItems = inaccessibleItems;
            this.files = files;
            this.folders = folders;
            this.fileTypes = fileTypes;
        }

        public String getFolderPath() {
            return folderPath;
        }

        public long getTotalSize() {
            return totalSize;
        }

        public long getTotalFiles() {
            return totalFiles;
        }

        public long getTotalFolders() {
            return totalFolders;
        }

        public long getInaccessibleItems() {
            return inaccessibleItems;
        }

        public List<FileRecord> getFiles() {
            return new ArrayList<>(files);
        }

        public List<FolderRecord> getFolders() {
            return new ArrayList<>(folders);
        }

        public Map<String, Integer> getFileTypes() {
            return new HashMap<>(fileTypes);
        }

        public String getFormattedTotalSize() {
            return formatBytes(totalSize);
        }
    }

    // ==============================
    // FILE RECORD
    // ==============================

    public static class FileRecord {

        private final String name;
        private final String path;
        private final long size;
        private final String extension;

        public FileRecord(
                String name,
                String path,
                long size,
                String extension) {

            this.name = name;
            this.path = path;
            this.size = size;
            this.extension = extension;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public long getSize() {
            return size;
        }

        public String getExtension() {
            return extension;
        }

        public String getFormattedSize() {
            return formatBytes(size);
        }
    }

    // ==============================
    // FOLDER RECORD
    // ==============================

    public static class FolderRecord {

        private final String name;
        private final String path;
        private final long size;

        public FolderRecord(
                String name,
                String path,
                long size) {

            this.name = name;
            this.path = path;
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public long getSize() {
            return size;
        }

        public String getFormattedSize() {
            return formatBytes(size);
        }
    }
}
