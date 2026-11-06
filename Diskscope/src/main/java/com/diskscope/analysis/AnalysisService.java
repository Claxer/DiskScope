package com.diskscope.analysis;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class AnalysisService {

    public List<FileRecord> scan(
            Path root,
            boolean calculateHashes,
            AtomicLong scannedCount
    ) throws Exception {

        if (root == null || !Files.isDirectory(root)) {
            throw new IllegalArgumentException(
                    "The selected folder does not exist."
            );
        }

        List<FileRecord> records = new ArrayList<>();

        Files.walkFileTree(root, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attrs
            ) {

                if (attrs.isRegularFile()) {
                    try {
                        String name = file.getFileName().toString();
                        String extension = extensionOf(name);

                        String hash = calculateHashes
                                ? sha256(file)
                                : "";

                        records.add(new FileRecord(
                                file,
                                attrs.size(),
                                attrs.lastModifiedTime().toInstant(),
                                extension,
                                hash
                        ));

                        scannedCount.incrementAndGet();

                    } catch (Exception ignored) {
                        // Skip files that cannot be accessed.
                    }
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(
                    Path file,
                    IOException exception
            ) {
                return FileVisitResult.CONTINUE;
            }
        });

        records.sort(
                Comparator.comparingLong(FileRecord::size).reversed()
        );

        return records;
    }

    private static String extensionOf(String name) {

        int dot = name.lastIndexOf('.');

        if (dot <= 0 || dot == name.length() - 1) {
            return "[No extension]";
        }

        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static String sha256(Path file) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream input = Files.newInputStream(file)) {

            byte[] buffer = new byte[64 * 1024];

            int read;

            while ((read = input.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }

        return HexFormat.of().formatHex(digest.digest());
    }
}
