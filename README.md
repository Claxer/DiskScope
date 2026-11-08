# DiskScope — Storage Analyzer

DiskScope is a modern desktop Storage Analyzer built with **Java 25, Java Swing, and JFreeChart**. It provides an overview of computer storage through a clean, dark-themed dashboard, allowing users to monitor disk usage, view storage statistics, visualize storage distribution, and identify connected drives.

The project is designed to make storage monitoring easier through organized information, interactive charts, drive usage indicators, automated updates, and additional file analysis and cleanup tools.

## Features

### Storage Dashboard
- **Storage Dashboard** — View total, used, and available storage through an organized dashboard.
- **Storage Statistics** — Monitor combined disk capacity, used storage, free storage, and overall usage percentages.
- **Drive Detection** — Automatically detect available storage drives.
- **Storage Visualization** — Display used and free storage through a JFreeChart pie chart.
- **Drive Information** — View detected drive names, paths, total capacity, and used storage.
- **Drive Usage Indicators** — Monitor individual drive usage through visual progress bars and percentage labels.
- **System Overview** — Display the number of detected drives, last update time, and current scan status.
- **Automatic Refresh** — Automatically update storage information every 5 seconds.
- **Manual Refresh** — Refresh storage information using the dashboard's Refresh button.
- **Background Scanning** — Use SwingWorker to perform drive scanning in the background without blocking the interface.
- **Scan Protection** — Prevent overlapping scans while another scan is already running.

### Storage Analysis Tools
- **Drive Analyzer** — Scan a selected folder recursively and inspect its files, sizes, extensions, and modification dates.
- **Large Files Finder** — Identify the largest files inside a selected directory, displaying up to the 100 largest results.
- **Large File Search** — Search through scanned results by file name, extension, or location.
- **File Sorting** — Sort table results using column headers, including numeric file-size sorting.
- **Open File Location** — Open the containing folder of a selected file directly from the Large Files Finder.
- **Large Files CSV Export** — Export large-file scan results into a CSV file.
- **Duplicate Finder** — Detect duplicate files by calculating and comparing SHA-256 content hashes.
- **Storage Breakdown** — Analyze storage usage by file extension, including file counts, total sizes, and storage percentages.
- **File Type Analyzer** — Categorize file extensions into Images, Videos, Audio, Documents, Archives, Applications, Code, and Others.
- **Category Statistics** — Calculate file counts, storage totals, and storage percentages for file categories through the analysis engine.
- **File Information** — Display file names, locations, sizes, and other available file details.
- **Folder Scanner** — Recursively analyze folders, collect file and folder information, identify large files, and track inaccessible items.
- **Inaccessible File Handling** — Skip files and directories that cannot be accessed during scanning.
- **Drive Storage Utilities** — Calculate total, used, free, and available storage across detected drives.
- **Drive Status Detection** — Identify drives that are full or approaching capacity.

### Cleanup and Folder Management
- **Cleanup Recommendations** — Identify files and folders that may be suitable for cleanup based on their type, location, size, and last-modified date.
- **Temporary File Detection** — Find temporary files with extensions such as `.tmp`, `.temp`, and `.cache`.
- **Temporary Folder Detection** — Identify files located inside common temporary and cache folders.
- **Old Log File Detection** — Recommend log files that have not been modified for at least 30 days.
- **Large Inactive File Detection** — Identify files at least 100 MB in size that have not been modified for at least 180 days.
- **Potential Storage Savings** — Estimate how much storage could potentially be recovered from the recommended files.
- **Recommendation Prioritization** — Sort cleanup recommendations by file size to help identify larger cleanup opportunities.
- **Cleanup Details** — Display recommendation categories, reasons, priorities, modification dates, and file locations.
- **Cleanup CSV Export** — Export cleanup recommendations into a CSV file for further review.
- **Open Cleanup File Location** — Open the location of a recommended file directly from the application.
- **Empty Folder Finder** — Recursively scan a selected directory and identify empty folders.
- **Empty Folder Information** — Display empty folder names, locations, and last-modified dates.
- **Empty Folder Sorting** — Organize discovered empty folders alphabetically.
- **Empty Folder CSV Export** — Export empty folder scan results into a CSV file.
- **Open Empty Folder Location** — Open the location of a selected empty folder.
- **Safe Cleanup Approach** — Display recommendations without automatically deleting files or folders.

