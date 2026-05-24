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

            Philosopher philosopher =
                    new Philosopher(i, left, right, this);

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
            JOptionPane.showMessageDialog(this,
                    "אפשר עד 7 פילוסופים בלבד");
            return;
        }

        for (Philosopher p : philosophers) {
            p.stopPhilosopher();
        }

        createInitialPhilosophers(philosophers.size() + 1);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int radius = 200;

        // שולחן
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(centerX - 150, centerY - 150, 300, 300);

        int count = philosophers.size();

        for (int i = 0; i < count; i++) {

            double angle = 2 * Math.PI * i / count;

            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));

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

            // פילוסוף
            g2d.fillOval(x - 30, y - 30, 60, 60);

            // טקסט
            g2d.setColor(Color.BLACK);

            g2d.drawString("P" + i, x - 10, y - 40);

            g2d.drawString(
                    p.getStateEnum().toString(),
                    x - 40,
                    y + 50
            );

            g2d.drawString(
                    "Eat Count: " + p.getEatCount(),
                    x - 40,
                    y + 70
            );
        }

        // ציור מזלגות
        for (int i = 0; i < count; i++) {

            double angle = 2 * Math.PI * i / count;

            int x = centerX +
                    (int) ((radius - 70) * Math.cos(angle));

            int y = centerY +
                    (int) ((radius - 70) * Math.sin(angle));

            Fork fork = forks.get(i);

            if (fork.isTaken()) {
                g2d.setColor(Color.GREEN);
            } else {
                g2d.setColor(Color.BLACK);
            }

            g2d.fillRect(x - 5, y - 20, 10, 40);
        }
    }
}