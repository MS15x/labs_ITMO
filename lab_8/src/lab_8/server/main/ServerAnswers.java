package lab_8.server.main;

import lab_8.common.*;
import lab_8.server.ticket.*;
import lab_8.server.service.Database;

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
        ServerCommand answer;
        switch (command.getCommand()) {
            case add:
                answer = add_(tickets, command.getInformation(), command.getUser());
                break;
            case clear:
                answer = clear_(tickets, command.getUser());
                break;
            case remove_by_id:
                answer = remove_by_id_(tickets, command.getInformation(), command.getUser());
                break;
            case show:
                answer = show_(tickets);
                break;
            case update:
                answer = update_(tickets, command.getInformation(), command.getUser());
                break;
            default:
                answer = new ServerCommand(AllCommands.service, "Команда не обнаружена");
        }
        Database.save(tickets);
        return answer;
    }

    private static ServerCommand add_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        Ticket ticket = add_basic(inf, user);
        tickets.add(ticket);
        ArrayList<String> answerinf = new ArrayList<>(Arrays.asList("Ans-209", String.valueOf(ticket.getId()),
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

    private static ServerCommand clear_(ArrayList<Ticket> tickets, String user) {
        List<Ticket> found_tickets = tickets.stream().filter(a -> a.getUser().equals(user)).collect(Collectors.toList());
        tickets.removeAll(found_tickets);
        ArrayList<String> id_found = new ArrayList<>();
        id_found.add("Ans-206");
        for (Ticket ticket : found_tickets)
            id_found.add(String.valueOf(ticket.getId()));
        return new ServerCommand(AllCommands.clear, true, id_found);
    }

    private static ServerCommand remove_by_id_(ArrayList<Ticket> tickets, ArrayList<String> inf, String user) {
        int id = Integer.parseInt(inf.get(0));
        if (tickets.removeIf(a -> (a.getId() == id & a.getUser().equals(user))))
            return new ServerCommand(AllCommands.remove_by_id, true, "Ans-207", String.valueOf(id));
        else
            return new ServerCommand(AllCommands.remove_by_id, false, "Ans-208", "-1");
    }

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
