package com.diskscope.analysis;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CleanupAnalyzer {

    private static final Set<String> TEMP_EXTENSIONS = Set.of(
            "tmp",
            "temp",
            "cache"
    );

    private static final Set<String> TEMP_DIRECTORIES = Set.of(
            "temp",
            "tmp",
            "cache",
            "caches"
    );

    private static final long LARGE_FILE_SIZE =
            100L * 1024 * 1024;

    private static final Duration OLD_LOG_AGE =
            Duration.ofDays(30);

    private static final Duration OLD_FILE_AGE =
            Duration.ofDays(180);

    public record Recommendation(
            FileRecord file,
            String category,
            String reason,
            String priority
    ) {

        public long reclaimableBytes() {
            return file.size();
        }
    }

    public List<Recommendation> analyze(
            List<FileRecord> records
    ) {
        return analyze(records, Instant.now());
    }

    public List<Recommendation> analyze(
            List<FileRecord> records,
            Instant now
    ) {

        List<Recommendation> recommendations =
                new ArrayList<>();

        if (records == null || now == null) {
            return recommendations;
        }

        for (FileRecord record : records) {

            if (record == null || record.size() <= 0) {
                continue;
            }

            String extension = record.extension() == null
                    ? ""
                    : record.extension().toLowerCase(Locale.ROOT);

            boolean insideTempDirectory =
                    isInsideTempDirectory(record);

            long ageDays = record.modified() == null
                    ? 0
                    : Math.max(
                            0,
                            Duration.between(
                                    record.modified(),
                                    now
                            ).toDays()
                    );

            // Temporary and cache directories.
            if (insideTempDirectory) {

                recommendations.add(
                        new Recommendation(
                                record,
                                "Temporary / Cache Folder",
                                "This file is stored inside a temporary or cache directory.",
                                "Review"
                        )
                );

            }

            // Temporary file extensions.
            else if (TEMP_EXTENSIONS.contains(extension)) {

                recommendations.add(
                        new Recommendation(
                                record,
                                "Temporary File",
                                "This file uses a temporary or cache-related extension.",
                                "Review"
                        )
                );

            }

            // Old log files.
            else if (
                    extension.equals("log")
                    && ageDays >= OLD_LOG_AGE.toDays()
            ) {

                recommendations.add(
                        new Recommendation(
                                record,
                                "Old Log File",
                                "This log file has not been modified for "
                                        + ageDays + " days.",
                                "Medium"
                        )
                );

            }

            // Large files that have not been modified recently.
            else if (
                    record.size() >= LARGE_FILE_SIZE
                    && ageDays >= OLD_FILE_AGE.toDays()
            ) {

                recommendations.add(
                        new Recommendation(
                                record,
                                "Large Inactive File",
                                "This file is at least 100 MB and has been inactive for "
                                        + ageDays + " days.",
                                "Review"
                        )
                );
            }
        }

        recommendations.sort(
                Comparator.comparingLong(
                        Recommendation::reclaimableBytes
                ).reversed()
        );

        return List.copyOf(recommendations);
    }

    private boolean isInsideTempDirectory(
            FileRecord record
    ) {

        Path path = record.path();

        if (path == null) {
            return false;
        }

        for (Path component : path) {

            String name = component.toString()
                    .toLowerCase(Locale.ROOT);

            if (TEMP_DIRECTORIES.contains(name)) {
                return true;
            }
        }

        return false;
    }
}
