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
    private final Random random = new Random();

    public Philosopher(int id, Fork leftFork, Fork rightFork, DiningTablePanel panel) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.panel = panel;
        this.state = PhilosopherState.THINKING;
    }

    public int getPhilosopherId() {
        return id;
    }

    @Override
    public void run() {
        // מניעת דדלוק מוחלטת באמצעות סדר קבוע (Resource Ordering):
        // פילוסוף זוגי ירים קודם שמאל, פילוסוף אי-זוגי ירים קודם ימין.
        Fork firstFork = (id % 2 == 0) ? leftFork : rightFork;
        Fork secondFork = (id % 2 == 0) ? rightFork : leftFork;

        try {
            while (running) {
                // 1. מצב חשיבה (זמן אקראי של עד 5 שניות)
                think();

                // 2. ניסיון להרמת מזלג ראשון (בהפרשים של 100 מילישניות)
                state = PhilosopherState.WAITING_FIRST;
                repaintPanel();

                while (running) {
                    if (firstFork.tryTake(id)) {
                        break;
                    }
                    Thread.sleep(100); // דרישה: הפרשי זמן של 100 מילישניות
                }

                if (!running) break;

                // 3. המתנה אקראית של עד שנייה אחת לפני המזלג השני
                state = PhilosopherState.WAITING_SECOND;
                repaintPanel();
                sleepRandom(1000);

                // 4. ניסיון להרמת מזלג שני (בהפרשים של 100 מילישניות)
                boolean gotSecondFork = false;
                while (running) {
                    if (secondFork.tryTake(id)) {
                        gotSecondFork = true;
                        break;
                    }
                    Thread.sleep(100);
                }

                // אם נקטענו או לא השגנו את המזלג השני, נשחרר את הראשון ונצא
                if (!running || !gotSecondFork) {
                    firstFork.release();
                    break;
                }

                // 5. מצב אכילה (זמן אקראי של עד שנייה אחת)
                eat();

                // שחרור המזלגות בסיום האכילה וחזרה חלילה
                secondFork.release();
                firstFork.release();
                repaintPanel();
            }

        } catch (InterruptedException e) {
            // החוט קיבל אינטראפט לעצירה
        } finally {
            // שחרור הגנתי קריטי בבלוק ה-finally
            firstFork.release();
            secondFork.release();
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
        eatCount++; // עדכון מונה אכילות
        repaintPanel();
        sleepRandom(1000);
    }

    private void sleepRandom(int max) {
        try {
            if (max > 0) {
                Thread.sleep(random.nextInt(max) + 1);
            }
        } catch (InterruptedException e) {
            running = false;
            Thread.currentThread().interrupt();
        }
    }

    private void repaintPanel() {
        SwingUtilities.invokeLater(panel::repaint);
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