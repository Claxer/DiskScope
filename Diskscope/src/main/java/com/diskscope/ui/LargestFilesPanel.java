package com.diskscope.ui;

import com.diskscope.scanner.FolderScanner;
import com.diskscope.scanner.FolderScanner.FileRecord;
import com.diskscope.scanner.FolderScanner.ScanResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LargestFilesPanel extends JPanel {

    private final FolderScanner scanner = new FolderScanner();

    private final DefaultTableModel tableModel;
    private final JTable fileTable;

    private final JTextField searchField;
    private final JLabel statusLabel;
    private final JLabel totalFilesLabel;
    private final JLabel totalSizeLabel;
    private final JLabel folderLabel;

    private List<FileRecord> scannedFiles;

    private final Color background = new Color(24, 24, 27);
    private final Color panelColor = new Color(35, 35, 40);
    private final Color accent = new Color(80, 150, 255);
    private final Color textColor = new Color(235, 235, 235);
    private final Color secondaryText = new Color(160, 160, 170);

    public LargestFilesPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(background);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        scannedFiles = List.of();

        // ==============================
        // HEADER
        // ==============================

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(
            titlePanel, BoxLayout.Y_AXIS
        ));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Largest Files Finder");
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(textColor);

        JLabel subtitle = new JLabel(
            "Find files consuming the most storage space"
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(secondaryText);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitle);

        JButton scanButton = createButton("Scan Folder", accent);

        scanButton.addActionListener(e -> chooseFolder());

        header.add(titlePanel, BorderLayout.WEST);
        header.add(scanButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ==============================
        // CENTER CONTENT
        // ==============================

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setOpaque(false);

        JPanel statistics = new JPanel(
            new GridLayout(1, 3, 15, 0)
        );
        statistics.setOpaque(false);

        totalFilesLabel = new JLabel("0");
        totalSizeLabel = new JLabel("0 B");
        folderLabel = new JLabel("Not scanned");

        statistics.add(createStatCard(
            "FILES FOUND", totalFilesLabel
        ));

        statistics.add(createStatCard(
            "TOTAL SCANNED SIZE", totalSizeLabel
        ));

        statistics.add(createStatCard(
            "SCAN LOCATION", folderLabel
        ));

        content.add(statistics, BorderLayout.NORTH);

        // ==============================
        // TABLE
        // ==============================

        JPanel tableContainer = new JPanel(new BorderLayout(0, 12));
        tableContainer.setBackground(panelColor);
        tableContainer.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JLabel tableTitle = new JLabel("Largest Files");
        tableTitle.setForeground(textColor);
        tableTitle.setFont(
            new Font("Segoe UI", Font.BOLD, 17)
        );

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(230, 34));
        searchField.setBackground(background);
        searchField.setForeground(textColor);
        searchField.setCaretColor(textColor);
        searchField.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(65, 65, 75)
                ),
                new EmptyBorder(5, 10, 5, 10)
            )
        );

        searchField.putClientProperty(
            "JTextField.placeholderText",
            "Search files..."
        );

        toolbar.add(tableTitle, BorderLayout.WEST);
        toolbar.add(searchField, BorderLayout.EAST);

        String[] columns = {
            "File Name", "File Type", "File Size", "Location"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 2 ? Long.class : String.class;
            }
        };

        fileTable = new JTable(tableModel);
        fileTable.setBackground(panelColor);
        fileTable.setForeground(textColor);
        fileTable.setGridColor(new Color(55, 55, 62));
        fileTable.setRowHeight(38);
        fileTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fileTable.setSelectionBackground(
            new Color(55, 85, 130)
        );
        fileTable.setSelectionForeground(Color.WHITE);
        fileTable.setShowVerticalLines(false);
        fileTable.setAutoCreateRowSorter(true);

        fileTable.getTableHeader().setBackground(
            new Color(43, 43, 49)
        );
        fileTable.getTableHeader().setForeground(textColor);
        fileTable.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 12)
        );
        fileTable.getTableHeader().setReorderingAllowed(false);

        fileTable.getColumnModel().getColumn(0)
            .setPreferredWidth(180);
        fileTable.getColumnModel().getColumn(1)
            .setPreferredWidth(90);
        fileTable.getColumnModel().getColumn(2)
            .setPreferredWidth(100);
        fileTable.getColumnModel().getColumn(3)
            .setPreferredWidth(350);

        TableRowSorter<DefaultTableModel> sorter =
            new TableRowSorter<>(tableModel);

        fileTable.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {

                private void filter() {

                    String query = searchField.getText()
                        .trim();

                    if (query.isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(
                            RowFilter.regexFilter(
                                "(?i)" + java.util.regex.Pattern
                                    .quote(query)
                            )
                        );
                    }
                }

                @Override
                public void insertUpdate(
                        javax.swing.event.DocumentEvent e) {
                    filter();
                }

                @Override
                public void removeUpdate(
                        javax.swing.event.DocumentEvent e) {
                    filter();
                }

                @Override
                public void changedUpdate(
                        javax.swing.event.DocumentEvent e) {
                    filter();
                }
            }
        );

        JScrollPane scrollPane = new JScrollPane(fileTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(panelColor);

        // ==============================
        // TABLE ACTIONS
        // ==============================

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        statusLabel = new JLabel("Select a folder to begin scanning.");
        statusLabel.setForeground(secondaryText);
        statusLabel.setFont(
            new Font("Segoe UI", Font.PLAIN, 12)
        );

        JButton openButton = createButton(
            "Open Location", new Color(65, 65, 75)
        );

        openButton.addActionListener(e -> openSelectedLocation());

        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(openButton, BorderLayout.EAST);

        JPanel tableContent = new JPanel(new BorderLayout(0, 12));
        tableContent.setOpaque(false);

        tableContent.add(toolbar, BorderLayout.NORTH);
        tableContent.add(scrollPane, BorderLayout.CENTER);
        tableContent.add(bottom, BorderLayout.SOUTH);

        tableContainer.add(tableContent, BorderLayout.CENTER);

        content.add(tableContainer, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    // ==============================
    // FOLDER SELECTION
    // ==============================

    private void chooseFolder() {

        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(
            JFileChooser.DIRECTORIES_ONLY
        );

        chooser.setDialogTitle("Select Folder to Analyze");

        if (chooser.showOpenDialog(this)
                == JFileChooser.APPROVE_OPTION) {

            scanFolder(chooser.getSelectedFile());
        }
    }

    // ==============================
    // SCAN FOLDER
    // ==============================

    private void scanFolder(File folder) {

        statusLabel.setText("Scanning folder...");
        setCursor(Cursor.getPredefinedCursor(
            Cursor.WAIT_CURSOR
        ));

        tableModel.setRowCount(0);

        JButton[] unused = {};

        SwingWorker<ScanResult, Void> worker =
            new SwingWorker<>() {

                @Override
                protected ScanResult doInBackground() {
                    return scanner.scanFolder(folder);
                }

                @Override
                protected void done() {

                    try {

                        ScanResult result = get();

                        scannedFiles = result.getFiles();

                        for (FileRecord file : scannedFiles) {

                            tableModel.addRow(new Object[] {
                                file.getName(),
                                file.getExtension().toUpperCase(
                                    Locale.ROOT
                                ),
                                file.getSize(),
                                file.getPath()
                            });
                        }

                        totalFilesLabel.setText(
                            String.valueOf(result.getTotalFiles())
                        );

                        totalSizeLabel.setText(
                            result.getFormattedTotalSize()
                        );

                        folderLabel.setText(folder.getName().isEmpty()
                            ? folder.getAbsolutePath()
                            : folder.getName()
                        );

                        statusLabel.setText(
                            "Scan completed | "
                            + result.getInaccessibleItems()
                            + " inaccessible items"
                        );

                    } catch (Exception e) {

                        statusLabel.setText("Scan failed.");

                        JOptionPane.showMessageDialog(
                            LargestFilesPanel.this,
                            "Unable to scan folder:\n"
                                + e.getMessage(),
                            "Scan Error",
                            JOptionPane.ERROR_MESSAGE
                        );

                    } finally {

                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            };

        worker.execute();
    }

    // ==============================
    // OPEN FILE LOCATION
    // ==============================

    private void openSelectedLocation() {

        int selectedRow = fileTable.getSelectedRow();

        if (selectedRow < 0) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a file first.",
                "No File Selected",
                JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int modelRow = fileTable.convertRowIndexToModel(
            selectedRow
        );

        String path = (String) tableModel.getValueAt(
            modelRow, 3
        );

        File file = new File(path);
        File parent = file.getParentFile();

        if (parent == null || !parent.exists()) {
            return;
        }

        if (!Desktop.isDesktopSupported()) {
            JOptionPane.showMessageDialog(
                this,
                "Desktop integration is unavailable."
            );
            return;
        }

        try {

            Desktop.getDesktop().open(parent);

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to open file location:\n"
                    + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==============================
    // STATISTICS CARD
    // ==============================

    private JPanel createStatCard(
            String title,
            JLabel valueLabel) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(
            card, BoxLayout.Y_AXIS
        ));

        card.setBackground(panelColor);
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel heading = new JLabel(title);
        heading.setForeground(secondaryText);
        heading.setFont(
            new Font("Segoe UI", Font.BOLD, 11)
        );

        valueLabel.setForeground(textColor);
        valueLabel.setFont(
            new Font("Segoe UI", Font.BOLD, 21)
        );

        card.add(heading);
        card.add(Box.createVerticalStrut(12));
        card.add(valueLabel);

        return card;
    }

    // ==============================
    // BUTTON DESIGN
    // ==============================

    private JButton createButton(
            String text,
            Color color) {

        JButton button = new JButton(text);

        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(
            new EmptyBorder(10, 18, 10, 18)
        );
        button.setFont(
            new Font("Segoe UI", Font.BOLD, 12)
        );
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        return button;
    }
}
