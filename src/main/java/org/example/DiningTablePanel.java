package org.example;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DiningTablePanel extends JPanel {

    private final ArrayList<Philosopher> philosophers = new ArrayList<>();
    private final ArrayList<Fork> forks = new ArrayList<>();

    public DiningTablePanel() {
        setBackground(Color.WHITE);
        createInitialPhilosophers(5);
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
        }

        for (Philosopher p : philosophers) {
            p.start();
        }
    }

    public void stopPhilosopher(int index) {
        if (index >= 0 && index < philosophers.size()) {
            philosophers.get(index).stopPhilosopher();
        }
    }

    public void addPhilosopher() {
        if (philosophers.size() >= 7) {
            JOptionPane.showMessageDialog(this, "אפשר עד 7 פילוסופים בלבד");
            return;
        }
        for (Philosopher p : philosophers) {
            p.stopPhilosopher();
        }
        createInitialPhilosophers(philosophers.size() + 1);
        repaint();
    }

    public int getPhilosopherCount() {
        return philosophers.size();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = 200;

        // ציור השולחן
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(centerX - 150, centerY - 150, 300, 300);

        int count = philosophers.size();

        // מערכים לשמירת מיקומי הפילוסופים לצורך חישוב קירור המזלגות
        int[] philX = new int[count];
        int[] philY = new int[count];

        // ציור פילוסופים
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            philX[i] = centerX + (int) (radius * Math.cos(angle));
            philY[i] = centerY + (int) (radius * Math.sin(angle));

            Philosopher p = philosophers.get(i);

            switch (p.getStateEnum()) {
                case THINKING: g2d.setColor(Color.BLUE); break;
                case EATING: g2d.setColor(Color.RED); break;
                case WAITING_FIRST:
                case WAITING_SECOND: g2d.setColor(Color.ORANGE); break;
                case STOPPED: g2d.setColor(Color.GRAY); break;
            }

            g2d.fillOval(philX[i] - 30, philY[i] - 30, 60, 60);

            // טקסטים
            g2d.setColor(Color.BLACK);
            g2d.drawString("P" + i, philX[i] - 10, philY[i] - 40);
            g2d.drawString(p.getStateEnum().toString(), philX[i] - 40, philY[i] + 50);
            g2d.drawString("Eat Count: " + p.getEatCount(), philX[i] - 40, philY[i] + 70);
        }

        // ציור מזלגות עם מנגנון קירוב לפילוסוף האוחז
        for (int i = 0; i < count; i++) {
            Fork fork = forks.get(i);
            int targetX, targetY;

            if (fork.isTaken()) {
                g2d.setColor(Color.GREEN);
                // קירוב המזלג לפילוסוף שאוחז בו
                int ownerId = fork.getOwnerId();
                if (ownerId >= 0 && ownerId < count) {
                    // ממוצע בין המיקום המקורי של המזלג למיקום הפילוסוף
                    double forkAngle = 2 * Math.PI * i / count;
                    int origX = centerX + (int) ((radius - 60) * Math.cos(forkAngle));
                    int origY = centerY + (int) ((radius - 60) * Math.sin(forkAngle));

                    targetX = (origX + philX[ownerId]) / 2;
                    targetY = (origY + philY[ownerId]) / 2;
                } else {
                    double angle = 2 * Math.PI * i / count;
                    targetX = centerX + (int) ((radius - 60) * Math.cos(angle));
                    targetY = centerY + (int) ((radius - 60) * Math.sin(angle));
                }
            } else {
                g2d.setColor(Color.BLACK);
                double angle = 2 * Math.PI * i / count;
                targetX = centerX + (int) ((radius - 60) * Math.cos(angle));
                targetY = centerY + (int) ((radius - 60) * Math.sin(angle));
            }

            g2d.fillRect(targetX - 5, targetY - 15, 10, 30);
        }
    }
}