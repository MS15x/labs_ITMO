package lab_7.server.ticket;

import lab_7.common.EventType;

import java.time.ZonedDateTime;

/**
 * Класс события
 */
public class Event {
    private static long j;
    private long id;
    private String name;
    private ZonedDateTime date;
    private EventType eventType;

    public Event(String name) {
        this.name = name;
        date = ZonedDateTime.now();
        generateID();
    }

    public Event(String name, EventType eventType) {
        this.name = name;
        date = ZonedDateTime.now();
        this.eventType = eventType;
        generateID();
    }

    public Event(long id, String name, ZonedDateTime date, EventType eventType) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.eventType = eventType;
        if (id > j)
            j = id + 1;
    }

    private void generateID() {
        j += 1;
        id = j;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public EventType getType() {
        return eventType;
    }
}
