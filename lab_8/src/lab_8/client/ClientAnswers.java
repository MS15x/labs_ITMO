package lab_8.client;

import lab_8.common.*;
import lab_8.common.ticket.EventType;
import lab_8.common.ticket.TicketType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

/**
 * Обрабатывает команды пользователя на стороне клиента
 */
public abstract class ClientAnswers {
    private static boolean script = false;
    private static Scanner scanner = new Scanner(System.in);
    private static Stack<Scanner> files = new Stack<>();
    private static Stack<String> addresses = new Stack<>();

    /**
     * Обрабатывает команды пользователя
     *
     * @param command команда
     */
    public static void doCommand(String[] command) {
        try {
            if (command.length == 1)
                switch (AllCommands.valueOf(command[0])) {
                    case add:
                        add_();
                        break;
                    case add_if_max:
                        add_if_max();
                        break;
                    case add_if_min:
                        add_if_min();
                        break;
                    case clear:
                        clear_();
                        break;
                    case help:
                        help_();
                        break;
                    case info:
                        info_();
                        break;
                    case show:
                        show_();
                        break;
                    default:
                        System.out.print("Команда введена некорректно\n");
                }
            else if (command.length == 2) {
                if (Check.checkInt(command[1])) {
                    switch (AllCommands.valueOf(command[0])) {
                        case remove_by_id:
                            remove_by_id_(command[1]);
                            break;
                        case remove_lower:
                            remove_lower_(command[1]);
                            break;
                        case update:
                            update_(command[1]);
                            break;
                        case count_less_then_price:
                            count_less_then_price_(command[1]);
                            break;
                        default:
                            System.out.print("Команда введена некорректно\n");
                    }
                } else if (Check.checkTicketEnum(command[1]) && AllCommands.valueOf(command[0]) == AllCommands.count_greater_then_type)
                    count_greater_then_type_(command[1]);
                else if (AllCommands.valueOf(command[0]) == AllCommands.execute_script)
                    execute_script_(command[1]);
                else
                    System.out.print("Команда введена некорректно\n");
            } else
                System.out.print("Команда введена некорректно\n");
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.print("Команда введена некорректно\n");
        }
    }

    private static void add_() {
        ServerCommand request = new ServerCommand(AllCommands.add);
        ServerCommand answer = Client.connect(add_basic(request));
        System.out.print(answer.getInformation().get(0));
    }

    private static void add_if_max() {
        ServerCommand request = new ServerCommand(AllCommands.add_if_max);
        ServerCommand answer = Client.connect(add_basic(request));
        System.out.print(answer.getInformation().get(0));
    }

    private static void add_if_min() {
        ServerCommand request = new ServerCommand(AllCommands.add_if_min);
        ServerCommand answer = Client.connect(add_basic(request));
        System.out.print(answer.getInformation().get(0));
    }

    private static void clear_() {
        ServerCommand answer = Client.connect(new ServerCommand(AllCommands.clear));
        System.out.print(answer.getInformation().get(0));
    }

    private static void count_less_then_price_(String price) {
        ServerCommand answer = Client.connect(new ServerCommand(AllCommands.count_less_then_price, price));
        System.out.print(answer.getInformation().get(0));
    }

    private static void count_greater_then_type_(String type) {
        ServerCommand answer = Client.connect(new ServerCommand(AllCommands.count_greater_then_type, type));
        System.out.print(answer.getInformation().get(0));
    }

    private static void execute_script_(String address) {
        try {
            script = true;
            Scanner s = new Scanner(new File(address));
            if (!addresses.contains(address)) {
                addresses.push(address);
                files.push(s);
                Scanner file_scanner = files.peek();
                String[] commands;
                while (file_scanner.hasNextLine()) {
                    commands = file_scanner.nextLine().trim().replaceAll(" {2,}", " ").toLowerCase().split(" ");
                    doCommand(commands);
                }
                addresses.pop();
                files.pop();
            } else
                System.out.println("Вы не можете циклически открывать одни и теже скрипты, команда EXECUTE_SCRIPT "
                        + address + " не была выполнена\n");
        } catch (FileNotFoundException e) {
            System.out.println("Не удалось найти или открыть файл\n");
        } catch (Exception e) {
            System.out.println("Команды в файле " + address + " некорректны\n");
        }
        if (files.empty())
            script = false;
    }

    private static void help_() {
        ServerCommand answer = Client.connect(new ServerCommand(AllCommands.help));
        System.out.print(answer.getInformation().get(0));
    }

