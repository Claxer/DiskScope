package com.diskscope;

import com.diskscope.ui.DashboardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Main extends JFrame {

    private final Color BACKGROUND = new Color(17, 17, 19);
    private final Color SIDEBAR = new Color(23, 23, 26);
    private final Color PANEL = new Color(30, 30, 34);
    private final Color TEXT = new Color(240, 240, 242);
    private final Color MUTED = new Color(150, 150, 158);

    private JPanel contentPanel;
    private JLabel pageTitle;

    private DashboardPanel dashboardPanel;

    public Main() {

        setTitle("DiskScope - Storage Analyzer");
        setSize(1250, 780);
        setMinimumSize(new Dimension(1050, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND);

        createSidebar();
        createMainContent();

        showPage("Dashboard");

    }

    private void createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 15, 20, 15));

        JLabel logo = new JLabel("  DiskScope");
        logo.setFont(new Font("SansSerif", Font.BOLD, 23));
        logo.setForeground(TEXT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("  STORAGE ANALYZER");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(40));

        addSectionLabel(sidebar, "WORKSPACE");

        addNavigationButton(sidebar, "Dashboard");
        addNavigationButton(sidebar, "Drive Analyzer");
        addNavigationButton(sidebar, "Large Files");
        addNavigationButton(sidebar, "Duplicate Finder");

        sidebar.add(Box.createVerticalStrut(25));

        addSectionLabel(sidebar, "MANAGEMENT");

        addNavigationButton(sidebar, "Reports");
        addNavigationButton(sidebar, "Scan History");

        sidebar.add(Box.createVerticalGlue());

        JLabel version = new JLabel("DiskScope v1.0.0");
        version.setForeground(MUTED);
        version.setFont(new Font("SansSerif", Font.PLAIN, 11));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(version);

        add(sidebar, BorderLayout.WEST);

    }

    private void addSectionLabel(JPanel sidebar, String text) {

        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 8, 12, 0));

        sidebar.add(label);

    }

    private void addNavigationButton(JPanel sidebar, String name) {

        JButton button = new JButton(name);

        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setForeground(MUTED);
        button.setBackground(SIDEBAR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 12, 12, 8));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> showPage(name));

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(PANEL);
                button.setForeground(TEXT);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(SIDEBAR);
                button.setForeground(MUTED);
            }

        });

        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(4));

    }

    private void createMainContent() {

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        header.setBorder(new EmptyBorder(25, 30, 20, 30));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setForeground(TEXT);
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel headerSubtitle = new JLabel(
            "Monitor and analyze your storage"
        );

        headerSubtitle.setForeground(MUTED);
        headerSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND);

        titlePanel.add(pageTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(headerSubtitle);

        JLabel status = new JLabel("● SYSTEM READY");
        status.setForeground(new Color(100, 200, 140));
        status.setFont(new Font("SansSerif", Font.BOLD, 11));

        header.add(titlePanel, BorderLayout.WEST);
        header.add(status, BorderLayout.EAST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(5, 30, 25, 30));

        main.add(header, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);

    }

    private void showPage(String page) {

        pageTitle.setText(page);
        contentPanel.removeAll();

        if (page.equals("Dashboard")) {

            if (dashboardPanel == null) {
                dashboardPanel = new DashboardPanel();
            }

            contentPanel.add(
                dashboardPanel,
                BorderLayout.CENTER
            );

        } else {

            JPanel placeholder = new JPanel(new GridBagLayout());
            placeholder.setBackground(BACKGROUND);

            JLabel message = new JLabel(page + " - Coming Soon");
            message.setForeground(MUTED);
            message.setFont(new Font("SansSerif", Font.PLAIN, 18));

            placeholder.add(message);

            contentPanel.add(
                placeholder,
                BorderLayout.CENTER
            );

        }

        contentPanel.revalidate();
        contentPanel.repaint();

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

            new Main().setVisible(true);

        });

    }

}
