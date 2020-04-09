package lab5;

import java.io.File;
import java.io.FileWriter;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Реализует взаимодействие с пользователем
 */
abstract class Answers {
    private static boolean script = false;
    private static String[] commands;
    private static int i;
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Выводит список доступных команд с комментариями
     */
    static void help_() {
        System.out.printf("%s%n %-8s %s%n %-8s %s%n %-8s %s%n %-8s %s%n %-8s %s%n %-8s %s%n %-8s %s%n %-16s %s%n %-16s %s%n %-16s %s%n %-16s %s%n %-16s %s%n %-16s %s%n %-25s %s%n %-25s %s%n",
                "Дступные команды, регистр не имеет значения:",
                "HELP", "вывести справку по доступным командам",
                "INFO", "вывести информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)",
                "SHOW", "вывести все элементы коллекции в строковом представлении",
                "ADD", "добавить новый элемент в коллекцию",
                "CLEAR", "очистить коллекцию",
                "SAVE", "сохранить коллекцию в файл",
                "EXIT", "завершить программу (без сохранения в файл)",
                "UPDATE", "обновить значение элемента, id которого равен заданному",
                "REMOVE_BY_ID", "удалить элемент из коллекции по его id",
                "EXECUTE_SCRIPT", "считать и исполнить скрипт",
                "ADD_IF_MAX", "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции",
                "ADD_IF_MIN", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции",
                "REMOVE_LOWER", "удалить из коллекции все элементы, меньшие, чем заданный",
                "COUNT_LESS_THEN_PRICE", "вывести количество элементов, значение поля price которых меньше заданного",
                "COUNT_GREATER_THEN_TYPE", "вывести количество элементов, значение поля type которых больше заданного");
    }

    /**
     * Выводит инвормацию о коллекции
     *
     * @param tickets коллекция
     * @param data    время создания коллекции
     */
    static void info_(HashSet<Ticket> tickets, ZonedDateTime data) {
        System.out.println("Коллкция типа " + tickets.getClass().getSimpleName() +
                " содержит " + tickets.size() +
                " элементов и была создана " + data);
    }

