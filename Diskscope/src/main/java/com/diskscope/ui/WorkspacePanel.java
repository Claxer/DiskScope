package com.diskscope.ui;

import com.diskscope.analysis.*;
import com.diskscope.history.ScanHistoryManager;
import com.diskscope.utils.FormatUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class WorkspacePanel extends JPanel {

    private final AnalysisService service =
            new AnalysisService();

    private final ScanHistoryManager history =
            new ScanHistoryManager();

    private final JTabbedPane tabs =
            new JTabbedPane();

    private final JTextArea reportArea =
            new JTextArea();

    private final JLabel settingsStatus =
            new JLabel("Settings are saved locally.");

    private final java.util.prefs.Preferences prefs =
            java.util.prefs.Preferences.userNodeForPackage(
                    WorkspacePanel.class
            );

    public WorkspacePanel() {

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        tabs.setBackground(Theme.BACKGROUND);
        tabs.setForeground(Theme.TEXT);

        tabs.addTab("Dashboard", new DashboardPanel());

        tabs.addTab(
                "Drive Analyzer",
                createScanPage(
                        "Drive Analyzer",
                        "Scan a folder to inspect its files and storage usage.",
                        Mode.ALL
                )
        );

        tabs.addTab(
                "Large Files",
                createScanPage(
                        "Largest Files Finder",
                        "Find the largest files inside a selected folder.",
                        Mode.LARGE
                )
        );

        tabs.addTab(
                "Duplicate Finder",
                createScanPage(
                        "Duplicate File Finder",
                        "Find files with matching SHA-256 content hashes.",
                        Mode.DUPLICATES
                )
        );

        tabs.addTab(
                "Storage Breakdown",
                createScanPage(
                        "File Type Analytics",
                        "See how storage is distributed by file extension.",
                        Mode.BREAKDOWN
                )
        );

        tabs.addTab(
                "Scan History",
                createHistoryPage()
        );

        tabs.addTab(
                "Reports",
                createReportsPage()
        );

        tabs.addTab(
                "Settings",
                createSettingsPage()
        );

        add(tabs, BorderLayout.CENTER);
    }

    private enum Mode {
        ALL,
        LARGE,
        DUPLICATES,
        BREAKDOWN
    }

    private JPanel createScanPage(
            String heading,
            String description,
            Mode mode
    ) {

        JPanel page = basePage();

        JLabel pageTitle = title(heading);
        JLabel desc = subtitle(description);

        JTextField pathField =
                new JTextField(System.getProperty("user.home"));

        styleField(pathField);

        JButton browse = button("Choose Folder");
        JButton scan = button("Start Scan");

        JLabel status =
                subtitle("Choose a folder, then start scanning.");

        JLabel summary = subtitle("");

        DefaultTableModel model =
                new DefaultTableModel();

        JTable table = new JTable(model);

        styleTable(table);

        JScrollPane tableScroll =
                new JScrollPane(table);

        styleScroll(tableScroll);

        JPanel pathRow =
                new JPanel(new BorderLayout(8, 0));

        pathRow.setBackground(Theme.BACKGROUND);

        pathRow.add(pathField, BorderLayout.CENTER);

        JPanel actions =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        actions.setBackground(Theme.BACKGROUND);
        actions.add(browse);
        actions.add(scan);

        pathRow.add(actions, BorderLayout.EAST);

        page.add(pageTitle);
        page.add(Box.createVerticalStrut(5));
        page.add(desc);
        page.add(Box.createVerticalStrut(18));
        page.add(pathRow);
        page.add(Box.createVerticalStrut(10));
        page.add(status);
        page.add(Box.createVerticalStrut(5));
        page.add(summary);
        page.add(Box.createVerticalStrut(12));
        page.add(tableScroll);

        browse.addActionListener(e -> {

            JFileChooser chooser =
                    new JFileChooser(pathField.getText());

            chooser.setFileSelectionMode(
                    JFileChooser.DIRECTORIES_ONLY
            );

            if (chooser.showOpenDialog(this)
                    == JFileChooser.APPROVE_OPTION) {

                pathField.setText(
                        chooser.getSelectedFile().getAbsolutePath()
                );
            }
        });

        scan.addActionListener(e -> {

            Path selected;

            try {
                selected = Path.of(pathField.getText().trim());

            } catch (Exception ex) {
                error("Enter a valid folder path.");
                return;
            }

            if (!Files.isDirectory(selected)) {
                error("The selected path is not an accessible folder.");
                return;
            }

            scan.setEnabled(false);
            scan.setText("Scanning...");

            status.setText(
                    mode == Mode.DUPLICATES
                            ? "Scanning and hashing files..."
                            : "Scanning files..."
            );

            summary.setText("");
            model.setRowCount(0);

            new SwingWorker<List<FileRecord>, Void>() {

                @Override
                protected List<FileRecord> doInBackground()
                        throws Exception {

                    return service.scan(
                            selected,
                            mode == Mode.DUPLICATES,
                            new AtomicLong()
                    );
                }

                @Override
                protected void done() {

                    try {

                        List<FileRecord> records = get();

                        long total = records.stream()
                                .mapToLong(FileRecord::size)
                                .sum();

                        history.add(
                                selected.toString(),
                                records.size(),
                                total
                        );

                        List<FileRecord> shown = records;

                        if (mode == Mode.LARGE) {

                            shown = records.stream()
                                    .limit(100)
                                    .toList();
                        }

                        if (mode == Mode.DUPLICATES) {

                            Map<String, List<FileRecord>> groups =
                                    records.stream()
                                            .filter(r -> !r.sha256().isBlank())
                                            .collect(Collectors.groupingBy(
                                                    FileRecord::sha256
                                            ));

                            shown = groups.values().stream()
                                    .filter(group -> group.size() > 1)
                                    .flatMap(Collection::stream)
                                    .sorted(
                                            Comparator.comparingLong(
                                                    FileRecord::size
                                            ).reversed()
                                    )
                                    .toList();
                        }

                        if (mode == Mode.BREAKDOWN) {

                            Map<String, Long> sizes =
                                    records.stream()
                                            .collect(Collectors.groupingBy(
                                                    FileRecord::extension,
                                                    TreeMap::new,
                                                    Collectors.summingLong(
                                                            FileRecord::size
                                                    )
                                            ));

                            model.setColumnIdentifiers(
                                    new Object[]{
                                            "File Extension",
                                            "Files",
                                            "Total Size",
                                            "Share"
                                    }
                            );

                            long grand = total;

                            sizes.forEach((ext, size) -> {

                                long count = records.stream()
                                        .filter(r -> r.extension().equals(ext))
                                        .count();

                                model.addRow(new Object[]{
                                        ext,
                                        count,
                                        FormatUtils.formatBytes(size),
                                        String.format(
                                                "%.1f%%",
                                                FormatUtils.getPercentage(
                                                        size,
                                                        grand
                                                )
                                        )
                                });
                            });

                        } else {

                            if (mode == Mode.DUPLICATES) {

                                model.setColumnIdentifiers(
                                        new Object[]{
                                                "File Name",
                                                "Size",
                                                "Location",
                                                "SHA-256"
                                        }
                                );

                            } else {

                                model.setColumnIdentifiers(
                                        new Object[]{
                                                "File Name",
                                                "Size",
                                                "Last Modified",
                                                "Location"
                                        }
                                );
                            }

                            for (FileRecord record : shown) {

                                if (mode == Mode.DUPLICATES) {

                                    model.addRow(new Object[]{
                                            record.fileName(),
                                            FormatUtils.formatBytes(
                                                    record.size()
                                            ),
                                            record.path().toString(),
                                            record.sha256()
                                    });

                                } else {

                                    String modified =
                                            DateTimeFormatter
                                                    .ofPattern(
                                                            "yyyy-MM-dd HH:mm"
                                                    )
                                                    .withZone(
                                                            ZoneId.systemDefault()
                                                    )
                                                    .format(record.modified());

                                    model.addRow(new Object[]{
                                            record.fileName(),
                                            FormatUtils.formatBytes(
                                                    record.size()
                                            ),
                                            modified,
                                            record.path().toString()
                                    });
                                }
                            }
                        }

                        summary.setText(
                                "Files scanned: " + records.size()
                                + "   •   Total size: "
                                + FormatUtils.formatBytes(total)
                                + (mode == Mode.LARGE
                                        ? "   •   Showing top 100"
                                        : mode == Mode.DUPLICATES
                                        ? "   •   Duplicate files: "
                                                + shown.size()
                                        : "")
                        );

                        status.setText(
                                "Scan completed successfully."
                        );

                    } catch (Exception ex) {

                        status.setText(
                                "Scan failed: " + ex.getMessage()
                        );

                    } finally {

                        scan.setEnabled(true);
                        scan.setText("Start Scan");
                    }
                }

            }.execute();
        });

        return page;
    }

    private JPanel createHistoryPage() {

        JPanel page = basePage();

        page.add(title("Scan History"));
        page.add(Box.createVerticalStrut(5));

        page.add(subtitle(
                "Previous scans saved locally in data/scan-history.csv."
        ));

        page.add(Box.createVerticalStrut(15));

        DefaultTableModel model =
                new DefaultTableModel(
                        new Object[]{
                                "Date",
                                "Location",
                                "Files",
                                "Total Size"
                        },
                        0
                );

        JTable table = new JTable(model);

        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);

        styleScroll(scroll);

        page.add(scroll);

        JButton reload = button("Refresh History");

        reload.addActionListener(e -> loadHistory(model));

        JPanel row = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        row.setBackground(Theme.BACKGROUND);
        row.add(reload);

        page.add(row);

        loadHistory(model);

        return page;
    }

    private void loadHistory(DefaultTableModel model) {

        model.setRowCount(0);

        for (String[] record : history.readAll()) {

            long bytes = 0;

            try {
                bytes = Long.parseLong(record[3]);
            } catch (Exception ignored) {
            }

            model.addRow(new Object[]{
                    record[0],
                    record[1],
                    record[2],
                    FormatUtils.formatBytes(bytes)
            });
        }
    }

    private JPanel createReportsPage() {

        JPanel page = basePage();

        page.add(title("Reports & Export"));
        page.add(Box.createVerticalStrut(5));

        page.add(subtitle(
                "Preview and export your saved scan history."
        ));

        page.add(Box.createVerticalStrut(15));

        reportArea.setEditable(false);
        reportArea.setBackground(Theme.PANEL);
        reportArea.setForeground(Theme.TEXT);
        reportArea.setFont(
                new Font("Consolas", Font.PLAIN, 12)
        );

        reportArea.setBorder(
                new EmptyBorder(12, 12, 12, 12)
        );

        JButton preview = button("Preview History");
        JButton export = button("Export CSV");

        JPanel row = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        row.setBackground(Theme.BACKGROUND);

        row.add(preview);
        row.add(export);

        preview.addActionListener(e -> {

            StringBuilder text = new StringBuilder(
                    "DATE | LOCATION | FILE COUNT | TOTAL SIZE\n\n"
            );

            for (String[] record : history.readAll()) {

                text.append(String.join(" | ", record))
                        .append("\n");
            }

            reportArea.setText(text.toString());
        });

        export.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();

            chooser.setSelectedFile(
                    new File("DiskScope-Scan-History.csv")
            );

            if (chooser.showSaveDialog(this)
                    == JFileChooser.APPROVE_OPTION) {

                try {

                    Files.copy(
                            history.getHistoryFile(),
                            chooser.getSelectedFile().toPath(),
                            StandardCopyOption.REPLACE_EXISTING
                    );

                    JOptionPane.showMessageDialog(
                            this,
                            "CSV report exported successfully."
                    );

                } catch (IOException ex) {

                    error(
                            "Could not export report: "
                                    + ex.getMessage()
                    );
                }
            }
        });

        page.add(row);
        page.add(new JScrollPane(reportArea));

        return page;
    }

    private JPanel createSettingsPage() {

        JPanel page = basePage();

        page.add(title("Settings"));
        page.add(Box.createVerticalStrut(5));

        page.add(subtitle(
                "Configure basic DiskScope preferences."
        ));

        page.add(Box.createVerticalStrut(18));

        JCheckBox confirm = new JCheckBox(
                "Ask before opening file locations",
                prefs.getBoolean("confirmOpen", true)
        );

        confirm.setForeground(Theme.TEXT);
        confirm.setBackground(Theme.BACKGROUND);

        JLabel intervalLabel =
                subtitle("Dashboard refresh interval (seconds)");

        JSpinner interval = new JSpinner(
                new SpinnerNumberModel(
                        prefs.getInt("refreshInterval", 5),
                        2,
                        120,
                        1
                )
        );

        JButton save = button("Save Settings");

        save.addActionListener(e -> {

            prefs.putBoolean(
                    "confirmOpen",
                    confirm.isSelected()
            );

            prefs.putInt(
                    "refreshInterval",
                    (Integer) interval.getValue()
            );

            settingsStatus.setText("Preferences saved.");
        });

        page.add(confirm);
        page.add(Box.createVerticalStrut(15));
        page.add(intervalLabel);
        page.add(Box.createVerticalStrut(6));
        page.add(interval);
        page.add(Box.createVerticalStrut(18));
        page.add(save);
        page.add(Box.createVerticalStrut(10));

        settingsStatus.setForeground(Theme.GREEN);

        page.add(settingsStatus);

        page.add(Box.createVerticalStrut(12));

        page.add(subtitle(
                "The refresh interval is saved as a preference. "
                + "Your existing dashboard timer remains unchanged."
        ));

        return page;
    }

    private JPanel basePage() {

        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.setBackground(Theme.BACKGROUND);

        panel.setBorder(
                new EmptyBorder(22, 24, 24, 24)
        );

        return panel;
    }

    private JLabel title(String text) {

        JLabel label = new JLabel(text);

        label.setForeground(Theme.TEXT);

        label.setFont(
                new Font("Segoe UI", Font.BOLD, 22)
        );

        label.setAlignmentX(LEFT_ALIGNMENT);

        return label;
    }

    private JLabel subtitle(String text) {

        JLabel label = new JLabel(text);

        label.setForeground(Theme.MUTED);

        label.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        label.setAlignmentX(LEFT_ALIGNMENT);

        return label;
    }

    private JButton button(String text) {

        JButton button = new JButton(text);

        button.setForeground(Theme.TEXT);
        button.setBackground(Theme.PANEL_LIGHT);
        button.setFocusPainted(false);

        button.setBorder(
                new EmptyBorder(9, 14, 9, 14)
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    private void styleField(JTextField field) {

        field.setBackground(Theme.PANEL);
        field.setForeground(Theme.TEXT);
        field.setCaretColor(Theme.TEXT);

        field.setBorder(
                new EmptyBorder(9, 10, 9, 10)
        );
    }

    private void styleTable(JTable table) {

        table.setBackground(Theme.PANEL);
        table.setForeground(Theme.TEXT);
        table.setGridColor(Theme.PANEL_LIGHT);
        table.setSelectionBackground(Theme.BLUE);
        table.setRowHeight(25);

        table.getTableHeader().setBackground(
                Theme.PANEL_LIGHT
        );

        table.getTableHeader().setForeground(
                Theme.TEXT
        );
    }

    private void styleScroll(JScrollPane scroll) {

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        Theme.PANEL_LIGHT
                )
        );

        scroll.getViewport().setBackground(Theme.PANEL);
    }

    private void error(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "DiskScope",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
