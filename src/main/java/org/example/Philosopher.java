package org.example;

import javax.swing.*;
import java.util.Random;

public class Philosopher extends Thread {

    private final int id;

    private final Fork leftFork;
    private final Fork rightFork;

    private volatile PhilosopherState state;

    private int eatCount = 0;

    private volatile boolean running = true;

    private final DiningTablePanel panel;

    private final Random random =
            new Random();

    public Philosopher(
            int id,
            Fork leftFork,
            Fork rightFork,
            DiningTablePanel panel
    ) {

        this.id = id;

        this.leftFork = leftFork;

        this.rightFork = rightFork;

        this.panel = panel;

        state = PhilosopherState.THINKING;
    }

    @Override
    public void run() {

        try {

            while (running) {

                think();

                Fork firstFork =
                        (id % 2 == 0)
                                ? leftFork
                                : rightFork;

                Fork secondFork =
                        (id % 2 == 0)
                                ? rightFork
                                : leftFork;

                state =
                        PhilosopherState.WAITING_FIRST;

                repaintPanel();

                while (
                        running &&
                                !firstFork.tryTake(id)
                ) {
                }

                if (!running) {

                    firstFork.release();

                    break;
                }

                sleepRandom(1000);

                state =
                        PhilosopherState.WAITING_SECOND;

                repaintPanel();

                boolean gotSecondFork = false;

                while (running) {

                    if (secondFork.tryTake(id)) {

                        gotSecondFork = true;

                        break;
                    }
                }

                if (!running) {

                    firstFork.release();

                    break;
                }

                if (!gotSecondFork) {

                    firstFork.release();

                    continue;
                }

                eat();

                secondFork.release();

                firstFork.release();

                repaintPanel();
            }

        } catch (InterruptedException e) {

            running = false;

        } finally {

            leftFork.release();

            rightFork.release();

            state = PhilosopherState.STOPPED;

            repaintPanel();
        }
    }

    private void think() {

        state = PhilosopherState.THINKING;

        repaintPanel();

        sleepRandom(5000);
    }

    private void eat() {

        state = PhilosopherState.EATING;

        eatCount++;

        repaintPanel();

        sleepRandom(1000);
    }

    private void sleepRandom(int max) {

        try {

            Thread.sleep(
                    random.nextInt(max) + 1
            );

        } catch (InterruptedException e) {

            running = false;
        }
    }

    private void repaintPanel() {

        SwingUtilities.invokeLater(
                panel::repaint
        );
    }

    public void stopPhilosopher() {

        running = false;

        interrupt();
    }

    public PhilosopherState getStateEnum() {

        return state;
    }

    public int getEatCount() {

        return eatCount;
    }
}