package com.diskscope.ui;

import com.diskscope.models.DriveInfo;
import com.diskscope.scanner.DriveScanner;
import com.diskscope.models.DriveInfo;
import com.diskscope.utils.FormatUtils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final Color BACKGROUND = new Color(17, 17, 19);
    private final Color PANEL = new Color(30, 30, 34);
    private final Color TEXT = new Color(240, 240, 242);
    private final Color MUTED = new Color(150, 150, 158);
    private final Color BLUE = new Color(100, 149, 237);
    private final Color GREEN = new Color(90, 190, 140);
    private final Color ORANGE = new Color(235, 165, 85);

    private final DriveScanner driveScanner = new DriveScanner();

    private JLabel totalStorageValue;
    private JLabel usedStorageValue;
    private JLabel freeStorageValue;
    private JLabel usageValue;

    private JLabel driveCountValue;
    private JLabel lastUpdatedValue;

    private JPanel driveListPanel;
    private ChartPanel chartPanel;

    private DefaultPieDataset<String> storageDataset;

    private Timer refreshTimer;

    public DashboardPanel() {

        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(10, 0, 10, 0));

        createDashboard();

        refreshDashboard();

        refreshTimer = new Timer(5000, e -> refreshDashboard());
        refreshTimer.start();

    }

    private void createDashboard() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND);

        mainPanel.add(createStatisticsPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createChartsSection());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createDriveSection());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND);
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

    }

    private JPanel createStatisticsPanel() {

        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(BACKGROUND);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalStorageValue = new JLabel("Loading...");
        usedStorageValue = new JLabel("Loading...");
        freeStorageValue = new JLabel("Loading...");
        usageValue = new JLabel("Loading...");

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

    private JPanel createStatCard(
            String title,
            JLabel value,
            String description,
            Color accent) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL);
        card.setBorder(new EmptyBorder(17, 17, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 10));

        value.setForeground(TEXT);
        value.setFont(new Font("SansSerif", Font.BOLD, 23));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setForeground(MUTED);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(value);
        card.add(Box.createVerticalStrut(6));
        card.add(descriptionLabel);

        return card;

    }

    private JPanel createChartsSection() {

        JPanel section = new JPanel(new BorderLayout(15, 0));
        section.setBackground(BACKGROUND);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(PANEL);
        chartContainer.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel chartTitle = new JLabel("Storage Distribution");
        chartTitle.setForeground(TEXT);
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

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

        PiePlot plot = (PiePlot) chart.getPlot();

        plot.setBackgroundPaint(PANEL);
        plot.setOutlineVisible(false);
        plot.setSectionPaint("Used Storage", ORANGE);
        plot.setSectionPaint("Free Storage", GREEN);
        plot.setLabelBackgroundPaint(PANEL);
        plot.setLabelPaint(TEXT);
        plot.setNoDataMessage("No storage information available");
        plot.setNoDataMessagePaint(MUTED);

        chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(PANEL);
        chartPanel.setBorder(null);

        chartContainer.add(chartTitle, BorderLayout.NORTH);
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        JPanel overview = new JPanel();
        overview.setLayout(new BoxLayout(overview, BoxLayout.Y_AXIS));
        overview.setBackground(PANEL);
        overview.setBorder(new EmptyBorder(20, 20, 20, 20));
        overview.setPreferredSize(new Dimension(240, 0));

        JLabel overviewTitle = new JLabel("System Overview");
        overviewTitle.setForeground(TEXT);
        overviewTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

        driveCountValue = createOverviewValue();
        lastUpdatedValue = createOverviewValue();

        overview.add(overviewTitle);
        overview.add(Box.createVerticalStrut(25));

        overview.add(createOverviewItem("Detected Drives", driveCountValue));
        overview.add(Box.createVerticalStrut(20));

        overview.add(createOverviewItem("Last Updated", lastUpdatedValue));
        overview.add(Box.createVerticalGlue());

        section.add(chartContainer, BorderLayout.CENTER);
        section.add(overview, BorderLayout.EAST);

        return section;

    }

    private JLabel createOverviewValue() {

        JLabel label = new JLabel("--");
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));

        return label;

    }

    private JPanel createOverviewItem(String title, JLabel value) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(7));
        panel.add(value);

        return panel;

    }

    private JPanel createDriveSection() {

        JPanel section = new JPanel(new BorderLayout(0, 15));
        section.setBackground(BACKGROUND);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setBackground(BACKGROUND);

        JLabel title = new JLabel("Connected Drives");
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel subtitle = new JLabel("Storage information for detected drives");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JPanel labels = new JPanel();
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labels.setBackground(BACKGROUND);

        labels.add(title);
        labels.add(Box.createVerticalStrut(5));
        labels.add(subtitle);

        heading.add(labels, BorderLayout.WEST);

        driveListPanel = new JPanel();
        driveListPanel.setLayout(new BoxLayout(driveListPanel, BoxLayout.Y_AXIS));
        driveListPanel.setBackground(BACKGROUND);

        section.add(heading, BorderLayout.NORTH);
        section.add(driveListPanel, BorderLayout.CENTER);

        return section;

    }

    private void refreshDashboard() {

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
                            java.time.LocalTime.now()
                                .withNano(0).toString()
                        );

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                }

            };

        worker.execute();

    }

    private void updateStatistics(List<DriveInfo> drives) {

        long total = 0;
        long used = 0;
        long free = 0;

        for (DriveInfo drive : drives) {

            total += drive.getTotalSpace();
            used += drive.getUsedSpace();
            free += drive.getFreeSpace();

        }

        totalStorageValue.setText(FormatUtils.formatBytes(total));
        usedStorageValue.setText(FormatUtils.formatBytes(used));
        freeStorageValue.setText(FormatUtils.formatBytes(free));

        usageValue.setText(
            String.format("%.1f%%", FormatUtils.getPercentage(used, total))
        );

    }

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

    private void updateDriveList(List<DriveInfo> drives) {

        driveListPanel.removeAll();

        if (drives.isEmpty()) {

            JLabel empty = new JLabel("No available drives detected.");
            empty.setForeground(MUTED);

            driveListPanel.add(empty);

        } else {

            for (DriveInfo drive : drives) {

                driveListPanel.add(createDriveCard(drive));
                driveListPanel.add(Box.createVerticalStrut(10));

            }

        }

        driveListPanel.revalidate();
        driveListPanel.repaint();

    }

    private JPanel createDriveCard(DriveInfo drive) {

        JPanel card = new JPanel(new BorderLayout(15, 8));
        card.setBackground(PANEL);
        card.setBorder(new EmptyBorder(17, 20, 17, 20));

        JLabel name = new JLabel(drive.getName());
        name.setForeground(TEXT);
        name.setFont(new Font("SansSerif", Font.BOLD, 13));

        JLabel path = new JLabel(drive.getPath());
        path.setForeground(MUTED);
        path.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBackground(PANEL);

        details.add(name);
        details.add(Box.createVerticalStrut(5));
        details.add(path);

        JProgressBar progress = new JProgressBar(
            0, 100
        );

        progress.setValue(
            (int) Math.round(drive.getUsagePercentage())
        );

        progress.setStringPainted(false);
        progress.setForeground(BLUE);
        progress.setBackground(new Color(48, 48, 53));
        progress.setBorderPainted(false);
        progress.setPreferredSize(new Dimension(150, 8));
        progress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));

        JLabel usage = new JLabel(
            FormatUtils.formatBytes(drive.getUsedSpace())
                + " used / "
                + FormatUtils.formatBytes(drive.getTotalSpace())
        );

        usage.setForeground(MUTED);
        usage.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(PANEL);

        right.add(usage);
        right.add(Box.createVerticalStrut(8));
        right.add(progress);

        card.add(details, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        return card;

    }

    public void stopRefreshTimer() {

        if (refreshTimer != null) {
            refreshTimer.stop();
        }

    }

}