    /**
     * Выводит информацию о каждом из элементов коллекции
     *
     * @param tickets коллекция
     */
    static void show_(HashSet<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            System.out.printf("%n%-21s%s%n %-20s%s%n %-20s%s%n %-20s%s%n %-20s%s%n %-20s%s%n",
                    " № билета:", ticket.getId(),
                    "Имя в билете:", ticket.getName(),
                    "Координата x:", ticket.getCoordinates().getX(),
                    "Координата y:", ticket.getCoordinates().getY(),
                    "Дата создания:", ticket.getCreationDate(),
                    "Цена:", ticket.getPrice());
            if (ticket.getType() != null) {
                System.out.printf("%-21s%s%n", " Тип билета:", ticket.getType());
            }
            if (ticket.getEvent() != null) {
                System.out.printf("%-21s%s%n %-20s%s%n %-20s%s%n",
                        " № события:", ticket.getEvent().getId(),
                        "Название события:", ticket.getEvent().getName(),
                        "Время события:", ticket.getEvent().getDate());
                if (ticket.getEvent().getType() != null) {
                    System.out.printf("%-21s%s%n", " Тип события:", ticket.getEvent().getType());
                }
            }
        }
    }

    /**
     * Очищент коллекцию
     *
     * @param tickets коллекция
     */
    static void clear_(HashSet<Ticket> tickets) {
        tickets.clear();
        System.out.print("Коллекция была очищена\n");
    }

    /**
     * Сохряняет коллекцию в файл .csv
     *
     * @param tickets коллекция
     * @param date    время создания коллекции
     */
    static void save_(HashSet<Ticket> tickets, ZonedDateTime date, String path) {
        try {
            FileWriter writer = new FileWriter(path);

            writer.write(date + "\n");
            for (Ticket ticket : tickets) {
                writer.write(ticket.getName() + ";" +
                        ticket.getCoordinates().getX() + ";" +
                        ticket.getCoordinates().getY() + ";" +
                        ticket.getCreationDate() + ";" +
                        ticket.getPrice() + ";" +
                        (ticket.getType() == null ? "" : ticket.getType()) + ";");
                if (ticket.getEvent() != null) {
                    writer.write(ticket.getEvent().getId() + ";" +
                            ticket.getEvent().getName() + ";" +
                            ticket.getEvent().getDate() + ";" +
                            (ticket.getEvent().getType() == null ? "" : ticket.getEvent().getType()) + ";");
                } else {
                    writer.write(";;;;");
                }
                writer.write(ticket.getId() + ";" + "\n");
            }
            writer.flush();
            writer.close();
            System.out.print("Файл был сохранён\n");
        } catch (Exception e) {
            System.out.print("Возникли проблемы, файл сохранён не был\n");
        }
    }

    /**
     * Добавляет новый элемент в коллекцию последовательно получая команда от пользователя
     *
     * @param tickets коллекция
     */
    static void add_(HashSet<Ticket> tickets) {
        Ticket ticket = script ? add_script() : add_universal();
        tickets.add(ticket);
        System.out.print("Элемент был добавлен\n");
    }

    /**
     * Получает список команд из файла формата .csv
     *
     * @param tickets коллекция
     * @param address адрес файла
     * @param date    время создания коллекции
     */
    static void execute_(HashSet<Ticket> tickets, String address, ZonedDateTime date, String path) {
        try {
            script = true;
            Scanner file_scanner = new Scanner(new File(address));
            commands = file_scanner.nextLine().trim().replaceAll(" {2,}", " ").toLowerCase().split(";");
            label:
            for (i = 0; i < commands.length; i++) {
                switch (commands[i]) {
                    case "help":
                        Answers.help_();
                        break;
                    case "info":
                        Answers.info_(tickets, date);
                        break;
                    case "show":
                        Answers.show_(tickets);
                        break;
                    case "clear":
                        Answers.clear_(tickets);
                        break;
                    case "save":
                        Answers.save_(tickets, date, path);
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
                    case "remove_by_id":
                        i++;
                        Answers.remove_(tickets,Integer.parseInt(commands[i]));
                        break;
                    case "count_less_than_price":
                        i++;
                        Answers.count_less_price(tickets,Integer.parseInt(commands[i]));
                        break;
                    case "count_greater_than_type":
                        i++;
                        Answers.count_greater_type(tickets,TicketType.valueOf(commands[i]));
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Извините, команды в файле некорректны" + e + "\n");
        }
        script = false;
    }

    /**
     * Удаляет элемент из коллекции по его номеру
     *
     * @param tickets коллекция
     * @param id      номер элемента
     */
    static void update_(HashSet<Ticket> tickets, int id) {
        String input;
        Iterator<Ticket> iterator = tickets.iterator();
        out:
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next();
            if (ticket.getId() == id) {
                System.out.println("Выберете, какой параметр элемента вы хотите поменять:" +
                        "\n1 - Имя в билете, текущее: " + ticket.getName() +
                        "\n2 - Координата x, текущая: " + ticket.getCoordinates().getX() +
                        "\n3 - Координата y, текущая: " + ticket.getCoordinates().getY() +
                        "\n4 - Стоимость, текущая: " + ticket.getPrice() +
                        "\n5 - Тип билета, текущий: " + (ticket.getType() == null ? "" : ticket.getType()));
                if (ticket.getEvent() != null) {
                    System.out.print("6 - Название события, текущее: " + ticket.getEvent().getName() +
                            "\n7 - Тип события, текущий: " + (ticket.getEvent().getType() == null ? "" : ticket.getEvent().getType()) + "\n");
                } else {
                    System.out.print("6 - Добавить событие\n");
                }
                input = scanner.nextLine().trim();
                if (Check.checkInt(input)) {
                    switch (Integer.parseInt(input)) {
                        case 1:
                            get_users_name(ticket);
                            break;
                        case 2:
                            get_users_x(ticket);
                            break;
                        case 3:
                            get_users_y(ticket);
                            break;
                        case 4:
                            get_users_price(ticket);
                            break;
                        case 5:
                            get_users_ticket_type(ticket);
                            break;
                        case 6:
                            if (ticket.getEvent() != null) {
                                ticket.setEvent(new Event());
                            }
                            while (true) {
                                System.out.print("Введите название события (поле не может быть пустым): ");
                                input = scanner.nextLine().trim();
                                if (!input.isEmpty()) {
                                    ticket.getEvent().setName(input);
                                    break;
                                }
                                System.out.print("Данные были введены некорректно\n");
                            }
                            break;
                        default:
                            if (ticket.getEvent() != null) {
                                get_users_event_type(ticket);
                            }
                            break out;
                    }
                }
                break;
            }
        }
        System.out.print("Данные были успешно изменены\n");
    }

    /**
     * Добавляет в коллекцию новый элекмент, если он больше других элементов
     *
     * @param tickets коллекция
     */
    static void add_if_max_(HashSet<Ticket> tickets) {
        Ticket ticket = script ? add_script() : add_universal();
        for (Ticket other_tickets : tickets) {
            if (ticket.compareTo(other_tickets) < 0) {
                System.out.print("Ваш элемент не является наибольшим, он не был добвлен\n");
                return;
            }
        }
        tickets.add(ticket);
        System.out.print("Элемент был добавлен\n");
    }

    /**
     * Добавляет в коллекцию новый элекмент, если он меньше других элементов
     *
     * @param tickets коллекция
     */
    static void add_if_min_(HashSet<Ticket> tickets) {
        Ticket ticket = script ? add_script() : add_universal();
        for (Ticket other_tickets : tickets) {
            if (ticket.compareTo(other_tickets) > 0) {
                System.out.print("Ваш элемент не является наименьшим, он не был добвлен\n");
                return;
            }
        }
        tickets.add(ticket);
        System.out.print("Элемент был добавлен\n");
    }

    /**
     * Удаляет из коллекции все элементы меньше введённого
     *
     * @param tickets коллекция
     */
    static void remove_lower_(HashSet<Ticket> tickets) {
        Ticket ticket = script ? add_script() : add_universal();
        for (Ticket other_tickets : tickets) {
            if (ticket.compareTo(other_tickets) > 0) {
                System.out.print("Из коллекции был удалён элемент №" + other_tickets.getId() + "\n");
                tickets.remove(other_tickets);
            }
        }
    }

    /**
     * Удалияет элемент по его номеру
     *
     * @param tickets коллекция
     * @param id      номер элемента
     */
    static void remove_(HashSet<Ticket> tickets, int id) {
        for (Ticket ticket : tickets) {
            if (ticket.getId() == id) {
                System.out.print("Из коллекции был удалён элемент №" + ticket.getId() + "\n");
                tickets.remove(ticket);
            }
        }
    }

    /**
     * Удаляет элементы коллекции цена которых меньше введённой
     *
     * @param tickets коллекция
     * @param price   цена
     */
    static void count_less_price(HashSet<Ticket> tickets, Integer price) {
        int num = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getPrice() < price) {
                num++;
            }
        }
        System.out.print("Коллекция содержит " + num + " элементов, удовлетворяющих условию\n");
    }

    /**
     * Удаляет элементы коллекции тип которых больше введённого
     *
     * @param tickets коллеуция
     * @param type    тип
     */
    static void count_greater_type(HashSet<Ticket> tickets, TicketType type) {
        int num = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getType() != null) {
                if (ticket.getType().ordinal() > type.ordinal()) {
                    num++;
                }
            }
        }
        System.out.print("Коллекция содержит " + num + " элементов, удовлетворяющих условию\n");
    }

    private static Ticket add_universal() {
        Ticket ticket = new Ticket();
        String input;
        get_users_name(ticket);
        get_users_x(ticket);
        get_users_y(ticket);
        get_users_price(ticket);
        get_users_ticket_type(ticket);
        System.out.print("Введите имя для события, если не хотите " +
                "добавлять к билету событие, оставте поле пустым: ");
        input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            Event event = new Event();
            event.setName(input);
            ticket.setEvent(event);
            get_users_event_type(ticket);
        }
        return ticket;
    }

    private static Ticket add_script() {
        Ticket ticket = new Ticket();
        ticket.setName(commands[++i]);
        ticket.getCoordinates().setX(Float.parseFloat(commands[++i]));
        ticket.getCoordinates().setY(Long.parseLong(commands[++i]));
        ticket.setPrice(Integer.parseInt(commands[++i]));
        if (!commands[++i].isEmpty()) {
            ticket.setType(TicketType.valueOf(commands[i].toUpperCase()));
        }
        if (!commands[++i].isEmpty()) {
            Event event = new Event();
            event.setName(commands[i]);
            if (!commands[++i].isEmpty()) {
                event.setEventType(EventType.valueOf(commands[i].toUpperCase()));
            }
            ticket.setEvent(event);
        }
        return ticket;
    }

    private static void get_users_name(Ticket ticket) {
        String input;
        while (true) {
            System.out.print("Введите имя для билета (поле не может быть пустым): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                ticket.setName(input);
                break;
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }

    private static void get_users_event_type(Ticket ticket) {
        String input;
        while (true) {
            System.out.print("Введите тип события, варианты:");
            EventType[] all_types = EventType.values();
            for (EventType i : all_types) {
                System.out.print(" " + i + ",");
            }
            System.out.print(" можете оставить поле пустым: ");
            input = scanner.nextLine().trim().toUpperCase();
            if (input.isEmpty()) {
                break;
            }
            if (Check.checkEventEnum(input)) {
                ticket.getEvent().setEventType(EventType.valueOf(input));
                break;
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }

    private static void get_users_ticket_type(Ticket ticket) {
        String input;
        while (true) {
            System.out.print("Введите тип билета, варианты:");
            TicketType[] all_types = TicketType.values();
            for (TicketType i : all_types) {
                System.out.print(" " + i + ",");
            }
            System.out.print(" можете оставить поле пустым: ");
            input = scanner.nextLine().trim().toUpperCase();
            if (input.isEmpty()) {
                break;
            }
            if (Check.checkTicketEnum(input)) {
                ticket.setType(TicketType.valueOf(input));
                break;
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }

    private static void get_users_price(Ticket ticket) {
        String input;
        while (true) {
            System.out.print("Введите стоимость билета (целое число): ");
            input = scanner.nextLine().trim();
            if (Check.checkInt(input)) {
                if (Integer.parseInt(input) > 0) {
                    ticket.setPrice(Integer.parseInt(input));
                    break;
                }
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }

    private static void get_users_y(Ticket ticket) {
        String input;
        while (true) {
            System.out.print("Введите координату Y (целое число): ");
            input = scanner.nextLine();
            if (Check.checkLong(input)) {
                ticket.getCoordinates().setY(Long.parseLong(input));
                break;
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }

    private static void get_users_x(Ticket ticket) {
        String input;
        while (true) {
            System.out.print("Введите координату X (число с плавающей точкой): ");
            input = scanner.nextLine().trim();
            if (Check.checkFloat(input)) {
                ticket.getCoordinates().setX(Float.parseFloat(input));
                break;
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }
}



