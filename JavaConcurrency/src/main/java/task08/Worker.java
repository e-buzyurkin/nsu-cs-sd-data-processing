package task08;

public class Worker extends Thread {
    long from;
    long to;
    double result = 0.0;

    public Worker(long from, long to) {
        this.from = from;
        this.to = to;
    }

    public double getResult() {
        return result;
    }

    @Override
    public void run() {
        for (long i = from; i < to; i++) {
            result += Math.pow(-1, i) / (2 * i + 1);
        }

    }
}