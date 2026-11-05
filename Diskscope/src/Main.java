package com.diskscope;

import com.diskscope.ui.DashboardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main extends JFrame {

    private static final Color BACKGROUND = new Color(15, 17, 21);
    private static final Color SIDEBAR = new Color(20, 22, 27);
    private static final Color PANEL = new Color(26, 29, 35);
    private static final Color PANEL_LIGHT = new Color(32, 36, 43);
    private static final Color ACCENT = new Color(70, 130, 220);
    private static final Color TEXT = new Color(235, 238, 243);
    private static final Color MUTED = new Color(145, 152, 163);

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private final JButton[] navigationButtons = new JButton[8];
    private JButton activeButton;

    private final String[] pageNames = {
            "Dashboard",
            "Drive Analyzer",
            "Large Files",
            "Duplicate Finder",
            "Storage Breakdown",
            "Scan History",
            "Reports",
            "Settings"
    };

    public Main() {
        setTitle("DiskScope - Storage Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 800);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        createSidebar();
        createMainArea();

        showPage("Dashboard", 0);
    }

    private void createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(28, 20, 20, 20));

        JLabel logo = new JLabel("DiskScope");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        logo.setForeground(TEXT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("STORAGE ANALYZER");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(logo);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(subtitle);
        topPanel.add(Box.createVerticalStrut(35));

        JLabel navigationLabel = new JLabel("WORKSPACE");
        navigationLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navigationLabel.setForeground(MUTED);
        navigationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(navigationLabel);
        topPanel.add(Box.createVerticalStrut(12));

        for (int i = 0; i < pageNames.length; i++) {
            final int index = i;
            JButton button = createNavigationButton(pageNames[i]);

            navigationButtons[i] = button;
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.addActionListener(e -> showPage(pageNames[index], index));

            topPanel.add(button);
            topPanel.add(Box.createVerticalStrut(6));
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(15, 20, 22, 20));

        JSeparator separator = new JSeparator();
        separator.setForeground(PANEL_LIGHT);
        separator.setBackground(PANEL_LIGHT);

        JLabel version = new JLabel("DiskScope v2.0");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        version.setForeground(MUTED);

        bottomPanel.add(separator);
        bottomPanel.add(Box.createVerticalStrut(15));
        bottomPanel.add(version);

        sidebar.add(topPanel, BorderLayout.NORTH);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
    }

    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(MUTED);
        button.setBackground(SIDEBAR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 14, 12, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        button.setPreferredSize(new Dimension(195, 43));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(PANEL_LIGHT);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(SIDEBAR);
                }
            }
        });

        return button;
    }

    private void createMainArea() {
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        header.setBorder(new EmptyBorder(22, 30, 18, 30));

        JLabel pageTitle = new JLabel("Storage Overview");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(TEXT);

        JLabel status = new JLabel("● SYSTEM MONITORING");
        status.setFont(new Font("Segoe UI", Font.BOLD, 10));
        status.setForeground(new Color(80, 190, 130));

        header.add(pageTitle, BorderLayout.WEST);
        header.add(status, BorderLayout.EAST);

        contentPanel.setBackground(BACKGROUND);

        // Existing dashboard
        contentPanel.add(new DashboardPanel(), "Dashboard");

        // Other navigation pages
        addPlaceholder(
                "Drive Analyzer",
                "Explore connected drives and inspect their storage details.",
                "Drive analysis tools will be added in the next development stage."
        );

        addPlaceholder(
                "Large Files",
                "Find files that consume significant storage space.",
                "Large file scanning will be added in the next development stage."
        );

        addPlaceholder(
                "Duplicate Finder",
                "Identify duplicate files to help organize your storage.",
                "Duplicate detection will be added in the next development stage."
        );

        addPlaceholder(
                "Storage Breakdown",
                "Understand how storage space is distributed across folders.",
                "Folder storage analysis will be added in the next development stage."
        );

        addPlaceholder(
                "Scan History",
                "Review previous storage scans and their results.",
                "Scan history will be added in the next development stage."
        );

        addPlaceholder(
                "Reports",
                "View and export storage analysis reports.",
                "Report generation will be added in the next development stage."
        );

        addPlaceholder(
                "Settings",
                "Customize DiskScope and manage application preferences.",
                "Application settings will be added in the next development stage."
        );

        mainArea.add(header, BorderLayout.NORTH);
        mainArea.add(contentPanel, BorderLayout.CENTER);

        add(mainArea, BorderLayout.CENTER);
    }

    private void addPlaceholder(String name, String description, String message) {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(BACKGROUND);

        JPanel card = new JPanel();
        card.setBackground(PANEL);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(35, 40, 35, 40));

        JLabel title = new JLabel(name);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel details = new JLabel(description);
        details.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        details.setForeground(MUTED);
        details.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel comingSoon = new JLabel("COMING SOON");
        comingSoon.setFont(new Font("Segoe UI", Font.BOLD, 10));
        comingSoon.setForeground(ACCENT);
        comingSoon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel information = new JLabel(message);
        information.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        information.setForeground(MUTED);
        information.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(12));
        card.add(details);
        card.add(Box.createVerticalStrut(30));
        card.add(comingSoon);
        card.add(Box.createVerticalStrut(10));
        card.add(information);

        page.add(card);
        contentPanel.add(page, name);
    }

    private void showPage(String pageName, int buttonIndex) {
        cardLayout.show(contentPanel, pageName);
        activeButton = navigationButtons[buttonIndex];

        for (JButton button : navigationButtons) {
            if (button != null) {
                button.setBackground(SIDEBAR);
                button.setForeground(MUTED);
            }
        }

        activeButton.setBackground(PANEL_LIGHT);
        activeButton.setForeground(TEXT);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            Main app = new Main();
            app.setVisible(true);
        });
    }
}
