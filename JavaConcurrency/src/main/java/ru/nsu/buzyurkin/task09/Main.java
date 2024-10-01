package ru.nsu.buzyurkin.task09;

public class Main {
    private static final int NUMBER_OF_PHILOSOPHERS = 5;

    public static void main(String[] args) {
        Philosopher[] philosophers = new Philosopher[NUMBER_OF_PHILOSOPHERS];
        Object[] forks = new Object[philosophers.length];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < philosophers.length; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % forks.length];

            // deadlock resolve
            if (i == philosophers.length - 1) {
                // last philosopher takes right fork first
                philosophers[i] = new Philosopher(rightFork, leftFork, "Philosopher " + (i + 1));
            } else {
                philosophers[i] = new Philosopher(leftFork, rightFork, "Philosopher " + (i + 1));
            }
            philosophers[i].start();
        }
    }
}
