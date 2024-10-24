package ru.nsu.buzyurkin.task14;

import java.util.concurrent.Semaphore;


public class Module extends Component {
    private final Semaphore partASemaphore;
    private final Semaphore partBSemaphore;


    public Module(String moduleName,
                  Semaphore moduleSemaphore,
                  Semaphore partASemaphore,
                  Semaphore partBSemaphore,
                  long delayTime)
    {
        super(moduleName, moduleSemaphore, delayTime);
        this.partASemaphore = partASemaphore;
        this.partBSemaphore = partBSemaphore;
    }

    @Override
    void produceComponent() throws InterruptedException {
        partASemaphore.acquire();
        partBSemaphore.acquire();

        System.out.println(componentName + " №" + counter.incrementAndGet() + " сделан");
    }
}