package org.example;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Fork {
    // Fair Lock למניעת starvation (הרעבה) של פילוסופים
    private final ReentrantLock lock = new ReentrantLock(true);
    private volatile int ownerId = -1;

    public boolean tryTake(int philosopherId) throws InterruptedException {
        // ניסיון תפיסה קצר של עד 100 מילישניות לפי דרישות המטלה
        boolean success = lock.tryLock(100, TimeUnit.MILLISECONDS);
        if (success) {
            ownerId = philosopherId;
        }
        return success;
    }

    public void release() {
        // משחררים רק אם החוט הנוכחי הוא זה שנועל את המזלג
        if (lock.isHeldByCurrentThread()) {
            ownerId = -1; // איפוס קריטי לצורך הציור הגרפי
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