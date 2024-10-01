package ru.nsu.buzyurkin.task07;

public class Main {
    private static final int ACCURACY = 10000;
    static public void main(String[] args) {
        int threadsNumber = 10;

        try {
            threadsNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e){
            System.err.println("Cannot parse a number");
            return;
        }

        if (threadsNumber < 1 || threadsNumber > 10000) {
            System.err.println("Incorrect number of threads");
            return;
        }

        int iterationNum = (threadsNumber * 2) * ACCURACY; //определяем число итераций
        int pivot = iterationNum / (threadsNumber * 2);
        Worker[] threads = new Worker[threadsNumber];

        for (int i = 0; i < threadsNumber; i++) {
            threads[i] = new Worker(pivot * i, pivot * (i + 1));
            threads[i].start();
        }

        double sum = 0.0;
        for (Worker thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sum += thread.getResult();
        }

        System.out.println("Result value: " + sum);
    }
}
