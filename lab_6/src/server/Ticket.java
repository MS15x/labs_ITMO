package lab_6.server;

import lab_6.common.TicketType;

import java.time.LocalDate;

/**
 * Класс билета
 */
public class Ticket implements Comparable<Ticket> {

    private static int i;
    private int id;
    private String name;
    private Coordinates coordinates;
    private LocalDate creationDate;
    private Integer price;
    private TicketType type;
    private Event event;

    public Ticket() {
        name = "No_name";
        coordinates = new Coordinates(0, 0);
        creationDate = LocalDate.now();
        price = 1;
        generateID();
    }

    public Ticket(String name,
                  Coordinates coordinates,
                  Integer price,
                  TicketType type,
                  Event event) {
        this.name = name;
        this.coordinates = coordinates;
        creationDate = LocalDate.now();
        this.price = price;
        this.type = type;
        this.event = event;
        generateID();
    }

    private void generateID(){
        i += 1;
        id = i;
    }

    public void setId(int id) {
        this.id = id;
        if (id > i) {
            i = id + 1;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
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
        return creationDate;
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

    /**
     * Сравнивает элементы по возрастанию их координате X
     *
     * @param ticket элемент
     */
    @Override
    public int compareTo(Ticket ticket) {
        return Float.compare(this.coordinates.getX(), ticket.getCoordinates().getX());
    }
}
