package org.example;
import javax.swing.*;
import java.util.Random;

public class Philosopher extends Thread {

    private final int id;

    private Fork leftFork;
    private Fork rightFork;

    private PhilosopherState state;

    private int eatCount = 0;

    private boolean running = true;

    private final DiningTablePanel panel;

    private final Random random = new Random();

    public Philosopher(int id,
                       Fork leftFork,
                       Fork rightFork,
                       DiningTablePanel panel) {

        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.panel = panel;

        state = PhilosopherState.THINKING;
    }

    @Override
    public void run() {

        while (running) {

            think();

            Fork firstFork;
            Fork secondFork;

            // מניעת Deadlock
            // הפילוסוף האחרון לוקח הפוך
            if (id % 2 == 0) {
                firstFork = leftFork;
                secondFork = rightFork;
            } else {
                firstFork = rightFork;
                secondFork = leftFork;
            }

            takeFirstFork(firstFork);

            sleepRandom(1000);

            takeSecondFork(secondFork);

            eat();

            firstFork.release();
            secondFork.release();

            repaintPanel();
        }

        state = PhilosopherState.STOPPED;
        repaintPanel();
    }

    private void think() {

        state = PhilosopherState.THINKING;
        repaintPanel();

        sleepRandom(5000);
    }

    private void takeFirstFork(Fork fork) {

        state = PhilosopherState.WAITING_FIRST;
        repaintPanel();

        while (running && !fork.take(id)) {
            sleepFixed(100);
        }

        repaintPanel();
    }

    private void takeSecondFork(Fork fork) {

        state = PhilosopherState.WAITING_SECOND;
        repaintPanel();

        while (running && !fork.take(id)) {
            sleepFixed(100);
        }

        repaintPanel();
    }

    private void eat() {

        state = PhilosopherState.EATING;
        eatCount++;

        repaintPanel();

        sleepRandom(1000);
    }

    private void sleepRandom(int max) {

        try {
            Thread.sleep(random.nextInt(max) + 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sleepFixed(int time) {

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void repaintPanel() {
        SwingUtilities.invokeLater(panel::repaint);
    }

    public void stopPhilosopher() {
        running = false;
    }

    public int getIdNumber() {
        return id;
    }

    public PhilosopherState getStateEnum() {
        return state;
    }

    public int getEatCount() {
        return eatCount;
    }
}