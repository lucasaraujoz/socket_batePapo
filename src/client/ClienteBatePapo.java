package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteBatePapo {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Uso: java client.ClienteBatePapo <hostname>");
            return;
        }

        String hostname = args[0];
        int port = 5555;

        try (Socket socket = new Socket(hostname, port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Conectado ao servidor. Digite 'exit' para sair.");

            // Thread para ouvir mensagens do servidor
            Thread listenerThread = new Thread(() -> {
                String serverResponse;
                try {
                    while ((serverResponse = reader.readLine()) != null) {
                        System.out.println("Servidor: " + serverResponse);
                    }
                } catch (IOException e) {
                    System.err.println("Conexão com o servidor perdida.");
                }
            });
            listenerThread.start();

            // Thread para enviar mensagens ao servidor
            Thread senderThread = new Thread(() -> {
                String userInput;
                try {
                    while ((userInput = consoleReader.readLine()) != null) {
                        writer.println(userInput);
                        if (userInput.equalsIgnoreCase("exit")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Erro ao ler entrada do usuário.");
                }
            });
            senderThread.start();

            listenerThread.join();
            senderThread.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}