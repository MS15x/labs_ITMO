package lab_8.client;

import lab_8.common.AllCommands;
import lab_8.common.CommandConverter;
import lab_8.common.ServerCommand;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Реализует подключение к серверу
 */
public abstract class ClientConnection {
    private static DatagramSocket socket;

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
            return new ServerCommand(AllCommands.service, false, "Ans-101");
        }
    }

    /**
     * Ожидает ответа от сервера
     *
     * @param fast время ожидания твета true-5 false-5000 миллисикунд
     * @return ответ от сервера
     */
    public static ServerCommand receive(boolean fast) {
        try {
            socket.setSoTimeout(fast ? 5 : 5000);
            byte[] server_buffer = new byte[65000];
            DatagramPacket server_packet = new DatagramPacket(server_buffer, server_buffer.length);
            socket.receive(server_packet);
            return CommandConverter.fromByteCommand(server_buffer);
        } catch (ClassNotFoundException | IOException e) {
            return null;
        }
    }
}