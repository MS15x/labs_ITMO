package lab_8.client;

import lab_8.common.AllCommands;
import lab_8.common.CommandConverter;
import lab_8.common.ServerCommand;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Client {//implements Runnable {
    private static DatagramSocket socket;

    /*private static ClientIdentification client_info = ClientIdentification.getInstance();

    public void run() {
        try {
            int port;
            String hostname;
            // try {
            //port = Integer.parseInt(args[1]);
            // hostname = args[0].trim();
            //} catch (Exception e) {
            port = 1444;
            hostname = "localhost";
            // }
            Scanner scanner = new Scanner(System.in);
            address = new InetSocketAddress(hostname, port);
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            ServerCommand answer = connect(new ServerCommand(AllCommands.check));
            if (!answer.getResult())
                throw new IOException();
            if (!client_info.check_user(scanner)) {
                socket.close();
                System.out.printf("%s%n", "Программа завершилась");
                return;
            }
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
            int port = 1444;
            String hostname = "localhost";
            SocketAddress address = new InetSocketAddress(hostname, port);
            socket = new DatagramSocket();
            //socket.setSoTimeout(5000);

            command.setUser(ClientMain.getUser().getUser());
            command.setPassword(ClientMain.getUser().getPassword());
            byte[] client_buffer = CommandConverter.toByteCommand(command);
            DatagramPacket client_packet = new DatagramPacket(client_buffer, client_buffer.length, address);
            socket.send(client_packet);

            ServerCommand answer = receive(false);
            if (answer == null)
                throw new IOException();
            else
                return answer;
        } catch (IOException e) {
            return new ServerCommand(AllCommands.service, false, String.format("%s%n", "Подключение к серверу отсутствует"));
        }
    }

    public static ServerCommand receive(boolean fast) {
        try {
            socket.setSoTimeout(fast?5:5000);
            byte[] server_buffer = new byte[65000];
            DatagramPacket server_packet = new DatagramPacket(server_buffer, server_buffer.length);
            socket.receive(server_packet);
            return CommandConverter.fromByteCommand(server_buffer);
        } catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }
}