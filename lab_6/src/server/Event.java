package lab_6.server;

import lab_6.common.EventType;

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

    public Event() {
        name = "No_name";
        date = ZonedDateTime.now();
        generateID();
    }

    public Event(String name, EventType eventType) {
        this.name = name;
        date = ZonedDateTime.now();
        this.eventType = eventType;
        generateID();
    }

    private void generateID(){
        j += 1;
        id = j;
    }

    void setId(long id) {
        this.id = id;
        if (id > j) {
            j = id + 1;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    void setDate(ZonedDateTime date) {
        this.date = date;
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
