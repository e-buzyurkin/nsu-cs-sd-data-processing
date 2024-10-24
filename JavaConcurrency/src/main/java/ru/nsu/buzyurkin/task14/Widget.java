package ru.nsu.buzyurkin.task14;

import java.util.concurrent.Semaphore;


public class Widget extends Component {

    private final Semaphore moduleSemaphore;
    private final Semaphore partCSemaphore;


    public Widget(String widgetName, Semaphore widgetSemaphore, Semaphore moduleSemaphore, Semaphore partCSemaphore) {
        super(widgetName, widgetSemaphore, 0);
        this.moduleSemaphore = moduleSemaphore;
        this.partCSemaphore = partCSemaphore;
    }

    @Override
    protected void produceComponent() throws InterruptedException {
        moduleSemaphore.acquire();
        partCSemaphore.acquire();
        System.out.println(componentName + " №" + counter.incrementAndGet() + " сделан");
    }
}