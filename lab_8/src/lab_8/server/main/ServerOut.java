package lab_8.server.main;

import lab_8.common.CommandConverter;
import lab_8.common.ServerCommand;
import lab_8.server.service.ThreadInfo;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Класс потока для отправки данных пользователю
 */
public class ServerOut implements Runnable {
    private ServerCommand answer;
    private DatagramChannel channel;
    private SocketAddress address;

    ServerOut(DatagramChannel channel, SocketAddress address, ServerCommand answer) {
        this.channel = channel;
        this.address = address;
        this.answer = answer;
    }

    @Override
    public void run() {
        try {
            ByteBuffer a_buffer = ByteBuffer.wrap(CommandConverter.toByteCommand(answer));
            channel.send(a_buffer, address);
        } catch (IOException ignored) {
        }
    }
}
