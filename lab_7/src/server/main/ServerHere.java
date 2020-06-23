package lab_7.server.main;

import lab_7.common.AllCommands;
import lab_7.common.ServerCommand;
import lab_7.server.service.ThreadInfo;
import lab_7.server.service.UserConnection;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Класс потока обработки данных
 */
public class ServerHere implements Runnable {
    private ThreadInfo info;
    private ExecutorService executor_out;
    private ServerCommand command;

    ServerHere(ThreadInfo info,
               ExecutorService executor_out,
               ServerCommand command) {
        this.info = info;
        this.executor_out = executor_out;
        this.command = command;
    }

    @Override
    public void run() {
        ServerCommand answer = null;
        if (command.getCommand() == AllCommands.registration)
            answer = UserConnection.add(command);
        else if (command.getCommand() == AllCommands.check)
            answer = UserConnection.check(command);
        else if (Objects.requireNonNull(UserConnection.check(command)).getResult())
            answer = ServerAnswers.doCommand(command, info.tickets, info.first_date);
        executor_out.execute(new ServerOut(info, answer));
    }
}
