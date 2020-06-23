package lab_7.server.main;

import lab_7.common.CommandConverter;
import lab_7.common.ServerCommand;
import lab_7.server.service.ThreadInfo;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

/**
 * Класс потока для получения данных от пользователя
 */
public class ServerIn implements Runnable {
    private ThreadInfo info;
    private ByteBuffer buffer;
    private ExecutorService executor_here;
    private ExecutorService executor_out;

    ServerIn(ThreadInfo info,
             ByteBuffer buffer,
             ExecutorService executor_here,
             ExecutorService executor_out) {
        this.info = info;
        this.buffer = buffer;
        this.executor_here = executor_here;
        this.executor_out = executor_out;
    }

    @Override
    public void run() {
        try {
            ServerCommand command = CommandConverter.fromByteCommand(buffer.array());
            executor_here.execute(new ServerHere(info, executor_out, command));
        } catch (Exception ignored) {
        }
    }
}
