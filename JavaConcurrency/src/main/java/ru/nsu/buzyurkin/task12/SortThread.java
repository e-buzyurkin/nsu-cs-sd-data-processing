package ru.nsu.buzyurkin.task12;

import java.util.LinkedList;

public class SortThread extends Thread {
    private final LinkedList<String> list;

    public SortThread(LinkedList<String> list) {
        this.list = list;
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (list) {
                    bubbleSort();
                }
                Thread.sleep(5000);
            }
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    private synchronized void bubbleSort() {
        String temp;
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (list.get(i).compareTo(list.get(j)) < 0) {
                    temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }
}
