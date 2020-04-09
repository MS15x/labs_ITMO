package lab5;

import java.time.LocalDate;

/**
 * Класс билета
 */
public class Ticket implements Comparable<Ticket> {
    Ticket() {
        name = "No_name";
        coordinates = new Coordinates(0, 0);
        creationDate = LocalDate.now();
        price = 1;
        i += 1;
        id = i;
    }

    void setId(int id) {
        this.id = id;
        if (id > i) {
            i = id + 1;
        }
    }

    void setName(String name) {
        this.name = name;
    }

    void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    void setPrice(Integer price) {
        this.price = price;
    }

    void setType(TicketType type) {
        this.type = type;
    }

    void setEvent(Event event) {
        this.event = event;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    Coordinates getCoordinates() {
        return coordinates;
    }

    LocalDate getCreationDate() {
        return creationDate;
    }

    Integer getPrice() {
        return price;
    }

    TicketType getType() {
        return type;
    }

    Event getEvent() {
        return event;
    }

    private static int i;
    private int id;
    private String name;
    private Coordinates coordinates;
    private LocalDate creationDate;
    private Integer price;
    private TicketType type;
    private Event event;

    /**
     * Сравнивает элементы по возрастанию их координате X
     * @param ticket элемент
     */
    @Override
    public int compareTo(Ticket ticket) {
        return Float.compare(this.coordinates.getX(), ticket.getCoordinates().getX());
    }
}
