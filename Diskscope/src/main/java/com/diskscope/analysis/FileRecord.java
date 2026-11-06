package com.diskscope.analysis;

import java.nio.file.Path;
import java.time.Instant;

public record FileRecord(
        Path path,
        long size,
        Instant modified,
        String extension,
        String sha256
) {
    public String fileName() {
        return path.getFileName() == null
                ? path.toString()
                : path.getFileName().toString();
    }
}
