package ru.nsu.buzyurkin.task08;

import sun.misc.Signal;


public class Main {
    private static final long ACCURACY = 100000000;
    private static Worker[] threads;

    static public void main(String[] args) {
        int threadsNumber = 10;
        long pivot = ACCURACY;


        Signal.handle(new Signal("INT"), sig -> {
            System.out.println("Caught signal: " + sig);

            double res = 0.0;

            for (var thread : threads) {
                res += thread.getResult();
                thread.interrupt();
            }

            System.out.println("Result value (after interruption): " + res * 4.0);
            System.exit(0);
        });

        try {
            threadsNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e){
            System.err.println("Cannot parse a number");
            return;
        }

        System.out.println("Number of threads: " + threadsNumber);

        if (threadsNumber < 1 || threadsNumber > 10000) {
            System.err.println("Incorrect number of threads");
            return;
        }

        System.out.println("Press ctrl+c to interrupt");

        threads = new Worker[threadsNumber];

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

        System.out.println("Result value: " + sum * 4.0);
    }
}