### History and Reporting
- **Scan History** — Automatically save completed folder scans, including scan date, location, file count, and total size.
- **Persistent History** — Store scan records locally in a CSV file so they remain available after restarting the application.
- **History Viewer** — Review previously completed scans through a table.
- **Report Preview** — View saved scan history in a readable report format.
- **CSV Export** — Export scan history into an external CSV file.
- **Large Files Reports** — Export scanned large-file results with file names, extensions, sizes, and locations.
- **Cleanup Reports** — Export cleanup recommendations with their categories, reasons, sizes, and file locations.
- **Empty Folder Reports** — Export discovered empty folders and their corresponding locations.

### Application Settings
- **Application Settings** — Customize basic application preferences.
- **Persistent Preferences** — Save settings using Java Preferences.
- **Refresh Interval Setting** — Configure and save a preferred dashboard refresh interval.
- **File Location Confirmation** — Enable or disable confirmation prompts before opening file locations.

### User Interface
- **Dark User Interface** — Clean, modern interface with a professional charcoal and dark-themed design.
- **Navigation Sidebar and Workspace** — Access the dashboard, analysis tools, cleanup utilities, history, reports, and settings through organized navigation.
- **Interactive Tables** — View, search, and sort file analysis results.
- **Background Processing** — Perform file analysis without freezing the application interface.
- **Organized Workspace** — Access ten application sections through a dedicated navigation sidebar.

## Technologies Used

- **Java 25** — Main programming language.
- **Java Swing** — Desktop graphical user interface.
- **JFreeChart** — Storage charts and data visualization.
- **Java NIO** — File and directory scanning.
- **SHA-256** — File content hashing for duplicate detection.
- **Java Preferences API** — Local application settings storage.
- **CSV File Handling** — Persistent scan history and report exports.
- **Apache Maven** — Project management and dependency handling.
- **Visual Studio Code** — Development environment.

## Project Structure

```text
DiskScope/
│
├── pom.xml
├── data/
│   └── scan-history.csv
│
└── src/
    └── main/
        └── java/
            └── com/
                └── diskscope/
                    │
                    ├── Main.java
                    │
                    ├── analysis/
                    │   ├── AnalysisService.java
                    │   ├── FileRecord.java
                    │   ├── FileTypeAnalyzer.java
                    │   └── CleanupAnalyzer.java
                    │
                    ├── history/
                    │   └── ScanHistoryManager.java
                    │
                    ├── models/
                    │   └── DriveInfo.java
                    │
                    ├── scanner/
                    │   ├── DriveScanner.java
                    │   ├── FolderScanner.java
                    │   └── EmptyFolderScanner.java
                    │
                    ├── ui/
                    │   ├── DashboardPanel.java
                    │   ├── LargestFilesPanel.java
                    │   ├── EmptyFolderPanel.java
                    │   ├── Theme.java
                    │   └── WorkspacePanel.java
                    │
                    └── utils/
                        └── FormatUtils.java
```

## Getting Started

### Requirements

- Java JDK 25 or later
- Apache Maven
- Visual Studio Code or another Java-compatible IDE

### Installation

1. Clone the repository:

```bash
git clone https://github.com/your-username/DiskScope.git
```

2. Open the project folder in Visual Studio Code.

3. Compile the project:

```bash
mvn clean compile
```

4. Run the application:

```bash
mvn exec:java
```

## Project Status

**Under Active Development**

DiskScope currently includes its foundational storage monitoring dashboard, automatic drive detection, storage statistics, interactive storage visualization, individual drive usage indicators, manual refresh functionality, and background scanning.

The application has expanded to include folder analysis, large file detection and search, SHA-256-based duplicate identification, file extension storage breakdowns, persistent scan history, CSV report exporting, and locally saved application preferences.

Additional development has introduced a dedicated folder scanning utility, a separate Largest Files interface, improved drive information and storage calculations, and a File Type Analyzer engine for categorizing files by type.

Recent updates have introduced two additional storage management tools:

- **Cleanup Recommendations** — Analyzes temporary files, old log files, and large inactive files to identify potential cleanup opportunities and estimate possible storage savings.
- **Empty Folder Finder** — Recursively scans selected directories to locate empty folders, display their information, and export the results to CSV.

Both tools include file or folder location access and CSV exporting. They provide information for manual review and do not automatically delete user data.

The advanced File Type Analyzer remains a separate analysis component, with further integration into the Storage Breakdown interface still planned.

Future improvements may include more advanced storage visualization, category-based charts, scan progress indicators, scan cancellation, folder-level storage analytics, and additional system storage utilities.

## Purpose

This project was created as a practical way to develop programming skills, explore Java desktop application development, and gain experience working with system information, storage management, background processing, file analysis, data visualization, cleanup recommendations, and local data persistence.

## License

This project is open-source and available under the MIT License.
