package lab_8.server.main;

import lab_8.common.AllCommands;
import lab_8.common.ServerCommand;
import lab_8.server.service.ThreadInfo;
import lab_8.server.service.UserConnection;

import java.net.SocketAddress;
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
        ServerCommand answer = new ServerCommand(AllCommands.service, false);
        if (command.getCommand() == AllCommands.registration)
            answer = UserConnection.add(command);
        else if (command.getCommand() == AllCommands.check)
            answer = UserConnection.check(command);
        else if (UserConnection.check(command).getResult())
            answer = ServerAnswers.doCommand(command, info.tickets, info.first_date);

        executor_out.execute(new ServerOut(info.channel, info.address, answer));
        if (answer.getResult() & answer.getCommand() != AllCommands.show & answer.getCommand() != AllCommands.registration & answer.getCommand() != AllCommands.check)
            for (SocketAddress address : info.allAddresses.values())
                if (address != info.address)
                    executor_out.execute(new ServerOut(info.channel, address, answer.toAll()));
    }
}