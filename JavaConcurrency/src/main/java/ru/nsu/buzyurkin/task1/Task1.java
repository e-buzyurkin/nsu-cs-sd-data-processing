package ru.nsu.buzyurkin.task1;

public class Task1 {

    public static void main(String[] args) {
        Runnable task = () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("new thread says " + i);
            }
        };

        Thread thread = new Thread(task);

        thread.start();

        for (int i = 0; i < 10; i++) {
            System.out.println("old thread says " + i);
        }
    }
}
