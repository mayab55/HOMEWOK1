package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DiningTablePanel extends JPanel {

    private final ArrayList<Philosopher> philosophers = new ArrayList<>();
    private final ArrayList<Fork> forks = new ArrayList<>();
    private final ArrayList<Integer> stoppedPhilosopherIds = new ArrayList<>();

    public DiningTablePanel() {
        setBackground(Color.WHITE);
        createInitialPhilosophers(5); // 5 פילוסופים כברירת מחדל
    }

    private void createInitialPhilosophers(int count) {
        forks.clear();
        philosophers.clear();

        for (int i = 0; i < count; i++) {
            forks.add(new Fork());
        }

        for (int i = 0; i < count; i++) {
            Fork left = forks.get(i);
            Fork right = forks.get((i + 1) % count);

            Philosopher philosopher = new Philosopher(i, left, right, this);
            philosophers.add(philosopher);

            if (stoppedPhilosopherIds.contains(i)) {
                philosopher.stopPhilosopher();
            }
        }

        for (Philosopher p : philosophers) {
            if (!stoppedPhilosopherIds.contains(p.getPhilosopherId())) {
                p.start();
            }
        }
    }

    public void stopPhilosopher(int index) {
        if (index >= 0 && index < philosophers.size()) {
            philosophers.get(index).stopPhilosopher();
            if (!stoppedPhilosopherIds.contains(index)) {
                stoppedPhilosopherIds.add(index);
            }
        }
    }

    public void addPhilosopher() {
        if (philosophers.size() >= 7) { // חסימה עד 7 (מקסימום 2 תוספות)
            JOptionPane.showMessageDialog(this, "Maximum 7 philosophers allowed");
            return;
        }

        int newCount = philosophers.size() + 1;

        // עצירה מסודרת של כולם לפני שינוי המבנה
        for (Philosopher p : philosophers) {
            p.stopPhilosopher();
        }

        for (Philosopher p : philosophers) {
            try {
                p.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        createInitialPhilosophers(newCount);
        repaint();
    }

    public int getPhilosopherCount() {
        return philosophers.size();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = 200;

        // ציור השולחן העגול
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(centerX - 150, centerY - 150, 300, 300);

        int count = philosophers.size();
        int[] philX = new int[count];
        int[] philY = new int[count];

        // 1. חישוב מיקומי פילוסופים וציורם
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;

            philX[i] = centerX + (int) (radius * Math.cos(angle));
            philY[i] = centerY + (int) (radius * Math.sin(angle));

            g2d.setColor(Color.WHITE);
            g2d.fillOval(philX[i] - 40, philY[i] - 40, 80, 80);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(philX[i] - 40, philY[i] - 40, 80, 80);

            Philosopher p = philosophers.get(i);
            switch (p.getStateEnum()) {
                case THINKING:
                    g2d.setColor(Color.BLUE);
                    break;
                case EATING:
                    g2d.setColor(Color.RED);
                    break;
                case WAITING_FIRST:
                case WAITING_SECOND:
                    g2d.setColor(Color.ORANGE);
                    break;
                case STOPPED:
                    g2d.setColor(Color.GRAY);
                    break;
            }

            g2d.fillOval(philX[i] - 25, philY[i] - 25, 50, 50);

            g2d.setColor(Color.BLACK);
            g2d.drawString("P" + i, philX[i] - 10, philY[i] - 45);
            g2d.drawString(p.getStateEnum().toString(), philX[i] - 45, philY[i] + 55);
            g2d.drawString("Eat Count: " + p.getEatCount(), philX[i] - 45, philY[i] + 75);
        }

        // 2. ציור המזלגות והזזתם הצידה/פנימה לכיוון הפילוסוף האוחז
        for (int i = 0; i < count; i++) {
            Fork fork = forks.get(i);

            // זווית המזלג המקורית (בדיוק במרכז בין פילוסוף i לפילוסוף i+1)
            double forkAngle = 2 * Math.PI * (i + 0.5) / count;
            int currentRadius = radius - 60; // רדיוס ברירת מחדל בתוך השולחן

            if (fork.isTaken()) {
                g2d.setColor(Color.GREEN);
                int owner = fork.getOwnerId();

                if (owner != -1) {
                    // חישוב הזווית הישירה של הפילוסוף שאוחז בו
                    double philAngle = 2 * Math.PI * owner / count;

                    // הטיית הזווית של המזלג לכיוון הפילוסוף (ממוצע משוקלל)
                    forkAngle = (forkAngle * 0.4) + (philAngle * 0.6);
                    currentRadius = radius - 30; // קירוב המזלג לפילוסוף עצמו
                }
            } else {
                g2d.setColor(Color.BLACK);
            }

            int forkX = centerX + (int) (currentRadius * Math.cos(forkAngle));
            int forkY = centerY + (int) (currentRadius * Math.sin(forkAngle));

            // ציור המזלג כצורה ברורה ודינמית
            g2d.fillRect(forkX - 5, forkY - 15, 10, 30);
        }
    }
}