package com.diskscope.scanner;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EmptyFolderScanner {

    public record EmptyFolder(
            Path path,
            Instant modified
    ) {
    }

    public List<EmptyFolder> scan(Path root) throws IOException {

        if (root == null || !Files.isDirectory(root)) {
            throw new IllegalArgumentException(
                    "The selected folder does not exist."
            );
        }

        if (!Files.isReadable(root)) {
            throw new IllegalArgumentException(
                    "The selected folder cannot be accessed."
            );
        }

        List<EmptyFolder> emptyFolders =
                new ArrayList<>();

        Files.walkFileTree(root, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult preVisitDirectory(
                    Path directory,
                    BasicFileAttributes attributes
            ) {

                // Do not report the selected root folder itself.
                if (directory.equals(root)) {
                    return FileVisitResult.CONTINUE;
                }

                try (
                        DirectoryStream<Path> contents =
                                Files.newDirectoryStream(directory)
                ) {

                    // A folder is empty only when it has no entries.
                    if (!contents.iterator().hasNext()) {

                        emptyFolders.add(
                                new EmptyFolder(
                                        directory,
                                        attributes.lastModifiedTime()
                                                .toInstant()
                                )
                        );
                    }

                } catch (IOException ignored) {
                    // Skip directories that cannot be accessed.
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

        emptyFolders.sort(
                Comparator.comparing(
                        folder -> folder.path().toString(),
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        return List.copyOf(emptyFolders);
    }
}
