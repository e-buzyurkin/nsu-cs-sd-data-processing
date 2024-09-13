package task03;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Task3 {
    static Random rand = new Random();
    
    public static void main(String[] args) {
        List<String> strings = new CopyOnWriteArrayList<>();
        strings.add("3");
        Thread thread1 = new Thread(new Printer("alpha", strings));
        thread1.start();

        strings.add("5");
        Thread thread2 = new Thread(new Printer("beta", strings));
        thread2.start();

        strings.add("7");
        Thread thread3 = new Thread(new Printer("gamma", strings));
        thread3.start();

        strings.add("11");
        Thread thread4 = new Thread(new Printer("delta", strings));
        thread4.start();
    }

    private static class Printer implements Runnable {
        private String name;
        private List<String> list;

        public Printer(String name, List<String> list) {
            this.name = name;
            this.list = new ArrayList<>(list);
        }

        public void run() {
            for (var elem : list) {
                System.out.println(name + "-" + elem);
            }
        }
    }
}
