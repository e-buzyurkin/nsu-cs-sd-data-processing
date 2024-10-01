package ru.nsu.buzyurkin.task12;

import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    private static final LinkedList<String> list = new LinkedList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Thread thread = new Thread(new SortThread(list));
        thread.start();

        while (true) {
            System.out.println("Enter a line (empty to show the list)");
            String input = scanner.nextLine();
            synchronized (list) {
                if (input.isEmpty()) {
                    list.forEach(System.out::println);
                } else {
                    list.addFirst(input);
                }
            }
        }
    }
}