    private static void info_() {
        ServerCommand answer = Client.connect(new ServerCommand(AllCommands.info));
        System.out.print(answer.getInformation().get(0));
    }

    private static void remove_by_id_(String id) {
        ServerCommand answer = Client.connect(new ServerCommand(AllCommands.remove_by_id, id));
        System.out.print(answer.getInformation().get(0));
    }

    private static void remove_lower_(String id) {
        ServerCommand answer = Client.connect(new ServerCommand(AllCommands.remove_lower, id));
        System.out.print(answer.getInformation().get(0));
    }

    private static void show_() {
        ServerCommand answer = Client.connect(new ServerCommand(AllCommands.show));
        System.out.print(answer.getInformation().get(0));
    }

    ////
    private static void update_(String id) {
        ServerCommand answer = Client.connect(new ServerCommand(AllCommands.inf_by_id, id));
        System.out.print(answer.getInformation().get(0));
        if (!answer.getResult()) {
            System.out.print(answer.getInformation().get(0));
            return;
        }
        String[] s = scanner.nextLine().trim().split(" ");
        if (s.length == 2 && Check.checkInt(s[0])) {
            answer = Client.connect(new ServerCommand(AllCommands.update, id, s[0].trim(), s[1].trim()));
            System.out.print(answer.getInformation().get(0));
        } else
            System.out.print("Команда введена некорректно\n");
    }

    private static ServerCommand add_basic(ServerCommand request) {
        if (!script) {
            get_users_name(request);
            get_users_x(request);
            get_users_y(request);
            get_users_price(request);
            get_users_ticket_type(request);
            System.out.print("Введите имя для события, если не хотите " +
                    "добавлять к билету событие, оставте поле пустым: ");
            String input = scanner.nextLine().trim();
            request.addInformation(input);
            if (!input.isEmpty())
                get_users_event_type(request);
            else
                request.addInformation("");
        } else {
            String input;
            Scanner file_scanner = files.peek();
            for (int i = 0; i < 4; i++)
                request.addInformation(file_scanner.nextLine().trim());
            input = file_scanner.nextLine().trim().toUpperCase();
            request.addInformation(input);
            if (!input.isEmpty())
                request.addInformation(file_scanner.nextLine().trim().toUpperCase());
        }
        return request;
    }

    private static void get_users_name(ServerCommand answer) {
        String input;
        while (true) {
            System.out.print("Введите имя для билета (поле не может быть пустым): ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                answer.addInformation(input);
                break;
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }

    private static void get_users_y(ServerCommand answer) {
        String input;
        while (true) {
            System.out.print("Введите координату Y (целое число от -9223372036854775808 до 9223372036854775807): ");
            input = scanner.nextLine();
            if (Check.checkLong(input)) {
                answer.addInformation(input);
                break;
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }

    private static void get_users_x(ServerCommand answer) {
        String input;
        while (true) {
            System.out.print("Введите координату X (число с плавающей точкой, ВНИМАНИЕ, числа имеющие более 7 цифр могут сохраниться некорректно): ");
            input = scanner.nextLine().trim();
            if (Check.checkFloat(input)) {
                answer.addInformation(input);
                break;
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }

    private static void get_users_price(ServerCommand answer) {
        String input;
        while (true) {
            System.out.print("Введите стоимость билета (целое положительное число не более 2147483647): ");
            input = scanner.nextLine().trim();
            if (Check.checkInt(input)) {
                answer.addInformation(input);
                break;
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }

    private static void get_users_ticket_type(ServerCommand answer) {
        String input;
        while (true) {
            System.out.print("Введите тип билета, варианты:");
            TicketType[] all_types = TicketType.values();
            for (TicketType i : all_types)
                System.out.print(" " + i + ",");
            System.out.print(" можете оставить поле пустым: ");

            input = scanner.nextLine().trim().toUpperCase();
            if (Check.checkTicketEnum(input) || input.isEmpty()) {
                answer.addInformation(input);
                break;
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }

    private static void get_users_event_type(ServerCommand answer) {
        String input;
        while (true) {
            System.out.print("Введите тип события, варианты:");
            EventType[] all_types = EventType.values();
            for (EventType i : all_types)
                System.out.print(" " + i + ",");
            System.out.print(" можете оставить поле пустым: ");
            input = scanner.nextLine().trim().toUpperCase();
            if (Check.checkEventEnum(input) || input.isEmpty()) {
                answer.addInformation(input);
                break;
            }
            System.out.print("Данные были введены некорректно\n");
        }
    }
}
