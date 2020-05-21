package lab_6.server;

import lab_6.common.*;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Главный класс приложения-сервера
 */
public class Server {

    public static void main(String[] args) {
        String line;
        Scanner scanner = new Scanner(System.in);

        ArrayList<Ticket> tickets = new ArrayList<>();
        ZonedDateTime first_date;
        try {
            int port = 6661;
            SocketAddress address = new InetSocketAddress(port);
            DatagramChannel channel = DatagramChannel.open();
            channel.bind(address);
            channel.configureBlocking(false);

            String envName = "LAB_SERVER_ENV_NAME";
            if (System.getenv(envName) == null)
                System.out.println("Переменная окружения не задана, для загрузки файла задайте переменную " + envName);
            else
                try {
                    String path = System.getenv(envName); //= "C:\\Users\\micha\\Documents\\ITMO\\programming\\lab_6\\src\\lab_6\\server\\info.csv";
                    Scanner file_scanner = new Scanner(new File(path));
                    first_date = ZonedDateTime.parse(file_scanner.nextLine());
                    while (file_scanner.hasNext()) {
                        String[] string = file_scanner.nextLine().trim().split(";");
                        Ticket ticket = new Ticket();
                        ticket.setId(Integer.parseInt(string[10]));
                        ticket.setName(string[0]);
                        ticket.getCoordinates().setX(Float.parseFloat(string[1]));
                        ticket.getCoordinates().setY(Long.parseLong(string[2]));
                        ticket.setCreationDate(LocalDate.parse(string[3]));
                        ticket.setPrice(Integer.parseInt(string[4]));
                        if (!string[5].isEmpty())
                            ticket.setType(TicketType.valueOf(string[5]));
                        if (!string[6].isEmpty()) {
                            ticket.setEvent(new Event());
                            ticket.getEvent().setId(Long.parseLong(string[6]));
                            ticket.getEvent().setName(string[7]);
                            ticket.getEvent().setDate(ZonedDateTime.parse(string[8]));
                        }
                        if (!string[9].isEmpty())
                            ticket.getEvent().setEventType(EventType.valueOf(string[9]));
                        tickets.add(ticket);
                    }
                    System.out.printf("%s%n", "Сервер был запущен");

                    while (true) {
                        if (System.in.available() != 0) {
                            line = scanner.nextLine();
                            if (line.equals("save"))
                                save_(tickets, first_date, path);
                            if (line.equals("exit")) {
                                save_(tickets, first_date, path);
                                channel.close();
                                System.out.printf("%s%n", "Работа сервера была остановлена");
                                return;
                            }
                        }
                        try {
                            //получение
                            ByteBuffer buffer = ByteBuffer.wrap(new byte[65000]);
                            address = channel.receive(buffer);

                            //обработка
                            ServerCommand command = CommandConverter.fromByteCommand(buffer.array());
                            ServerCommand answer = ServerAnswers.doCommand(command, tickets, first_date);

                            //отправка
                            ByteBuffer a_buffer = ByteBuffer.wrap(CommandConverter.toByteCommand(answer));
                            channel.send(a_buffer, address);
                        } catch (IOException ignored) {}
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Не удалось найти или открыть файл");
                } catch (Exception e) {
                    System.out.println("Данные в файле некорректны " + e);
                }
        } catch (IOException e) {
            System.out.println("Произошли неполадки в подключении");
        }
    }

    private static void save_(ArrayList<Ticket> tickets, ZonedDateTime data, String path) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(data + "\n");
            for (Ticket ticket : tickets) {
                writer.write(ticket.getName() + ";" +
                        ticket.getCoordinates().getX() + ";" +
                        ticket.getCoordinates().getY() + ";" +
                        ticket.getCreationDate() + ";" +
                        ticket.getPrice() + ";" +
                        (ticket.getType() == null ? "" : ticket.getType()) + ";");
                if (ticket.getEvent() != null)
                    writer.write(ticket.getEvent().getId() + ";" +
                            ticket.getEvent().getName() + ";" +
                            ticket.getEvent().getDate() + ";" +
                            (ticket.getEvent().getType() == null ? "" : ticket.getEvent().getType()) + ";");
                else
                    writer.write(";;;;");
                writer.write(ticket.getId() + ";" + "\n");
            }
            writer.flush();
            writer.close();
            System.out.print("Файл был сохранён\n");
        } catch (Exception e) {
            System.out.print("Возникли проблемы, файл сохранён не был\n");
        }
    }
}