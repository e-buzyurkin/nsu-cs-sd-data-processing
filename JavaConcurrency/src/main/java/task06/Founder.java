package task06;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public final class Founder {
    private final List<Thread> workersThreads;

    private final Company company;

    private final CyclicBarrier barrier;

    public Founder(final Company company) {
        this.company = company;
        this.workersThreads = new ArrayList<>(company.getDepartmentsCount());
        // Когда все потоки достигнут барьера, выведем результат
        this.barrier = new CyclicBarrier(company.getDepartmentsCount(), company::showCollaborativeResult);
        initializeWorkers();
    }


    private void initializeWorkers() {
        for (int i = 0; i < company.getDepartmentsCount(); i++) {
            final Department department = company.getFreeDepartment(i);
            Worker worker = new Worker(department, barrier);
            workersThreads.add(new Thread(worker));
        }
    }


    public void start() {
        for (Thread workerThread : workersThreads) {
            workerThread.start();
        }
    }
}