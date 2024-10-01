package ru.nsu.buzyurkin.task04;

public class Task4 {
    public static void main(String[] args) {
        Runnable task = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("12345");
            }
        };

        Thread thread = new Thread(task);
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread.interrupt();
    }
}
