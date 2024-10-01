package ru.nsu.buzyurkin.task05;

public class Task_5 {

    public static void main(String[] args) {
        Runnable task = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("12345");
            }

            System.out.println("goodbye my friends");
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
