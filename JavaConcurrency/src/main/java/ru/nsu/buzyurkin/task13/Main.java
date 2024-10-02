package ru.nsu.buzyurkin.task13;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final int NUMBER_OF_PHILOSOPHERS = 5;

    public static void main(String[] args) {
        ReentrantLock forksMutex = new ReentrantLock();
        Condition condition = forksMutex.newCondition();
        Philosopher[] philosophers = new Philosopher[NUMBER_OF_PHILOSOPHERS];
        ReentrantLock[] forks = new ReentrantLock[philosophers.length];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new ReentrantLock();
        }

        for (int i = 0; i < philosophers.length; i++) {
            ReentrantLock leftFork = forks[i];
            ReentrantLock rightFork = forks[(i + 1) % forks.length];

            // deadlock resolve
            if (i == philosophers.length - 1) {
                // last philosopher takes right fork first
                philosophers[i] = new Philosopher(rightFork, leftFork, "Philosopher " + (i + 1), condition, forksMutex);
            } else {
                philosophers[i] = new Philosopher(leftFork, rightFork, "Philosopher " + (i + 1), condition, forksMutex);
            }
            philosophers[i].start();
        }
    }
}