package lab_8.server.main;

import lab_8.server.service.Database;
import lab_8.server.service.ThreadInfo;
import lab_8.common.ticket.Ticket;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Главный класс приложения-сервера
 */
public class Server {

    private static ExecutorService executor_in = Executors.newFixedThreadPool(4),
            executor_here = Executors.newCachedThreadPool(),
            executor_out = Executors.newWorkStealingPool();
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        int port;
        String line;
        Scanner scanner = new Scanner(System.in);

        ZonedDateTime first_date = ZonedDateTime.now();
        try {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                port = 1444;
            }
            SocketAddress address = new InetSocketAddress(port);
            HashMap<String, SocketAddress> allAddresses = new HashMap<>();
            DatagramChannel channel = DatagramChannel.open();
            channel.bind(address);
            channel.configureBlocking(false);
            try {
                ArrayList<Ticket> tickets = Database.open();
                if (tickets == null)
                    throw new Exception();
                logger.info("Сервер запущен");
                final byte[] buffer_to_zero = new byte[65000];
                while (true) {
                    if (System.in.available() != 0) {
                        line = scanner.nextLine();
                        if (line.equals("save"))
                            Database.save(tickets);
                        if (line.equals("exit")) {
                            Database.save(tickets);
                            channel.close();
                            executor_in.shutdown();
                            executor_here.shutdown();
                            executor_out.shutdown();
                            if (executor_in.awaitTermination(1, TimeUnit.SECONDS))
                                logger.info("Пул входа успешно остановлен");
                            else {
                                logger.warning("Пул входа был закрыт силой");
                                executor_in.shutdownNow();
                            }
                            if (executor_here.awaitTermination(1, TimeUnit.SECONDS))
                                logger.info("Пул внутренний успешно остановлен");
                            else {
                                logger.warning("Пул внутренний был закрыт силой");
                                executor_here.shutdownNow();
                            }
                            if (executor_out.awaitTermination(1, TimeUnit.SECONDS))
                                logger.info("Пул выхода успешно остановлен");
                            else {
                                logger.warning("Пул выхода был закрыт силой");
                                executor_out.shutdownNow();
                            }
                            logger.info("Сервер успешно остановлен");
                            return;
                        }
                    }
                    ByteBuffer buffer = ByteBuffer.wrap(buffer_to_zero);
                    address = channel.receive(buffer);

                    if (address != null)
                        executor_in.execute(new ServerIn(new ThreadInfo(address, allAddresses, channel, tickets, first_date), buffer, executor_here, executor_out));
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Сервер упал", e);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Неполадки в подключении", e);
        }
    }
}