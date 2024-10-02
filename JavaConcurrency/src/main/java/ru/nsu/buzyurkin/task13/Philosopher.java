package ru.nsu.buzyurkin.task13;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Philosopher extends Thread {
    private final ReentrantLock leftFork;
    private final ReentrantLock rightFork;
    private final String name;
    private final Condition condition;
    private final ReentrantLock forksMutex;
    private final Random random = new Random();

    public Philosopher(ReentrantLock leftFork, ReentrantLock rightFork, String name,
                       Condition condition, ReentrantLock forksMutex) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.name = name;
        this.condition = condition;
        this.forksMutex = forksMutex;
    }

    private void doAction(String action) throws InterruptedException {
        System.out.println(name + ": " + action);
        Thread.sleep(2000 + random.nextInt() % 50);
    }

    @Override
    public void run() {
        try {
            while (true) {
                doAction("Thinking");

                forksMutex.lock();
                while (!leftFork.tryLock() || !rightFork.tryLock()) {
                    if (leftFork.isHeldByCurrentThread()) {
                        leftFork.unlock();
                    }
                    if (rightFork.isHeldByCurrentThread()) {
                        rightFork.unlock();
                    }
                    condition.await();
                }
                forksMutex.unlock();
                doAction("Picked up forks - eating");
                forksMutex.lock();
                leftFork.unlock();
                rightFork.unlock();
                doAction("Hands up. Back to thinking");
                condition.signalAll();
                forksMutex.unlock();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}