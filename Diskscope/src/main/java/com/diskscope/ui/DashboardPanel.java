package com.diskscope.ui;

import com.diskscope.models.DriveInfo;
import com.diskscope.scanner.DriveScanner;
import com.diskscope.utils.FormatUtils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardPanel extends JPanel {

    // Theme colors
    private final Color BACKGROUND = new Color(15, 17, 21);
    private final Color PANEL = new Color(26, 29, 35);
    private final Color PANEL_LIGHT = new Color(35, 39, 47);
    private final Color TEXT = new Color(235, 238, 243);
    private final Color MUTED = new Color(145, 152, 163);

    private final Color BLUE = new Color(80, 145, 235);
    private final Color GREEN = new Color(80, 190, 135);
    private final Color ORANGE = new Color(235, 165, 85);

    private final DriveScanner driveScanner = new DriveScanner();

    // Statistics
    private JLabel totalStorageValue;
    private JLabel usedStorageValue;
    private JLabel freeStorageValue;
    private JLabel usageValue;

    // System overview
    private JLabel driveCountValue;
    private JLabel lastUpdatedValue;
    private JLabel scanStatusValue;

    // Dashboard components
    private JPanel driveListPanel;
    private ChartPanel chartPanel;
    private DefaultPieDataset<String> storageDataset;
    private JButton refreshButton;

    private Timer refreshTimer;
    private boolean scanning = false;

    public DashboardPanel() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(10, 20, 20, 20));

        createDashboard();

        refreshDashboard();

        // Automatically refresh every 5 seconds
        refreshTimer = new Timer(5000, e -> refreshDashboard());
        refreshTimer.start();
    }

    // ==========================================
    // CREATE DASHBOARD
    // ==========================================

    private void createDashboard() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND);

        mainPanel.add(createStatisticsPanel());
        mainPanel.add(Box.createVerticalStrut(22));

        mainPanel.add(createChartsSection());
        mainPanel.add(Box.createVerticalStrut(25));

        mainPanel.add(createDriveSection());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND);
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    // ==========================================
    // STATISTICS CARDS
    // ==========================================

    private JPanel createStatisticsPanel() {

        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(BACKGROUND);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalStorageValue = createStatValue();
        usedStorageValue = createStatValue();
        freeStorageValue = createStatValue();
        usageValue = createStatValue();

        panel.add(createStatCard(
                "TOTAL STORAGE",
                totalStorageValue,
                "Combined drive capacity",
                BLUE
        ));

        panel.add(createStatCard(
                "USED STORAGE",
                usedStorageValue,
                "Space currently occupied",
                ORANGE
        ));

        panel.add(createStatCard(
                "FREE STORAGE",
                freeStorageValue,
                "Available drive space",
                GREEN
        ));

        panel.add(createStatCard(
                "DRIVE USAGE",
                usageValue,
                "Overall storage usage",
                BLUE
        ));

        return panel;
    }

    private JLabel createStatValue() {

        JLabel label = new JLabel("Loading...");
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 23));

        return label;
    }

    private JPanel createStatCard(
            String title,
            JLabel value,
            String description,
            Color accent) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL);
        card.setBorder(new EmptyBorder(17, 18, 16, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));

        JPanel accentLine = new JPanel();
        accentLine.setBackground(accent);
        accentLine.setMaximumSize(new Dimension(35, 3));
        accentLine.setPreferredSize(new Dimension(35, 3));
        accentLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        value.setForeground(TEXT);
        value.setFont(new Font("Segoe UI", Font.BOLD, 23));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setForeground(MUTED);
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(accentLine);
        card.add(Box.createVerticalStrut(10));
        card.add(value);
        card.add(Box.createVerticalStrut(5));
        card.add(descriptionLabel);

        return card;
    }

    // ==========================================
    // STORAGE CHART AND SYSTEM OVERVIEW
    // ==========================================

    private JPanel createChartsSection() {

        JPanel section = new JPanel(new BorderLayout(15, 0));
        section.setBackground(BACKGROUND);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 290));
        section.setPreferredSize(new Dimension(800, 290));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Chart container
        JPanel chartContainer = new JPanel(new BorderLayout(0, 12));
        chartContainer.setBackground(PANEL);
        chartContainer.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel chartTitle = new JLabel("Storage Distribution");
        chartTitle.setForeground(TEXT);
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));

        storageDataset = new DefaultPieDataset<>();
        storageDataset.setValue("Used Storage", 1);
        storageDataset.setValue("Free Storage", 1);

        JFreeChart chart = ChartFactory.createPieChart(
                null,
                storageDataset,
                true,
                false,
                false
        );

        chart.setBackgroundPaint(PANEL);
        chart.setBorderVisible(false);

        PiePlot plot = (PiePlot) chart.getPlot();

        plot.setBackgroundPaint(PANEL);
        plot.setOutlineVisible(false);
        plot.setSectionPaint("Used Storage", ORANGE);
        plot.setSectionPaint("Free Storage", GREEN);
        plot.setLabelBackgroundPaint(PANEL);
        plot.setLabelPaint(TEXT);
        plot.setNoDataMessage("No storage information available");
        plot.setNoDataMessagePaint(MUTED);

        // Create chart panel
        chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(PANEL);
        chartPanel.setBorder(null);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(400, 210));

        chartContainer.add(chartTitle, BorderLayout.NORTH);
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        // System overview panel
        JPanel overview = new JPanel();
        overview.setLayout(new BoxLayout(overview, BoxLayout.Y_AXIS));
        overview.setBackground(PANEL);
        overview.setBorder(new EmptyBorder(20, 20, 20, 20));
        overview.setPreferredSize(new Dimension(245, 0));

        JLabel overviewTitle = new JLabel("System Overview");
        overviewTitle.setForeground(TEXT);
        overviewTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));

        driveCountValue = createOverviewValue();
        lastUpdatedValue = createOverviewValue();

        scanStatusValue = createOverviewValue();
        scanStatusValue.setForeground(GREEN);

        overview.add(overviewTitle);
        overview.add(Box.createVerticalStrut(25));

        overview.add(createOverviewItem(
                "Detected Drives",
                driveCountValue
        ));

        overview.add(Box.createVerticalStrut(20));

        overview.add(createOverviewItem(
                "Last Updated",
                lastUpdatedValue
        ));

        overview.add(Box.createVerticalStrut(20));

        overview.add(createOverviewItem(
                "Scan Status",
                scanStatusValue
        ));

        overview.add(Box.createVerticalGlue());

        section.add(chartContainer, BorderLayout.CENTER);
        section.add(overview, BorderLayout.EAST);

        return section;
    }

    private JLabel createOverviewValue() {

        JLabel label = new JLabel("--");
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        return label;
    }

    private JPanel createOverviewItem(String title, JLabel value) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(7));
        panel.add(value);

        return panel;
    }

    // ==========================================
    // CONNECTED DRIVES
    // ==========================================

    private JPanel createDriveSection() {

        JPanel section = new JPanel(new BorderLayout(0, 15));
        section.setBackground(BACKGROUND);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setBackground(BACKGROUND);

        JPanel labels = new JPanel();
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labels.setBackground(BACKGROUND);

        JLabel title = new JLabel("Connected Drives");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));

        JLabel subtitle = new JLabel(
                "Storage information for detected drives"
        );
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        labels.add(title);
        labels.add(Box.createVerticalStrut(5));
        labels.add(subtitle);

        refreshButton = new JButton("Refresh");
        styleRefreshButton();

        refreshButton.addActionListener(e -> refreshDashboard());

        heading.add(labels, BorderLayout.WEST);
        heading.add(refreshButton, BorderLayout.EAST);

        driveListPanel = new JPanel();
        driveListPanel.setLayout(new BoxLayout(
                driveListPanel,
                BoxLayout.Y_AXIS
        ));
        driveListPanel.setBackground(BACKGROUND);

        section.add(heading, BorderLayout.NORTH);
        section.add(driveListPanel, BorderLayout.CENTER);

        return section;
    }

    private void styleRefreshButton() {

        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        refreshButton.setForeground(TEXT);
        refreshButton.setBackground(PANEL_LIGHT);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(new EmptyBorder(9, 18, 9, 18));
        refreshButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    // ==========================================
    // REFRESH DASHBOARD
    // ==========================================

    private void refreshDashboard() {

        // Prevent multiple scans from running simultaneously
        if (scanning) {
            return;
        }

        scanning = true;

        if (refreshButton != null) {
            refreshButton.setEnabled(false);
            refreshButton.setText("Scanning...");
        }

        if (scanStatusValue != null) {
            scanStatusValue.setText("Scanning...");
            scanStatusValue.setForeground(ORANGE);
        }

        SwingWorker<List<DriveInfo>, Void> worker =
                new SwingWorker<>() {

            @Override
            protected List<DriveInfo> doInBackground() {
                return driveScanner.getAvailableDrives();
            }

            @Override
            protected void done() {

                try {

                    List<DriveInfo> drives = get();

                    updateStatistics(drives);
                    updateChart(drives);
                    updateDriveList(drives);

                    driveCountValue.setText(
                            String.valueOf(drives.size())
                    );

                    lastUpdatedValue.setText(
                            LocalTime.now().format(
                                    DateTimeFormatter.ofPattern("HH:mm:ss")
                            )
                    );

                    scanStatusValue.setText("Online");
                    scanStatusValue.setForeground(GREEN);

                } catch (Exception e) {

                    scanStatusValue.setText("Scan failed");
                    scanStatusValue.setForeground(ORANGE);

                    JOptionPane.showMessageDialog(
                            DashboardPanel.this,
                            "Unable to scan available drives:\n"
                                    + e.getMessage(),
                            "Storage Scan Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                } finally {

                    scanning = false;

                    if (refreshButton != null) {
                        refreshButton.setEnabled(true);
                        refreshButton.setText("Refresh");
                    }
                }
            }
        };

        worker.execute();
    }

    // ==========================================
    // UPDATE STATISTICS
    // ==========================================

    private void updateStatistics(List<DriveInfo> drives) {

        long total = 0;
        long used = 0;
        long free = 0;

        for (DriveInfo drive : drives) {

            total += drive.getTotalSpace();
            used += drive.getUsedSpace();
            free += drive.getFreeSpace();
        }

        totalStorageValue.setText(
                FormatUtils.formatBytes(total)
        );

        usedStorageValue.setText(
                FormatUtils.formatBytes(used)
        );

        freeStorageValue.setText(
                FormatUtils.formatBytes(free)
        );

        usageValue.setText(
                String.format(
                        "%.1f%%",
                        FormatUtils.getPercentage(used, total)
                )
        );
    }

    // ==========================================
    // UPDATE STORAGE CHART
    // ==========================================

    private void updateChart(List<DriveInfo> drives) {

        long used = 0;
        long free = 0;

        for (DriveInfo drive : drives) {

            used += drive.getUsedSpace();
            free += drive.getFreeSpace();
        }

        storageDataset.setValue("Used Storage", used);
        storageDataset.setValue("Free Storage", free);
    }

    // ==========================================
    // UPDATE DRIVE LIST
    // ==========================================

    private void updateDriveList(List<DriveInfo> drives) {

        driveListPanel.removeAll();

        if (drives.isEmpty()) {

            JLabel empty = new JLabel(
                    "No available drives detected."
            );

            empty.setForeground(MUTED);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            driveListPanel.add(empty);

        } else {

            for (DriveInfo drive : drives) {

                driveListPanel.add(createDriveCard(drive));
                driveListPanel.add(Box.createVerticalStrut(12));
            }
        }

        driveListPanel.revalidate();
        driveListPanel.repaint();
    }

    // ==========================================
    // DRIVE CARD
    // ==========================================

    private JPanel createDriveCard(DriveInfo drive) {

        JPanel card = new JPanel(new BorderLayout(20, 8));
        card.setBackground(PANEL);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Drive information
        JLabel name = new JLabel(drive.getName());
        name.setForeground(TEXT);
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel path = new JLabel(drive.getPath());
        path.setForeground(MUTED);
        path.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBackground(PANEL);

        details.add(name);
        details.add(Box.createVerticalStrut(6));
        details.add(path);

        // Storage progress bar
        int percentage = (int) Math.round(
                drive.getUsagePercentage()
        );

        percentage = Math.max(0, Math.min(100, percentage));

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue(percentage);
        progress.setStringPainted(false);
        progress.setForeground(BLUE);
        progress.setBackground(PANEL_LIGHT);
        progress.setBorderPainted(false);
        progress.setPreferredSize(new Dimension(180, 9));
        progress.setMaximumSize(new Dimension(220, 9));

        JLabel usage = new JLabel(
                FormatUtils.formatBytes(drive.getUsedSpace())
                        + " used / "
                        + FormatUtils.formatBytes(drive.getTotalSpace())
                        + " total"
        );

        usage.setForeground(MUTED);
        usage.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel percentageLabel = new JLabel(
                percentage + "% used"
        );

        percentageLabel.setForeground(TEXT);
        percentageLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );

        JPanel usageDetails = new JPanel(new BorderLayout());
        usageDetails.setBackground(PANEL);

        usageDetails.add(usage, BorderLayout.WEST);
        usageDetails.add(percentageLabel, BorderLayout.EAST);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(PANEL);
        right.setPreferredSize(new Dimension(270, 55));

        right.add(usageDetails);
        right.add(Box.createVerticalStrut(9));
        right.add(progress);

        card.add(details, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    // ==========================================
    // TIMER CONTROL
    // ==========================================

    public void stopRefreshTimer() {

        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
}
