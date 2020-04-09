package lab5;

import java.io.File;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Главный класс
 */
public class Collection {

    public static void main(String[] args) {
        ZonedDateTime first_date = ZonedDateTime.now();
        Scanner scanner = new Scanner(System.in);
        HashSet<Ticket> tickets = new HashSet<>();
        String path = args[0].trim();

        try {
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
                if (!string[5].isEmpty()) {
                    ticket.setType(TicketType.valueOf(string[5]));
                }
                if (!string[6].isEmpty()) {
                    ticket.setEvent(new Event());
                    ticket.getEvent().setId(Long.parseLong(string[6]));
                    ticket.getEvent().setName(string[7]);
                    ticket.getEvent().setDate(ZonedDateTime.parse(string[8]));
                }
                if (!string[9].isEmpty()) {
                    ticket.getEvent().setEventType(EventType.valueOf(string[9]));
                }
                tickets.add(ticket);
            }
        } catch (Exception e) {
            System.out.println("Извините, не удалось загрузить файл, данные в файле некорректны\n");
        }

        Answers.help_();
        label:
        while (true) {
            String[] commands = scanner.nextLine().trim().replaceAll(" {2,}", " ").toLowerCase().split(" ");//квантификатор
            if (commands.length == 1) {
                switch (commands[0]) {
                    case "help":
                        Answers.help_();
                        break;
                    case "info":
                        Answers.info_(tickets, first_date);
                        break;
                    case "show":
                        Answers.show_(tickets);
                        break;
                    case "clear":
                        Answers.clear_(tickets);
                        break;
                    case "save":
                        Answers.save_(tickets, first_date, path);
                        break;
                    case "add":
                        Answers.add_(tickets);
                        break;
                    case "exit":
                        break label;
                    case "add_if_max":
                        Answers.add_if_max_(tickets);
                        break;
                    case "add_if_min":
                        Answers.add_if_min_(tickets);
                        break;
                    case "remove_lower":
                        Answers.remove_lower_(tickets);
                        break;
                    case "execute_script":
                        try {
                            String script_file = args[1].trim();
                            Answers.execute_(tickets, script_file, first_date, path);
                        } catch (Exception e) {
                            System.out.println("Извините, путь к файлу при запуске был указан некорректно");
                        }
                        break;
                    default:
                        System.out.println("Команда была введена некорректно\n");
                        break;
                }
            } else if (commands.length == 2) {
                if (commands[0].equals("update") && Check.checkInt(commands[1])) {
                    Answers.update_(tickets, Integer.parseInt(commands[1]));
                } else if (commands[0].equals("remove_by_id") && Check.checkInt(commands[1])) {
                    Answers.remove_(tickets, Integer.parseInt(commands[1]));
                } else if (commands[0].equals("count_less_than_price") && Check.checkInt(commands[1])) {
                    Answers.count_less_price(tickets, Integer.parseInt(commands[1]));
                } else if (commands[0].equals("count_greater_than_type") && Check.checkTicketEnum(commands[1])) {
                    Answers.count_greater_type(tickets, TicketType.valueOf(commands[1]));
                } else {
                    System.out.println("Команда была введена некорректно\n");
                }
            } else {
                System.out.println("Команда была введена некорректно\n");
            }
        }
    }
}
