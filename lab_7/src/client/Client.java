package lab_7.client;

import lab_7.common.*;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Главный класс приложения-клиента
 */
public class Client {
    private static SocketAddress address;
    private static DatagramSocket socket;
    private static ClientIdentification client_info = new ClientIdentification();

    public static void main(String[] args) {
        try {
            int port;
            String hostname;
            try {
                port = Integer.parseInt(args[1]);
                hostname = args[0].trim();
            } catch (Exception e) {
                port = 1444;
                hostname = "localhost";
            }
            Scanner scanner = new Scanner(System.in);
            address = new InetSocketAddress(hostname, port);
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            ServerCommand answer = connect(new ServerCommand(AllCommands.check));
            if (answer == null)
                throw new IOException();
            client_info.check_user(scanner);
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

    /**
     * Отправляет команды на сервер и получает ответ
     *
     * @param command команда
     * @return ответ сервера
     */
    public static ServerCommand connect(ServerCommand command) {
        try {
            command.setUser(client_info.getUser());
            command.setPassword(client_info.getPassword());
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