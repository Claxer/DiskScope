# DiskScope — Storage Analyzer

DiskScope is a modern desktop Storage Analyzer built with **Java 25, Java Swing, and JFreeChart**. It provides an overview of computer storage through a clean, dark-themed dashboard, allowing users to monitor disk usage, view storage statistics, and identify connected drives.

The project is designed to make storage monitoring easier through organized information, visual charts, and automated updates.

## Features

- **Storage Dashboard** — View total, used, and available storage through an organized dashboard.
- **Storage Statistics** — Monitor disk capacity and usage percentages.
- **Drive Detection** — Automatically detect connected storage drives.
- **Storage Visualization** — Display used and free storage through interactive charts.
- **Drive Information** — View storage details for detected drives.
- **Automatic Refresh** — Update storage information periodically.
- **Dark User Interface** — Clean, modern interface with a professional dark theme.
- **Navigation Sidebar** — Access different sections of the application through organized navigation.

## Planned Features

- **Drive Analyzer** — Browse drives, scan directories, and calculate folder sizes.
- **Large Files Finder** — Identify and organize the largest files on a computer.
- **Duplicate Finder** — Detect duplicate files using file sizes and SHA-256 hashing.
- **Storage Breakdown** — Analyze storage usage by folder and file type.
- **Scan History** — Save previous scan information using SQLite.
- **Report Export** — Export storage analysis results into CSV reports.

## Technologies Used

- **Java 25** — Main programming language.
- **Java Swing** — Desktop graphical user interface.
- **JFreeChart** — Storage charts and data visualization.
- **SQLite JDBC** — Local database integration.
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

DiskScope currently includes its foundational storage monitoring dashboard, drive detection, storage statistics, visual charts, and automatic refresh functionality. Additional storage analysis tools and reporting features are planned for future updates.

## Purpose

This project was created as a practical way to develop programming skills, explore Java desktop application development, and gain experience working with system information, storage management, data visualization, and database integration.

## License

This project is open-source and available under the MIT License.
