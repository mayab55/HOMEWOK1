package org.example;

public class Fork {

    private boolean taken = false;
    private int ownerId = -1;

    public synchronized boolean take(int philosopherId) {

        if (!taken) {
            taken = true;
            ownerId = philosopherId;
            return true;
        }

        return false;
    }

    public synchronized void release() {
        taken = false;
        ownerId = -1;
    }

    public synchronized boolean isTaken() {
        return taken;
    }

    public synchronized int getOwnerId() {
        return ownerId;
    }
}