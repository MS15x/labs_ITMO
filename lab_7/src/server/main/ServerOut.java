package lab_7.server.main;

import lab_7.common.CommandConverter;
import lab_7.common.ServerCommand;
import lab_7.server.service.ThreadInfo;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Класс потока для отправки данных пользователю
 */
public class ServerOut implements Runnable {
    private ThreadInfo info;
    private ServerCommand answer;

    ServerOut(ThreadInfo info, ServerCommand answer) {
        this.info = info;
        this.answer = answer;
    }

    @Override
    public void run() {
        try {
            ByteBuffer a_buffer = ByteBuffer.wrap(CommandConverter.toByteCommand(answer));
            info.channel.send(a_buffer, info.address);
        } catch (IOException ignored) {
        }
    }
}
