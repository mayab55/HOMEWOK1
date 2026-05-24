package org.example;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private final DiningTablePanel tablePanel;

    public Main() {

        setTitle("Dining Philosophers Problem");

        setSize(1000, 800);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        tablePanel = new DiningTablePanel();

        add(tablePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();

        JButton addButton = new JButton("Add Philosopher");

        JButton stopButton = new JButton("Stop Philosopher 0");

        addButton.addActionListener(e -> {
            tablePanel.addPhilosopher();
        });

        stopButton.addActionListener(e -> {
            tablePanel.stopPhilosopher(0);
        });

        controlPanel.add(addButton);
        controlPanel.add(stopButton);

        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(Main::new);
    }
}