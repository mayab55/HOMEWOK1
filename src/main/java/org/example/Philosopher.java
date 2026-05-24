package org.example;
import javax.swing.*;
import java.util.Random;

public class Philosopher extends Thread {

    private final int id;
    private final Fork leftFork;
    private final Fork rightFork;
    private PhilosopherState state;
    private int eatCount = 0;
    private boolean running = true;
    private final DiningTablePanel panel;
    private final Random random = new Random();

    public Philosopher(int id, Fork leftFork, Fork rightFork, DiningTablePanel panel) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.panel = panel;
        this.state = PhilosopherState.THINKING;
    }

    @Override
    public void run() {
        try {
            while (running) {
                think();

                Fork firstFork;
                Fork secondFork;

                // מניעת Deadlock באמצעות אסימטריה
                if (id % 2 == 0) {
                    firstFork = leftFork;
                    secondFork = rightFork;
                } else {
                    firstFork = rightFork;
                    secondFork = leftFork;
                }

                // ניסיון הרמת מזלג ראשון
                state = PhilosopherState.WAITING_FIRST;
                repaintPanel();
                firstFork.take(id);

                // המתנה אקראית של עד שנייה בין המזלגות לפי הדרישות
                sleepRandom(1000);

                // ניסיון הרמת מזלג שני
                state = PhilosopherState.WAITING_SECOND;
                repaintPanel();
                secondFork.take(id);

                // אכילה
                eat();

                // שחרור המזלגות
                firstFork.release();
                secondFork.release();

                repaintPanel();
            }
        } catch (InterruptedException e) {
            // יציאה מסודרת במקרה של אינטראפט
        }

        state = PhilosopherState.STOPPED;
        repaintPanel();
    }

    private void think() {
        state = PhilosopherState.THINKING;
        repaintPanel();
        sleepRandom(5000); // חשיבה של עד 5 שניות
    }

    private void eat() {
        state = PhilosopherState.EATING;
        eatCount++;
        repaintPanel();
        sleepRandom(1000); // אכילה של עד שנייה
    }

    private void sleepRandom(int max) {
        try {
            Thread.sleep(random.nextInt(max) + 1);
        } catch (InterruptedException e) {
            running = false;
        }
    }

    private void repaintPanel() {
        SwingUtilities.invokeLater(panel::repaint);
    }

    public void stopPhilosopher() {
        running = false;
        this.interrupt(); // מעיר אותו מה-wait/sleep כדי שיעצור מיד
    }

    public PhilosopherState getStateEnum() {
        return state;
    }

    public int getEatCount() {
        return eatCount;
    }
}