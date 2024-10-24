package ru.nsu.buzyurkin.task14;

import java.util.concurrent.Semaphore;


public class Main {

    public static void main(String[] args) {
        Semaphore partASemaphore = new Semaphore(0);
        Semaphore partBSemaphore = new Semaphore(0);
        Semaphore partCSemaphore = new Semaphore(0);
        Semaphore moduleSemaphore = new Semaphore(0);
        Semaphore widgetSemaphore = new Semaphore(0);

        Thread partAProducer = new Thread(new Part("Деталь 'A'", partASemaphore, 1000));
        Thread partBProducer = new Thread(new Part("Деталь 'B'", partBSemaphore, 2000));
        Thread partCProducer = new Thread(new Part("Деталь 'C'", partCSemaphore, 3000));
        Thread moduleProducer = new Thread(new Module("Модуль", moduleSemaphore, partASemaphore, partBSemaphore, 0));
        Thread widgetProducer = new Thread(new Widget("Винтик", widgetSemaphore, moduleSemaphore, partCSemaphore));

        partAProducer.start();
        partBProducer.start();
        partCProducer.start();
        moduleProducer.start();
        widgetProducer.start();
    }
}