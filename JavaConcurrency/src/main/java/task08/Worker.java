package task08;

public class Worker extends Thread {
    int from;
    int to;
    int i;
    double result = 0.0;

    public Worker(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getIteration() {
        return i;
    }

    public double getResult() {
        return result;
    }

    @Override
    public void run() {
        for (i = from; i < to; i++) {
            result += Math.pow(-1, i) / (2 * i + 1);
        }

        result *= 4.0;
    }
}