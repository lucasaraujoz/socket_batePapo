package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            // Thread para ouvir mensagens do cliente
            Thread listenerThread = new Thread(() -> {
                String clientMessage;
                try {
                    while ((clientMessage = reader.readLine()) != null) {
                        System.out.println("Cliente: " + clientMessage);
                    }
                } catch (IOException e) {
                    System.err.println("Erro ao ler mensagem do cliente.");
                }
            });
            listenerThread.start();

            // Thread para enviar mensagens ao cliente
            Thread senderThread = new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = consoleReader.readLine()) != null) {
                        writer.println(serverMessage);
                        if (serverMessage.equalsIgnoreCase("exit")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Erro ao ler entrada do servidor.");
                }
            });
            senderThread.start();

            listenerThread.join();
            senderThread.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro de comunicação com o cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar o socket do cliente: " + e.getMessage());
            }
        }
    }
}