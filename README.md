# DiskScope — Storage Analyzer

DiskScope is a modern desktop Storage Analyzer built with **Java 25, Java Swing, and JFreeChart**. It provides an overview of computer storage through a clean, dark-themed dashboard, allowing users to monitor disk usage, view storage statistics, visualize storage distribution, and identify connected drives.

The project is designed to make storage monitoring easier through organized information, interactive charts, drive usage indicators, and automated updates.

## Features

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
- **Dark User Interface** — Clean, modern interface with a professional charcoal and dark-themed design.
- **Navigation Sidebar** — Access different application sections through organized navigation.

## Planned Features

- **Drive Analyzer** — Browse drives, scan directories, and calculate folder sizes.
- **Large Files Finder** — Identify and organize the largest files on a computer.
- **Duplicate Finder** — Detect duplicate files using file sizes and SHA-256 hashing.
- **Storage Breakdown** — Analyze storage usage by folder and file type.
- **Scan History** — Save previous scan information using SQLite.
- **Report Export** — Export storage analysis results into CSV reports.
- **Application Settings** — Customize scanning preferences and application behavior.
- **Scan Controls** — Add scanning progress indicators and cancellation options.

## Technologies Used

- **Java 25** — Main programming language.
- **Java Swing** — Desktop graphical user interface.
- **JFreeChart** — Storage charts and data visualization.
- **SQLite JDBC** — Local database integration for planned storage history and data management.
- **Apache Maven** — Project management and dependency handling.
- **Visual Studio Code** — Development environment.

## Project Structure

```text
DiskScope/
│
├── pom.xml
│
└── src/
    └── main/
        └── java/
            └── com/
                └── diskscope/
                    │
                    ├── Main.java
                    │
                    ├── models/
                    │   └── DriveInfo.java
                    │
                    ├── scanner/
                    │   └── DriveScanner.java
                    │
                    ├── ui/
                    │   └── DashboardPanel.java
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

DiskScope currently includes its foundational storage monitoring dashboard, automatic drive detection, storage statistics, interactive storage visualization, individual drive usage indicators, manual refresh functionality, background scanning, and an improved dark-themed interface.

Additional storage analysis tools, file management utilities, scan history, database integration, and reporting features are planned for future updates.

## Purpose

This project was created as a practical way to develop programming skills, explore Java desktop application development, and gain experience working with system information, storage management, background processing, data visualization, and database integration.

## License

This project is open-source and available under the MIT License.
