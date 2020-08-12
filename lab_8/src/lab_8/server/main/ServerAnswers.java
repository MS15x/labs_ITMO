package lab_8.server.main;

import lab_8.common.*;
import lab_8.common.ticket.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Обрабатывает команды пользователя на стороне сервера
 */
public abstract class ServerAnswers {
    /**
     * Обрабатывает команды
     *
     * @param command команда
     * @param tickets коллекция билетов
     * @param date    дата создания коллекции
     * @return возвращает коменду для топравки пользователю
     */
    public static synchronized ServerCommand doCommand(ServerCommand command, ArrayList<Ticket> tickets, ZonedDateTime date) {
        ServerCommand answer = null;
        switch (command.getCommand()) {
            case add:
                answer = add_(tickets, command.getInformation(), command.getUser());
                break;
            case add_if_max:
                //answer = add_if_max_(tickets, command.getInformation(), command.getUser());
                break;
            case add_if_min:
                //answer = add_if_min_(tickets, command.getInformation(), command.getUser());
                break;
            case clear:
                answer = clear_(tickets, command.getUser());
                break;
            case count_greater_then_type:
                //answer = count_greater_then_type_(tickets, command.getInformation());
                break;
            case count_less_then_price:
                //answer = count_less_then_price_(tickets, command.getInformation());
                break;
            case info:
                //answer = info_(tickets, date);
                break;
            case help:
                //answer = help();
                break;
            case remove_by_id:
                answer = remove_by_id_(tickets, command.getInformation(), command.getUser());
                break;
            case remove_lower:
                //answer = remove_lover_(tickets, command.getInformation(), command.getUser());
                break;
            case show:
                answer = show_(tickets);
                break;
            case inf_by_id:
                //answer = inf_by_id(tickets, command.getInformation(), command.getUser());
                break;
            case update:////////////
                answer = update_(tickets, command.getInformation(), command.getUser());
                break;
            default:
                answer = new ServerCommand(AllCommands.service, String.format("%n%s", "Команда не обнаружена"));
        }
        return answer;
    }

