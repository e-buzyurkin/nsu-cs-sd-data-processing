package ru.nsu.buzyurkin.task14;

import java.util.concurrent.Semaphore;


public class Part extends Component {

    public Part(String partName, Semaphore semaphore, long productionTime) {
        super(partName, semaphore, productionTime);
    }

    @Override
    void produceComponent() throws InterruptedException {
        Thread.sleep(delayTime);
        System.out.println(componentName + " №" + counter.incrementAndGet() + " сделана");
    }
}