package lab_7.server.main;

import lab_7.common.*;
import lab_7.server.ticket.Coordinates;
import lab_7.server.ticket.Event;
import lab_7.server.ticket.Ticket;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
        ServerCommand answer;
        switch (command.getCommand()) {
            case add:
                answer = add_(tickets, command.getInformation(), command.getUser());
                break;
            case add_if_max:
                answer = add_if_max_(tickets, command.getInformation(), command.getUser());
                break;
            case add_if_min:
                answer = add_if_min_(tickets, command.getInformation(), command.getUser());
                break;
            case clear:
                answer = clear_(tickets, command.getUser());
                break;
            case count_greater_then_type:
                answer = count_greater_then_type_(tickets, command.getInformation());
                break;
            case count_less_then_price:
                answer = count_less_then_price_(tickets, command.getInformation());
                break;
            case info:
                answer = info_(tickets, date);
                break;
            case remove_by_id:
                answer = remove_by_id_(tickets, command.getInformation(), command.getUser());
                break;
            case remove_lower:
                answer = remove_lover_(tickets, command.getInformation(), command.getUser());
                break;
            case show:
                answer = show_(tickets);
                break;
            case inf_by_id:
                answer = inf_by_id(tickets, command.getInformation());
                break;
            case update:////////////
                answer = update_(tickets, command.getInformation());
                break;
            default:
                answer = new ServerCommand(AllCommands.service, String.format("%n%s", "Команда не обнаружена"));
        }
        return answer;
    }

    private static ServerCommand add_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        tickets.add(add_basic(inf, user));
        return new ServerCommand(AllCommands.service, String.format("%n%s%n", "Элемент был успешно добавлен"));
    }

    private static ServerCommand add_if_max_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
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
    }

    private static ServerCommand clear_(ArrayList<Ticket> tickets, String user) {
        List<Ticket> found_tickets = tickets.stream().filter(a -> a.getUser().equals(user)).collect(Collectors.toList());
        tickets.removeAll(found_tickets);
        return new ServerCommand(AllCommands.service, String.format("%n%s%n", "Все ваши элементы были удалены"));
    }

    private static ServerCommand count_greater_then_type_(ArrayList<Ticket> tickets, ArrayList<String> inf) {
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

    private static ServerCommand remove_by_id_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        int id = Integer.parseInt(inf.get(0));
        return new ServerCommand(AllCommands.service, tickets.removeIf(a -> (a.getId() == id & a.getUser().equals(user))) ?
                String.format("%n%s%n", "Из коллекции был удалён элемент с ID " + id) :
                String.format("%n%s%n", "Элемент с ID " + id + " не принадлежит вам, либо не был найден"));

    }

    private static ServerCommand remove_lover_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        Ticket test_ticket = tickets.stream().filter(a -> a.getId() == Integer.parseInt(inf.get(0))).findAny().orElse(null);
        if (test_ticket == null)
            return new ServerCommand(AllCommands.service, String.format("%n%s%n", "Элемент с ID" + inf.get(0) + " не был найден"));
        List<Ticket> found_tickets = tickets.stream().filter(a -> a.compareTo(test_ticket) < 0 & a.getUser().equals(user)).collect(Collectors.toList());
        tickets.removeAll(found_tickets);
        return new ServerCommand(AllCommands.service,
                String.format("%n%s%n", found_tickets.isEmpty() ? "Подходящих под критерии элементов, принадлежащих вам, не нашлось, ни один элемент удалён не был" :
                        found_tickets.stream().reduce("Из коллекции были удалёны элементы с ID:  ", (x, y) -> x + "  " + y.getId(), (x, y) -> x + y)));

    }

    private static ServerCommand show_(ArrayList<Ticket> tickets) {
        tickets.sort(Comparator.comparing(Ticket::getName));
        ArrayList<String> answerinf = new ArrayList<>();
        answerinf.add("Данные билетов:");
        StringBuilder format = new StringBuilder("%n%s");
        for (Ticket ticket : tickets) {
            format.append("%n%-21s%s%n %-20s%s%n %-20s%s%n %-20s%s%n %-20s%s%n %-20s%s%n");
            answerinf.addAll(Arrays.asList(" № билета:", String.valueOf(ticket.getId()),
                    "Имя в билете:", ticket.getName(),
                    "Координата x:", String.valueOf(ticket.getCoordinates().getX()),
                    "Координата y:", String.valueOf(ticket.getCoordinates().getY()),
                    "Дата создания:", String.valueOf(ticket.getCreationDate()),
                    "Цена:", String.valueOf(ticket.getPrice())));
            if (ticket.getType() != null) {
                format.append("%-21s%s%n");
                answerinf.addAll(Arrays.asList(" Тип билета:", String.valueOf(ticket.getType())));
            }
            if (ticket.getEvent() != null) {
                format.append("%-21s%s%n %-20s%s%n %-20s%s%n");
                answerinf.addAll(Arrays.asList(" № события:", String.valueOf(ticket.getEvent().getId()),
                        "Название события:", ticket.getEvent().getName(),
                        "Время события:", String.valueOf(ticket.getEvent().getDate())));
                if (ticket.getEvent().getType() != null) {
                    format.append("%-21s%s%n");
                    answerinf.addAll(Arrays.asList(" Тип события:",
                            String.valueOf(ticket.getEvent().getType())));
                }
            }
        }
        return new ServerCommand(AllCommands.service, String.format(format.toString(), answerinf.toArray()));
    }

    private static ServerCommand inf_by_id(ArrayList<Ticket> tickets, ArrayList<String> inf) {
        ArrayList<String> answerinf = new ArrayList<>();
        answerinf.add("Выберете, какие данные билета вы хотите поменять:");
        StringBuilder format = new StringBuilder("%n%s");
        for (Ticket ticket : tickets)
            if (ticket.getId() == Integer.parseInt(inf.get(0))) {
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
        return new ServerCommand(AllCommands.service, String.format(format.toString(), answerinf.toArray()));
    }

    private static ServerCommand update_(ArrayList<Ticket> tickets, ArrayList<String> inf) {
        if (inf.get(0).isEmpty())
            return new ServerCommand(AllCommands.service, String.format("%n%s", "Элемент не был изменён"));
        else {
            Ticket ticket = tickets.stream().filter(a -> a.getId() == Integer.parseInt(inf.get(0))).findAny().orElse(null);
            try {
                if (ticket != null)
                    switch (Integer.parseInt(inf.get(1))) {
                        case 1:
                            ticket.setName(inf.get(2));
                            break;
                        case 2:
                            ticket.getCoordinates().setX(Float.parseFloat(inf.get(2)));
                            break;
                        case 3:
                            ticket.getCoordinates().setY(Long.parseLong(inf.get(2)));
                            break;
                        case 4:
                            ticket.setPrice(Integer.parseInt(inf.get(2)));
                            break;
                        case 5:
                            ticket.setType(TicketType.valueOf(inf.get(2).toUpperCase()));
                            break;
                        case 6:
                            if (ticket.getEvent() == null)
                                ticket.setEvent(new Event(inf.get(2)));
                            break;
                        case 7:
                            if (ticket.getEvent() != null)
                                ticket.getEvent().setEventType(EventType.valueOf(inf.get(2).toUpperCase()));
                            else
                                throw new NullPointerException();
                            break;
                        default:
                            throw new NullPointerException();
                    }
                else
                    throw new NullPointerException();
            } catch (NullPointerException e) {
                return new ServerCommand(AllCommands.service, String.format("%n%s%n", "Данные введены некоректно, элемент не был изменён"));
            }
            return new ServerCommand(AllCommands.service, String.format("%n%s%n", "Элемент был успешно изменён"));
        }
    }

    private static Ticket add_basic(ArrayList<String> inf, String user) {
        return new Ticket(inf.get(0),
                new Coordinates(Float.parseFloat(inf.get(1)),
                        Long.parseLong(inf.get(2))),
                Integer.parseInt(inf.get(3)),
                inf.get(4).isEmpty() ? null : TicketType.valueOf(inf.get(4)),
                inf.get(5).isEmpty() ? null : new Event(inf.get(5),
                        inf.get(6).isEmpty() ? null : EventType.valueOf(inf.get(6))),
                user);
    }
}
