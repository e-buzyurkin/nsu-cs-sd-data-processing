package ru.nsu.buzyurkin.task02;

public class Task2 {
    public static void main(String[] args) {
        Runnable task = () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("new thread says " + i);
            }
        };

        Thread thread = new Thread(task);

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("new thread finished");

        for (int i = 0; i < 10; i++) {
            System.out.println("old thread says " + i);
        }
    }
}
