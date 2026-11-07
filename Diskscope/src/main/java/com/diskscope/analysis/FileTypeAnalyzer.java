package com.diskscope.analysis;

import java.util.*;
import java.util.stream.Collectors;

public class FileTypeAnalyzer {

    public enum Category {
        IMAGES("Images"),
        VIDEOS("Videos"),
        AUDIO("Audio"),
        DOCUMENTS("Documents"),
        ARCHIVES("Archives"),
        APPLICATIONS("Applications"),
        CODE("Code"),
        OTHERS("Others");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public record CategorySummary(
            String category,
            long fileCount,
            long totalSize,
            double percentage,
            List<String> extensions
    ) {}

    private static final Map<String, Category> EXTENSION_CATEGORIES =
            createExtensionMap();

    private static Map<String, Category> createExtensionMap() {

        Map<String, Category> map = new HashMap<>();

        // Images
        add(map, Category.IMAGES,
                "jpg", "jpeg", "png", "gif", "bmp",
                "webp", "svg", "ico", "tiff", "tif",
                "heic", "raw", "psd");

        // Videos
        add(map, Category.VIDEOS,
                "mp4", "mkv", "avi", "mov", "wmv",
                "flv", "webm", "m4v", "mpeg", "mpg",
                "3gp", "ts");

        // Audio
        add(map, Category.AUDIO,
                "mp3", "wav", "aac", "flac", "ogg",
                "wma", "m4a", "opus", "mid", "midi");

        // Documents
        add(map, Category.DOCUMENTS,
                "pdf", "doc", "docx", "txt", "rtf",
                "odt", "xls", "xlsx", "csv", "ppt",
                "pptx", "odp", "ods", "epub");

        // Archives
        add(map, Category.ARCHIVES,
                "zip", "rar", "7z", "tar", "gz",
                "bz2", "xz", "iso", "jar", "war");

        // Applications and installers
        add(map, Category.APPLICATIONS,
                "exe", "msi", "apk", "app", "deb",
                "rpm", "dmg", "pkg", "bat", "cmd");

        // Programming and development files
        add(map, Category.CODE,
                "java", "class", "py", "pyc", "js",
                "jsx", "ts", "tsx", "html", "css",
                "json", "xml", "yml", "yaml", "sql",
                "c", "cpp", "h", "hpp", "cs",
                "php", "go", "rs", "rb", "swift",
                "kt", "kts", "sh", "ps1", "md");

        return Collections.unmodifiableMap(map);
    }

    private static void add(
            Map<String, Category> map,
            Category category,
            String... extensions
    ) {

        for (String extension : extensions) {
            map.put(extension, category);
        }
    }

    public Category getCategory(String extension) {

        if (extension == null || extension.isBlank()) {
            return Category.OTHERS;
        }

        String cleaned = extension
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceFirst("^\\.", "");

        return EXTENSION_CATEGORIES.getOrDefault(
                cleaned,
                Category.OTHERS
        );
    }

    public List<CategorySummary> analyze(
            List<FileRecord> records
    ) {

        if (records == null || records.isEmpty()) {
            return List.of();
        }

        Map<Category, List<FileRecord>> grouped =
                records.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(
                                record -> getCategory(
                                        record.extension()
                                )
                        ));

        long grandTotal = records.stream()
                .filter(Objects::nonNull)
                .mapToLong(record ->
                        Math.max(0, record.size())
                )
                .sum();

        List<CategorySummary> results =
                new ArrayList<>();

        for (Category category : Category.values()) {

            List<FileRecord> files =
                    grouped.get(category);

            if (files == null || files.isEmpty()) {
                continue;
            }

            long totalSize = files.stream()
                    .mapToLong(record ->
                            Math.max(0, record.size())
                    )
                    .sum();

            long fileCount = files.size();

            double percentage =
                    grandTotal > 0
                            ? (totalSize * 100.0) / grandTotal
                            : 0;

            List<String> extensions = files.stream()
                    .map(FileRecord::extension)
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .toList();

            results.add(
                    new CategorySummary(
                            category.getDisplayName(),
                            fileCount,
                            totalSize,
                            percentage,
                            extensions
                    )
            );
        }

        results.sort(
                Comparator.comparingLong(
                        CategorySummary::totalSize
                ).reversed()
        );

        return List.copyOf(results);
    }

    public long getTotalFiles(
            List<CategorySummary> summaries
    ) {

        return summaries.stream()
                .mapToLong(CategorySummary::fileCount)
                .sum();
    }

    public long getTotalSize(
            List<CategorySummary> summaries
    ) {

        return summaries.stream()
                .mapToLong(CategorySummary::totalSize)
                .sum();
    }

    public CategorySummary getLargestCategory(
            List<CategorySummary> summaries
    ) {

        return summaries.stream()
                .max(
                        Comparator.comparingLong(
                                CategorySummary::totalSize
                        )
                )
                .orElse(null);
    }
}
