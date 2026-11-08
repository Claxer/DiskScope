package com.diskscope.ui;

import com.diskscope.scanner.EmptyFolderScanner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmptyFolderPanel extends JPanel {

    private final EmptyFolderScanner scanner =
            new EmptyFolderScanner();

    private final JTextField pathField =
            new JTextField(System.getProperty("user.home"));

    private final JLabel status =
            new JLabel("Choose a folder to begin scanning.");

    private final JLabel summary =
            new JLabel("");

    private final DefaultTableModel model =
            new DefaultTableModel(
                    new Object[]{
                            "Folder Name",
                            "Last Modified",
                            "Folder Location"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(
                        int row,
                        int column
                ) {
                    return false;
                }
            };

    private final JTable table =
            new JTable(model);

    private final JButton scanButton =
            new JButton("Find Empty Folders");

    private final JButton browseButton =
            new JButton("Choose Folder");

    private final JButton openButton =
            new JButton("Open Location");

    private final JButton exportButton =
            new JButton("Export CSV");

    public EmptyFolderPanel() {

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        JPanel content = new JPanel();

        content.setLayout(
                new BoxLayout(content, BoxLayout.Y_AXIS)
        );

        content.setBackground(Theme.BACKGROUND);

        content.setBorder(
                new EmptyBorder(22, 24, 24, 24)
        );

        JLabel heading = new JLabel("Empty Folder Finder");

        heading.setForeground(Theme.TEXT);
        heading.setFont(
                new Font("Segoe UI", Font.BOLD, 22)
        );

        JLabel description = new JLabel(
                "Find empty directories inside a selected folder."
        );

        description.setForeground(Theme.MUTED);
        description.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        styleField(pathField);
        styleButton(scanButton);
        styleButton(browseButton);
        styleButton(openButton);
        styleButton(exportButton);
        styleTable(table);

        table.setAutoResizeMode(
                JTable.AUTO_RESIZE_LAST_COLUMN
        );

        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(180);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(150);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(450);

        status.setForeground(Theme.MUTED);
        summary.setForeground(Theme.TEXT);

        JPanel pathRow = new JPanel(
                new BorderLayout(8, 0)
        );

        pathRow.setBackground(Theme.BACKGROUND);
        pathRow.add(pathField, BorderLayout.CENTER);

        JPanel pathButtons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        pathButtons.setBackground(Theme.BACKGROUND);
        pathButtons.add(browseButton);
        pathButtons.add(scanButton);

        pathRow.add(pathButtons, BorderLayout.EAST);

        JPanel toolbar = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0)
        );

        toolbar.setBackground(Theme.BACKGROUND);
        toolbar.add(openButton);
        toolbar.add(exportButton);

        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        Theme.PANEL_LIGHT
                )
        );

        scroll.getViewport().setBackground(Theme.PANEL);

        content.add(heading);
        content.add(Box.createVerticalStrut(5));
        content.add(description);
        content.add(Box.createVerticalStrut(18));
        content.add(pathRow);
        content.add(Box.createVerticalStrut(12));
        content.add(status);
        content.add(Box.createVerticalStrut(6));
        content.add(summary);
        content.add(Box.createVerticalStrut(12));
        content.add(toolbar);
        content.add(Box.createVerticalStrut(10));
        content.add(scroll);

        add(content, BorderLayout.CENTER);

        browseButton.addActionListener(
                e -> chooseFolder()
        );

        scanButton.addActionListener(
                e -> scanFolders()
        );

        openButton.addActionListener(
                e -> openSelectedLocation()
        );

        exportButton.addActionListener(
                e -> exportResults()
        );
    }

    private void chooseFolder() {

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
    }

    private void scanFolders() {

        Path selected;

        try {

            selected = Path.of(
                    pathField.getText().trim()
            );

        } catch (Exception ex) {

            showError("Enter a valid folder path.");
            return;
        }

        if (!Files.isDirectory(selected)
                || !Files.isReadable(selected)) {

            showError("The selected folder is not accessible.");
            return;
        }

        scanButton.setEnabled(false);
        browseButton.setEnabled(false);
        scanButton.setText("Scanning...");

        status.setText("Searching for empty folders...");
        summary.setText("");

        model.setRowCount(0);

        new SwingWorker<List<EmptyFolderScanner.EmptyFolder>, Void>() {

            @Override
            protected List<EmptyFolderScanner.EmptyFolder>
            doInBackground() throws Exception {

                return scanner.scan(selected);
            }

            @Override
            protected void done() {

                try {

                    List<EmptyFolderScanner.EmptyFolder> results =
                            get();

                    DateTimeFormatter formatter =
                            DateTimeFormatter
                                    .ofPattern("yyyy-MM-dd HH:mm")
                                    .withZone(ZoneId.systemDefault());

                    for (EmptyFolderScanner.EmptyFolder folder : results) {

                        Path folderPath = folder.path();

                        String folderName =
                                folderPath.getFileName() == null
                                        ? folderPath.toString()
                                        : folderPath.getFileName().toString();

                        String modified =
                                folder.modified() == null
                                        ? "Unknown"
                                        : formatter.format(folder.modified());

                        model.addRow(new Object[]{
                                folderName,
                                modified,
                                folderPath.toString()
                        });
                    }

                    summary.setText(
                            "Empty folders found: " + results.size()
                    );

                    status.setText(
                            results.isEmpty()
                                    ? "Scan completed. No empty folders were found."
                                    : "Scan completed successfully."
                    );

                } catch (Exception ex) {

                    status.setText(
                            "Scan failed: " + ex.getMessage()
                    );

                } finally {

                    scanButton.setEnabled(true);
                    browseButton.setEnabled(true);
                    scanButton.setText("Find Empty Folders");
                }
            }

        }.execute();
    }

    private void openSelectedLocation() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            showError("Select a folder from the table first.");
            return;
        }

        String location = String.valueOf(
                model.getValueAt(selectedRow, 2)
        );

        try {

            Path folder = Path.of(location);

            if (!Files.isDirectory(folder)) {
                showError("The selected folder no longer exists.");
                return;
            }

            if (!Desktop.isDesktopSupported()) {
                showError("Opening folders is not supported on this system.");
                return;
            }

            Desktop.getDesktop().open(folder.toFile());

        } catch (Exception ex) {

            showError(
                    "Could not open folder: " + ex.getMessage()
            );
        }
    }

    private void exportResults() {

        if (model.getRowCount() == 0) {
            showError("Scan for empty folders before exporting.");
            return;
        }

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Export Empty Folders");

        chooser.setSelectedFile(
                new File("DiskScope-Empty-Folders.csv")
        );

        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        Path destination =
                chooser.getSelectedFile().toPath();

        try (
                BufferedWriter writer = Files.newBufferedWriter(
                        destination,
                        StandardCharsets.UTF_8
                )
        ) {

            writer.write(
                    "Folder Name,Last Modified,Folder Location"
            );

            writer.newLine();

            for (int row = 0; row < model.getRowCount(); row++) {

                writer.write(
                        csv(String.valueOf(model.getValueAt(row, 0)))
                                + ","
                                + csv(String.valueOf(model.getValueAt(row, 1)))
                                + ","
                                + csv(String.valueOf(model.getValueAt(row, 2)))
                );

                writer.newLine();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Empty folder results exported successfully.\n\n"
                            + destination,
                    "DiskScope",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException ex) {

            showError(
                    "Could not export results: " + ex.getMessage()
            );
        }
    }

    private String csv(String value) {

        return "\""
                + value.replace("\"", "\"\"")
                + "\"";
    }

    private void styleField(JTextField field) {

        field.setBackground(Theme.PANEL);
        field.setForeground(Theme.TEXT);
        field.setCaretColor(Theme.TEXT);

        field.setBorder(
                new EmptyBorder(9, 10, 9, 10)
        );
    }

    private void styleButton(JButton button) {

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
    }

    private void styleTable(JTable table) {

        table.setBackground(Theme.PANEL);
        table.setForeground(Theme.TEXT);
        table.setGridColor(Theme.PANEL_LIGHT);
        table.setSelectionBackground(Theme.BLUE);
        table.setRowHeight(26);

        table.getTableHeader().setBackground(
                Theme.PANEL_LIGHT
        );

        table.getTableHeader().setForeground(
                Theme.TEXT
        );
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "DiskScope",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
