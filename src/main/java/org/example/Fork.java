package org.example;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Fork {

    private final ReentrantLock lock =
            new ReentrantLock(true);

    private int ownerId = -1;

    public boolean tryTake(int philosopherId)
            throws InterruptedException {

        if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {

            ownerId = philosopherId;

            return true;
        }

        return false;
    }

    public void release() {

        if (lock.isHeldByCurrentThread()) {

            ownerId = -1;

            lock.unlock();
        }
    }

    public boolean isTaken() {

        return lock.isLocked();
    }

    public int getOwnerId() {

        return ownerId;
    }
}