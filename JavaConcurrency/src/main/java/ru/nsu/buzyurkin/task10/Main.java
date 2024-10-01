package ru.nsu.buzyurkin.task10;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        AtomicBoolean isParentTurn = new AtomicBoolean(false); // Используем флаг для очередности

        Runnable task = () -> {
            for (int i = 0; i < 10; i++) {
                lock.lock();
                try {
                    while (isParentTurn.get()) {
                        lock.unlock();
                        Thread.yield();
                        lock.lock();
                    }
                    System.out.println("new thread says " + i);
                    isParentTurn.set(true);
                } finally {
                    lock.unlock();
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();

        for (int i = 0; i < 10; i++) {
            lock.lock();
            try {
                while (!isParentTurn.get()) {
                    lock.unlock();
                    Thread.yield();
                    lock.lock();
                }
                System.out.println("old thread says " + i);
                isParentTurn.set(false);
            } finally {
                lock.unlock();
            }
        }
    }
}
