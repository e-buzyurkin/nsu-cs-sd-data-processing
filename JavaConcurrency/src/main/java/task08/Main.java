package task08;



import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


public class Main {
    private static final int ACCURACY = 100000000;
    private static Worker[] threads;

    static public void oko(String[] args) {
        int threadsNumber = 10;
        int pivot = ACCURACY;


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                sleep(2500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<Integer> iterations = new ArrayList<>();
            List<Double> results = new ArrayList<>();

            for (var thread : threads) {
                iterations.add(thread.getIteration() % ACCURACY);
                results.add(thread.getResult());
                thread.interrupt();
            }

            int maxIteration = iterations.stream().max(Integer::compare).orElse(0);

            for (int i = 0; i < iterations.size(); i++) {
                Double temp = results.get(i);
                temp += calculateRest(iterations.get(i) + ACCURACY * i, maxIteration + ACCURACY * i, results.get(i));
                results.set(i, temp);
            }

            System.out.println("Result value (after interruption): " + results.stream()
                    .mapToDouble(Double::doubleValue)
                    .sum());
        }));

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

        System.out.println("Result value: " + sum);
    }

    private static double calculateRest(int from, int to, double result) {
        if (from == to) {
            return 0.0;
        }

        for (int i = from; i < to; i++) {
            result += Math.pow(-1, i) / (2 * i + 1);
        }

        return result * 4.0;
    }

    static public void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                sleep(2500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Shutdown hook executed!");
        }));

        System.out.println("Program is running... Press Ctrl+C to stop.");

        // Бесконечный цикл, чтобы программа не завершилась мгновенно
        while (true) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}