package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DiningTablePanel extends JPanel {

    private final ArrayList<Philosopher>
            philosophers = new ArrayList<>();

    private final ArrayList<Fork>
            forks = new ArrayList<>();

    public DiningTablePanel() {

        setBackground(Color.WHITE);

        createInitialPhilosophers(5);
    }

    private void createInitialPhilosophers(
            int count
    ) {

        forks.clear();

        philosophers.clear();

        for (int i = 0; i < count; i++) {

            forks.add(new Fork());
        }

        for (int i = 0; i < count; i++) {

            Fork left = forks.get(i);

            Fork right =
                    forks.get((i + 1) % count);

            Philosopher philosopher =
                    new Philosopher(
                            i,
                            left,
                            right,
                            this
                    );

            philosophers.add(philosopher);
        }

        for (Philosopher p : philosophers) {

            p.start();
        }
    }

    public void stopPhilosopher(int index) {

        if (
                index >= 0 &&
                        index < philosophers.size()
        ) {

            philosophers
                    .get(index)
                    .stopPhilosopher();
        }
    }

    public void addPhilosopher() {

        if (philosophers.size() >= 7) {

            JOptionPane.showMessageDialog(
                    this,
                    "Maximum 7 philosophers allowed"
            );

            return;
        }

        int newCount =
                philosophers.size() + 1;

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

        int centerX = getWidth() / 2;

        int centerY = getHeight() / 2;

        int radius = 200;

        g2d.setColor(Color.LIGHT_GRAY);

        g2d.fillOval(
                centerX - 150,
                centerY - 150,
                300,
                300
        );

        int count = philosophers.size();

        int[] philX = new int[count];

        int[] philY = new int[count];

        for (int i = 0; i < count; i++) {

            double angle =
                    2 * Math.PI * i / count;

            philX[i] =
                    centerX +
                            (int) (
                                    radius *
                                            Math.cos(angle)
                            );

            philY[i] =
                    centerY +
                            (int) (
                                    radius *
                                            Math.sin(angle)
                            );

            g2d.setColor(Color.WHITE);

            g2d.fillOval(
                    philX[i] - 40,
                    philY[i] - 40,
                    80,
                    80
            );

            g2d.setColor(Color.BLACK);

            g2d.drawOval(
                    philX[i] - 40,
                    philY[i] - 40,
                    80,
                    80
            );

            Philosopher p =
                    philosophers.get(i);

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

            g2d.fillOval(
                    philX[i] - 25,
                    philY[i] - 25,
                    50,
                    50
            );

            g2d.setColor(Color.BLACK);

            g2d.drawString(
                    "P" + i,
                    philX[i] - 10,
                    philY[i] - 45
            );

            g2d.drawString(
                    p.getStateEnum().toString(),
                    philX[i] - 45,
                    philY[i] + 55
            );

            g2d.drawString(
                    "Eat Count: " +
                            p.getEatCount(),
                    philX[i] - 45,
                    philY[i] + 75
            );
        }

        for (int i = 0; i < count; i++) {

            Fork fork = forks.get(i);

            int targetX;

            int targetY;

            double angle =
                    2 * Math.PI * i / count;

            int originalX =
                    centerX +
                            (int) (
                                    (radius - 60) *
                                            Math.cos(angle)
                            );

            int originalY =
                    centerY +
                            (int) (
                                    (radius - 60) *
                                            Math.sin(angle)
                            );

            if (fork.isTaken()) {

                g2d.setColor(Color.GREEN);

                int ownerId =
                        fork.getOwnerId();

                if (
                        ownerId >= 0 &&
                                ownerId < count
                ) {

                    targetX =
                            (
                                    originalX +
                                            philX[ownerId]
                            ) / 2;

                    targetY =
                            (
                                    originalY +
                                            philY[ownerId]
                            ) / 2;

                } else {

                    targetX = originalX;

                    targetY = originalY;
                }

            } else {

                g2d.setColor(Color.BLACK);

                targetX = originalX;

                targetY = originalY;
            }

            g2d.fillRect(
                    targetX - 5,
                    targetY - 15,
                    10,
                    30
            );
        }
    }
}