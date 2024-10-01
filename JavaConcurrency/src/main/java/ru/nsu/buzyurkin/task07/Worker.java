package ru.nsu.buzyurkin.task07;

public class Worker extends Thread {
    int from;
    int to;
    double result = 0.0;

    public Worker(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public double getResult() {
        return result;
    }

    @Override
    public void run() {
        for (int i = from; i < to; i++) {
            result += Math.pow(-1, i) / (2 * i + 1);
        }

        result *= 4.0;
    }
}
