package task03;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Task3 {
    static Random rand = new Random();
    
    public static void main(String[] args) {
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            tasks.add(new Printer(List.of(i + "-bobik", i + "-murzik", i + "-sharik")));
        }

        for (var task : tasks) {
            Thread thread = new Thread(task);
            thread.start();
        }
    }

    private static class Printer implements Runnable {
        private List<String> list;

        public Printer(List<String> list) {
            this.list = list;
        }

        public void run() {
            list.forEach(System.out::println);
        }
    }
}
