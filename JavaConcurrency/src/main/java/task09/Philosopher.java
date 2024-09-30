package task09;

import java.util.Random;

public class Philosopher extends Thread {
    private final Object leftFork;
    private final Object rightFork;
    private final String name;
    private final Random random = new Random();

    public Philosopher(Object leftFork, Object rightFork, String name) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.name = name;
    }

    private void doAction(String action) throws InterruptedException {
        System.out.println(name + ": " + action);
        Thread.sleep(2000 + random.nextInt() % 50);
    }

    @Override
    public void run() {
        try {
            while (true) {
                // thinking
                doAction("Thinking");

                synchronized (leftFork) {
                    doAction("Picked up left fork");

                    synchronized (rightFork) {
                        // eating
                        doAction("Picked up right fork - eating");
                        doAction("Put down right fork");
                    }

                    // Back to thinking
                    doAction("Put down left fork. Back to thinking");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
