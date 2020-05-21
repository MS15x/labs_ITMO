package lab_6.client;

import lab_6.common.*;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Главный класс приложения-клиента
 */
public class Client {
    private static SocketAddress address;
    private static DatagramSocket socket;

    public static void main(String[] args) {
        try {
            int port = 6661;
            String hostname = "localhost";
            Scanner scanner = new Scanner(System.in);
            address = new InetSocketAddress(hostname, port);
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            ClientAnswers.doCommand("help".split(" "));
            while (true) {
                String[] commands = scanner.nextLine().trim().replaceAll(" {2,}", " ").toLowerCase().split(" ");//квантификатор
                if (commands[0].equals("exit")) {
                    socket.close();
                    System.out.printf("%s%n", "Программа завершилась");
                    return;
                } else
                    ClientAnswers.doCommand(commands);
            }
        } catch (IOException e) {
            System.out.println("Произошли неполадки в подключении к серверу");
        }
    }

    public static ServerCommand connect(ServerCommand command) {
        try {
            byte[] client_buffer = CommandConverter.toByteCommand(command);
            DatagramPacket client_packet = new DatagramPacket(client_buffer, client_buffer.length, address);
            socket.send(client_packet);

            byte[] server_buffer = new byte[65000];
            DatagramPacket server_packet = new DatagramPacket(server_buffer, server_buffer.length);
            socket.receive(server_packet);

            return CommandConverter.fromByteCommand(server_buffer);
        } catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }
}