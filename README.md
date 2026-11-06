# DiskScope — Storage Analyzer

DiskScope is a modern desktop Storage Analyzer built with **Java 25, Java Swing, and JFreeChart**. It provides an overview of computer storage through a clean, dark-themed dashboard, allowing users to monitor disk usage, view storage statistics, visualize storage distribution, and identify connected drives.

The project is designed to make storage monitoring easier through organized information, interactive charts, drive usage indicators, automated updates, and additional file analysis tools.

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
- **Duplicate Finder** — Detect duplicate files by calculating and comparing SHA-256 content hashes.
- **Storage Breakdown** — Analyze storage usage by file extension, including file counts, total sizes, and storage percentages.
- **File Information** — Display file names, locations, sizes, and other available file details.
- **Inaccessible File Handling** — Skip files and directories that cannot be accessed during scanning.

### History and Reporting
- **Scan History** — Automatically save completed folder scans, including scan date, location, file count, and total size.
- **Persistent History** — Store scan records locally in a CSV file so they remain available after restarting the application.
- **History Viewer** — Review previously completed scans through a table.
- **Report Preview** — View saved scan history in a readable report format.
- **CSV Export** — Export scan history into an external CSV file.

### Application Settings
- **Application Settings** — Customize basic application preferences.
- **Persistent Preferences** — Save settings using Java Preferences.
- **Refresh Interval Setting** — Configure and save a preferred dashboard refresh interval.

### User Interface
- **Dark User Interface** — Clean, modern interface with a professional charcoal and dark-themed design.
- **Navigation Sidebar and Workspace** — Access the dashboard, analysis tools, history, reports, and settings through organized navigation.
- **Background Processing** — Perform file analysis without freezing the application interface.

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
                    │   └── FileRecord.java
                    │
                    ├── history/
                    │   └── ScanHistoryManager.java
                    │
                    ├── models/
                    │   └── DriveInfo.java
                    │
                    ├── scanner/
                    │   └── DriveScanner.java
                    │
                    ├── ui/
                    │   ├── DashboardPanel.java
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

The application has also expanded to include folder analysis, large file detection, SHA-256-based duplicate identification, file type storage breakdowns, persistent scan history, CSV report exporting, and locally saved application preferences.

Future improvements may include more advanced storage visualization, scan progress indicators, scan cancellation, folder-level storage analytics, and additional system storage utilities.

## Purpose

This project was created as a practical way to develop programming skills, explore Java desktop application development, and gain experience working with system information, storage management, background processing, file analysis, data visualization, and local data persistence.

## License

This project is open-source and available under the MIT License.