    private static ServerCommand add_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        Ticket ticket = add_basic(inf, user);
        tickets.add(ticket);
        ArrayList<String> answerinf = new ArrayList<>(Arrays.asList("Элемент был успешно добавлен", String.valueOf(ticket.getId()),
                ticket.getUser(),
                String.valueOf(ticket.getCreationDate()),
                ticket.getName(),
                String.valueOf(ticket.getCoordinates().getX()),
                String.valueOf(ticket.getCoordinates().getY()),
                String.valueOf(ticket.getPrice()),
                ticket.getType() == null ? "" : String.valueOf(ticket.getType())));
        if (ticket.getEvent() != null)
            answerinf.addAll(Arrays.asList(ticket.getEvent().getName(),
                    ticket.getEvent().getType() == null ? "" : String.valueOf(ticket.getEvent().getType()),
                    String.valueOf(ticket.getEvent().getDate())));
        else
            answerinf.addAll(Arrays.asList("", "", ""));
        return new ServerCommand(AllCommands.add, true, answerinf);
    }

    /*private static ServerCommand add_if_max_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        Ticket ticket = add_basic(inf, user);
        if (tickets.stream().anyMatch(a -> a.compareTo(ticket) >= 0))
            return new ServerCommand(AllCommands.service, String.format("%n%s%n", "Ваш элемент не был добвлен, так как он не является наибольшим"));
        else {
            tickets.add(ticket);
            return new ServerCommand(AllCommands.service, String.format("%n%s%n", "Элемент был успешно добавлен"));
        }
    }

    private static ServerCommand add_if_min_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        Ticket ticket = add_basic(inf, user);
        if (tickets.stream().anyMatch(a -> a.compareTo(ticket) <= 0))
            return new ServerCommand(AllCommands.service, String.format("%n%s%n", "Ваш элемент не был добвлен, так как он не является наименьшим"));
        else {
            tickets.add(ticket);
            return new ServerCommand(AllCommands.service, String.format("%n%s%n", "Элемент был успешно добавлен"));
        }
    }*/

    private static ServerCommand clear_(ArrayList<Ticket> tickets, String user) {
        List<Ticket> found_tickets = tickets.stream().filter(a -> a.getUser().equals(user)).collect(Collectors.toList());
        tickets.removeAll(found_tickets);
        ArrayList<String> id_found = new ArrayList<>();
        id_found.add("Все ваши элементы были удалены");
        for (Ticket ticket : found_tickets)
            id_found.add(String.valueOf(ticket.getId()));
        return new ServerCommand(AllCommands.clear, true, id_found);
    }

    /*private static ServerCommand count_greater_then_type_(ArrayList<Ticket> tickets, ArrayList<String> inf) {
        long num = tickets.stream().filter(a -> a.getType() != null).filter(a -> a.getType().ordinal() > TicketType.valueOf(inf.get(0)).ordinal()).count();
        return new ServerCommand(AllCommands.service, String.format("%n%s",
                "Коллекция содержит " + num + " элементов с большим типом"));
    }

    private static ServerCommand count_less_then_price_(ArrayList<Ticket> tickets, ArrayList<String> inf) {
        long num = tickets.stream().filter(a -> a.getPrice() < Integer.parseInt(inf.get(0))).count();
        return new ServerCommand(AllCommands.service, String.format("%n%s",
                "Коллекция содержит " + num + " элементов с меньшей ценой"));
    }

    private static ServerCommand info_(ArrayList<Ticket> tickets, ZonedDateTime date) {
        return new ServerCommand(AllCommands.service, String.format("%n%s%n", "Коллкция типа " +
                tickets.getClass().getSimpleName() +
                " содержит " + tickets.size() +
                " элементов и была создана " + date));
    }

    private static ServerCommand help() {
        return new ServerCommand(AllCommands.service, String.format("%s%n %-8s %s%n %-8s %s%n %-8s %s%n %-8s %s%n %-8s %s%n %-8s %s%n %-16s %s%n %-16s %s%n %-16s %s%n %-16s %s%n %-16s %s%n %-16s %s%n %-25s %s%n %-25s %s%n",
                "Дступные команды, регистр не имеет значения:",
                "HELP", "вывести справку по доступным командам",
                "INFO", "вывести информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)",
                "SHOW", "вывести все элементы коллекции в строковом представлении",
                "ADD", "добавить новый элемент в коллекцию",
                "CLEAR", "очистить коллекцию",
                "EXIT", "завершить программу",
                "UPDATE", "обновить значение элемента, id которого равен заданному",
                "REMOVE_BY_ID", "удалить элемент из коллекции по его id",
                "EXECUTE_SCRIPT", "считать и исполнить скрипт (для корректной роботы указывайте полный путь к файлу)",
                "ADD_IF_MAX", "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции",
                "ADD_IF_MIN", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции",
                "REMOVE_LOWER", "удалить из коллекции все элементы, меньшие, чем заданный",
                "COUNT_LESS_THEN_PRICE", "вывести количество элементов, значение поля price которых меньше заданного",
                "COUNT_GREATER_THEN_TYPE", "вывести количество элементов, значение поля type которых больше заданного"));
    }*/

    private static ServerCommand remove_by_id_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        int id = Integer.parseInt(inf.get(0));
        if (tickets.removeIf(a -> (a.getId() == id & a.getUser().equals(user))))
            return new ServerCommand(AllCommands.remove_by_id, true, "Элемент был успешно удалён", String.valueOf(id));
        else
            return new ServerCommand(AllCommands.remove_by_id, false, "Этот элемент вам не принадлежит", "-1");
    }
    /*

    private static ServerCommand remove_lover_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        Ticket test_ticket = tickets.stream().filter(a -> a.getId() == Integer.parseInt(inf.get(0))).findAny().orElse(null);
        if (test_ticket == null)
            return new ServerCommand(AllCommands.service, String.format("%n%s%n", "Элемент с ID" + inf.get(0) + " не был найден"));
        List<Ticket> found_tickets = tickets.stream().filter(a -> a.compareTo(test_ticket) < 0 & a.getUser().equals(user)).collect(Collectors.toList());
        tickets.removeAll(found_tickets);
        return new ServerCommand(AllCommands.service,
                String.format("%n%s%n", found_tickets.isEmpty() ? "Подходящих под критерии элементов, принадлежащих вам, не нашлось, ни один элемент удалён не был" :
                        found_tickets.stream().reduce("Из коллекции были удалёны элементы с ID:  ", (x, y) -> x + "  " + y.getId(), (x, y) -> x + y)));

    }*/

    private static ServerCommand show_(ArrayList<Ticket> tickets) {

        ArrayList<String> answerinf = new ArrayList<>();
        answerinf.add(null);
        for (Ticket ticket : tickets) {
            answerinf.addAll(Arrays.asList(String.valueOf(ticket.getId()),
                    ticket.getUser(),
                    String.valueOf(ticket.getCreationDate()),
                    ticket.getName(),
                    String.valueOf(ticket.getCoordinates().getX()),
                    String.valueOf(ticket.getCoordinates().getY()),
                    String.valueOf(ticket.getPrice()),
                    ticket.getType() == null ? "" : String.valueOf(ticket.getType())));
            if (ticket.getEvent() != null)
                answerinf.addAll(Arrays.asList(ticket.getEvent().getName(),
                        ticket.getEvent().getType() == null ? "" : String.valueOf(ticket.getEvent().getType()),
                        String.valueOf(ticket.getEvent().getDate())));
            else
                answerinf.addAll(Arrays.asList("", "", ""));
        }
        return new ServerCommand(AllCommands.show, true, answerinf);
    }

    /*
    private static ServerCommand inf_by_id(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        ArrayList<String> answerinf = new ArrayList<>();
        answerinf.add("Выберете, какие данные билета вы хотите поменять:");
        StringBuilder format = new StringBuilder("%n%s");
        Ticket ticket = tickets.stream().filter(a -> a.getId() == Integer.parseInt(inf.get(0))).findAny().orElse(null);
        if (ticket == null)
            return new ServerCommand(AllCommands.inf_by_id, false, String.format("%n%s%n", "Билет с id " + inf.get(0) + " не найден"));
        else if (!ticket.getUser().equals(user))
            return new ServerCommand(AllCommands.inf_by_id, false, String.format("%n%s%n", "Билет с id " + inf.get(0) + " вам не принадлежит"));
        else {
            format.append("%n%-36s%s%n %-35s%s%n %-35s%s%n %-35s%s%n");
            answerinf.addAll(Arrays.asList(" 1 - Имя в билете, текущее:", ticket.getName(),
                    "2 - Координата x, текущая:", String.valueOf(ticket.getCoordinates().getX()),
                    "3 - Координата y, текущая:", String.valueOf(ticket.getCoordinates().getY()),
                    "4 - Стоимость, текущая:", String.valueOf(ticket.getPrice())));
            if (ticket.getType() != null) {
                format.append("%-36s%s%n");
                answerinf.addAll(Arrays.asList(" 5 - Тип билета, текущий:", String.valueOf(ticket.getType())));
            } else {
                format.append("%-35s%n");
                answerinf.add(" 5 - Добавить тип билету");
            }
            if (ticket.getEvent() != null) {
                format.append("%-36s%s%n");
                answerinf.addAll(Arrays.asList(" 6 - Название события, текущее:", ticket.getEvent().getName()));
                if (ticket.getEvent().getType() != null) {
                    format.append("%-36s%s%n");
                    answerinf.addAll(Arrays.asList(" 7 - Тип события, текущий:", String.valueOf(ticket.getEvent().getType())));
                } else {
                    format.append("%-35s%n");
                    answerinf.add(" 7 - Добавить тип события");
                }
            } else {
                format.append("%-30s%n");
                answerinf.add(" 6 - Добавить событие");
            }
        }
        format.append("%s%n");
        answerinf.add("Введите через пробел число и новое значение:");
        return new ServerCommand(AllCommands.service, true, String.format(format.toString(), answerinf.toArray()));
    }
    */
    private static ServerCommand update_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        Ticket ticket = tickets.stream().filter(a -> a.getId() == Integer.parseInt(inf.get(0))).findAny().orElse(null);
        ArrayList<String> answerinf = new ArrayList<>(Arrays.asList("", inf.get(0)));
        try {
            if (ticket != null && ticket.getUser().equals(user))
                switch (Integer.parseInt(inf.get(1))) {
                    case 2:
                        ticket.setDate(LocalDate.parse(inf.get(2)));
                        answerinf.addAll(Arrays.asList("2", String.valueOf(ticket.getCreationDate())));
                        break;
                    case 3:
                        if(inf.get(2).equals(""))
                            throw new Exception();
                        ticket.setName(inf.get(2));
                        answerinf.addAll(Arrays.asList("3", ticket.getName()));
                        break;
                    case 4:
                        ticket.getCoordinates().setX(Float.parseFloat(inf.get(2)));
                        answerinf.addAll(Arrays.asList("4", String.valueOf(ticket.getCoordinates().getX())));
                        break;
                    case 5:
                        ticket.getCoordinates().setY(Long.parseLong(inf.get(2)));
                        answerinf.addAll(Arrays.asList("5", String.valueOf(ticket.getCoordinates().getY())));
                        break;
                    case 6:
                        ticket.setPrice(Integer.parseInt(inf.get(2)));
                        answerinf.addAll(Arrays.asList("6", String.valueOf(ticket.getPrice())));
                        break;
                    case 7:
                        ticket.setType(inf.get(2).equals("") ? null : TicketType.valueOf(inf.get(2).toUpperCase()));
                        answerinf.addAll(Arrays.asList("7", ticket.getType() == null ? "" : String.valueOf(ticket.getType())));
                        break;
                    case 8:
                        answerinf.add("8");
                        if (!inf.get(2).equals(""))
                            if (ticket.getEvent() == null)
                                ticket.setEvent(new Event(inf.get(2)));
                            else
                                ticket.getEvent().setName(inf.get(2));
                        else
                            ticket.setEvent(null);
                        if (ticket.getEvent() != null)
                            answerinf.addAll(Arrays.asList(ticket.getEvent().getName(),
                                    ticket.getEvent().getType() == null ? "" : String.valueOf(ticket.getEvent().getType()),
                                    String.valueOf(ticket.getEvent().getDate())));
                        else
                            answerinf.addAll(Arrays.asList("", "", ""));
                        break;
                    case 9:
                        if (ticket.getEvent() != null)
                            ticket.getEvent().setEventType(inf.get(2).equals("") ? null : EventType.valueOf(inf.get(2).toUpperCase()));
                        else
                            throw new Exception();
                        answerinf.addAll(Arrays.asList("9", ticket.getEvent().getType() == null ? "" : String.valueOf(ticket.getEvent().getType())));
                        break;
                    default:
                        throw new Exception();
                }
            else
                throw new Exception();
        } catch (Exception e) {
            return new ServerCommand(AllCommands.service, false,"Данные введены некоректно, элемент не был изменён");
        }
        return new ServerCommand(AllCommands.update, true, answerinf);
    }

    private static Ticket add_basic(ArrayList<String> inf, String user) {
        return new Ticket(inf.get(0),
                LocalDate.parse(inf.get(1)),
                new Coordinates(Float.parseFloat(inf.get(2)),
                        Long.parseLong(inf.get(3))),
                Integer.parseInt(inf.get(4)),
                inf.get(5).isEmpty() ? null : TicketType.valueOf(inf.get(5)),
                inf.get(6).isEmpty() ? null : new Event(inf.get(6),
                        inf.get(7).isEmpty() ? null : EventType.valueOf(inf.get(7))),
                user);
    }
}
