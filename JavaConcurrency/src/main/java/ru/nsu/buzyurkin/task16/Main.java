package ru.nsu.buzyurkin.task16;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Main {

    public static void
    main(String[] args) {

        if (args.length != 1) {
            System.out.println("Error input");
            return;
        }

        try {
            URL url = (URI.create(args[0])).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
//            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream input = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);

                Thread thread = new Thread(() -> {
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            queue.put(line);
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int lineCount = 0;
                while (lineCount < 25 && !queue.isEmpty()) {
                    try {
                        System.out.println(queue.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lineCount++;
                    if (lineCount == 25) {
                        System.out.println("Press Enter to continue and 'q' for exit...");
                        if (System.in.read() == 'q') {
                            System.out.println("Exit");
                            System.exit(0);
                        }
                        lineCount = 0;
                    }
                }
                reader.close();

            } else {
                System.out.println(
                        "HTTP request failed with response code: "
                                + connection.getResponseCode());
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}