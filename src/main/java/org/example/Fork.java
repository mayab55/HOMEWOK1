package org.example;

public class Fork {

    private boolean taken = false;
    private int ownerId = -1;

    // שימוש ב-wait מונע Busy Waiting ומבטיח סנכרון הוגן ויעיל
    public synchronized void take(int philosopherId) throws InterruptedException {
        while (taken) {
            wait();
        }
        taken = true;
        ownerId = philosopherId;
    }

    public synchronized void release() {
        taken = false;
        ownerId = -1;
        notifyAll(); // מעיר את הפילוסופים הממתינים למזלג זה
    }

    public synchronized boolean isTaken() {
        return taken;
    }

    public synchronized int getOwnerId() {
        return ownerId;
    }
}