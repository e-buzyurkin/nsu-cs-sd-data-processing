package ru.nsu.buzyurkin.task12;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final int PART_LENGTH = 50;
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
                    for (int i = 0; i < input.length(); i += PART_LENGTH) {
                        String sub = input.substring(i, Math.min(i + PART_LENGTH, input.length()));

                        list.addFirst(sub);
                    }
                }
            }
        }
    }
}
