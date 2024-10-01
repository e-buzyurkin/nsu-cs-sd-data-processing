package ru.nsu.buzyurkin.task06;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker implements Runnable {
    private final Department department;

    private final CyclicBarrier barrier;


    public Worker(Department department, CyclicBarrier barrier) {
        this.department = department;
        this.barrier = barrier;
    }


    @Override
    public void run() {
        department.performCalculations();
        try {
            barrier.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted: " + e.getMessage());
        } catch (BrokenBarrierException e) {
            System.out.println("Barrier was broken: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
