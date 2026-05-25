package org.example;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private final DiningTablePanel tablePanel;
    private final JComboBox<String> philosopherChooser;

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
        JButton stopButton = new JButton("Stop Selected Philosopher");
        philosopherChooser = new JComboBox<>();

        updateChooser();

        addButton.addActionListener(e -> {
            tablePanel.addPhilosopher();
            updateChooser();
        });

        stopButton.addActionListener(e -> {
            int selectedIndex = philosopherChooser.getSelectedIndex();
            if (selectedIndex >= 0) {
                tablePanel.stopPhilosopher(selectedIndex);
            }
        });

        controlPanel.add(addButton);
        controlPanel.add(new JLabel("Select P:"));
        controlPanel.add(philosopherChooser);
        controlPanel.add(stopButton);

        add(controlPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void updateChooser() {
        philosopherChooser.removeAllItems();
        int count = tablePanel.getPhilosopherCount();
        for (int i = 0; i < count; i++) {
            philosopherChooser.addItem("Philosopher " + i);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}