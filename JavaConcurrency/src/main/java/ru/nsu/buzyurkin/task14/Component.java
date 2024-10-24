package ru.nsu.buzyurkin.task14;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class Component implements Runnable {

    final String componentName;
    final Semaphore semaphore;
    final long delayTime;


    final AtomicInteger counter = new AtomicInteger(0);

    public Component(String componentName, Semaphore semaphore, long delayTime) {
        this.componentName = componentName;
        this.semaphore = semaphore;
        this.delayTime = delayTime;
    }

    @Override
    public void run() {
        try {
            while (true) {
                produceComponent();
                semaphore.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    abstract void produceComponent() throws InterruptedException;
}
