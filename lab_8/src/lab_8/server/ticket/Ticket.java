package lab_8.server.ticket;

import java.time.LocalDate;

/**
 * Класс билета
 */
public class Ticket implements Comparable<Ticket> {

    private static int i;
    private int id;
    private String name, user;
    private Coordinates coordinates;
    private LocalDate date;
    private Integer price;
    private TicketType type;
    private Event event;

    public Ticket(String name,
                  LocalDate date,
                  Coordinates coordinates,
                  Integer price,
                  TicketType type,
                  Event event,
                  String user) {
        this.name = name;
        this.coordinates = coordinates;
        this.date = date;
        this.price = price;
        this.type = type;
        this.event = event;
        this.user = user;
        generateID();
    }

    public Ticket(int id,
                  String name,
                  Coordinates coordinates,
                  LocalDate date,
                  Integer price,
                  TicketType type,
                  Event event,
                  String user) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.date = date;
        this.price = price;
        this.type = type;
        this.event = event;
        this.user = user;
        if (id > i)
            i = id + 1;
    }

    private void generateID() {
        i += 1;
        id = i;
    }

    public void setId(int id) {
        this.id = id;
        if (id > i)
            i = id + 1;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return date;
    }

    public Integer getPrice() {
        return price;
    }

    public TicketType getType() {
        return type;
    }

    public Event getEvent() {
        return event;
    }

    public String getUser() {
        return user;
    }

    /**
     * Сравнивает элементы по пользователю
     *
     * @param ticket элемент
     */
    @Override
    public int compareTo(Ticket ticket) {
        return this.getUser().compareTo(ticket.getUser());
    }
}
