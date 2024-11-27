package ru.nsu.buzyurkin.task15;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: java AsyncTcpProxy <listening_port> <remote_host> <remote_port>");
            return;
        }

        int listeningPort = Integer.parseInt(args[0]);
        String remoteHost = args[1];
        int remotePort = Integer.parseInt(args[2]);

        // Create a server socket and bind to the listening port
        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {
            System.out.println("Server is listening on port " + listeningPort);

            while (true) {
                // Accept incoming connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from client: " + clientSocket.getRemoteSocketAddress());

                // Handle the connection asynchronously
                handleConnection(clientSocket, remoteHost, remotePort);
            }
        }
    }

    private static void handleConnection(Socket clientSocket, String remoteHost, int remotePort) {
        CompletableFuture.runAsync(() -> {
            Socket serverSocket = null;
            try {
                // Connect to the remote server
                serverSocket = new Socket(remoteHost, remotePort);
                System.out.println("Connected to remote server: " + remoteHost + ":" + remotePort);

                // Start bidirectional data transfer
                CompletableFuture<Void> clientToServer = transferData(clientSocket, serverSocket);
                CompletableFuture<Void> serverToClient = transferData(serverSocket, clientSocket);

                // Wait for either transfer to complete (indicating disconnection)
                CompletableFuture.anyOf(clientToServer, serverToClient).join();
            } catch (Exception e) {
                System.err.println("Error handling connection: " + e.getMessage());
            } finally {
                closeSocket(clientSocket);
                closeSocket(serverSocket);
            }
        });
    }

    private static CompletableFuture<Void> transferData(Socket source, Socket target) {
        return CompletableFuture.runAsync(() -> {
            try (InputStream input = source.getInputStream(); OutputStream output = target.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    output.flush();
                }
            } catch (Exception e) {
                System.err.println("Connection closed: " + e.getMessage());
            } finally {
                closeSocket(target);
            }
        });
    }

    private static void closeSocket(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                System.out.println("Closing connection: " + socket.getRemoteSocketAddress());
                socket.close();
            } catch (Exception e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}
