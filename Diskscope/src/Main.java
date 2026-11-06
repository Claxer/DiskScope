package com.diskscope;

import com.diskscope.ui.Theme;
import com.diskscope.ui.WorkspacePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Main extends JFrame {

    public Main() {

        setTitle("DiskScope - Storage Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1320, 850);
        setMinimumSize(new Dimension(1050, 680));

        setLocationRelativeTo(null);

        getContentPane().setBackground(Theme.BACKGROUND);

        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(Theme.BACKGROUND);

        header.setBorder(
                new EmptyBorder(18, 24, 14, 24)
        );

        JPanel brand = new JPanel();

        brand.setBackground(Theme.BACKGROUND);

        brand.setLayout(
                new BoxLayout(brand, BoxLayout.Y_AXIS)
        );

        JLabel name = new JLabel("DiskScope");

        name.setFont(
                new Font("Segoe UI", Font.BOLD, 25)
        );

        name.setForeground(Theme.TEXT);

        JLabel subtitle = new JLabel(
                "STORAGE ANALYZER  •  VERSION 2.1"
        );

        subtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 10)
        );

        subtitle.setForeground(Theme.MUTED);

        brand.add(name);
        brand.add(Box.createVerticalStrut(3));
        brand.add(subtitle);

        JLabel status = new JLabel(
                "● LOCAL STORAGE WORKSPACE"
        );

        status.setFont(
                new Font("Segoe UI", Font.BOLD, 10)
        );

        status.setForeground(Theme.GREEN);

        header.add(brand, BorderLayout.WEST);
        header.add(status, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        add(new WorkspacePanel(), BorderLayout.CENTER);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );

            } catch (Exception e) {

                System.err.println(
                        "Using default look and feel: "
                                + e.getMessage()
                );
            }

            new Main().setVisible(true);
        });
    }
}
