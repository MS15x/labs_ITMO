package lab5;

import java.time.ZonedDateTime;

/**
 * Класс события
 */
class Event {
    Event() {
        name = "No_name";
        date = ZonedDateTime.now();
        j += 1;
        id = j;
    }

    void setId(long id) {
        this.id = id;
        if (id > j) {
            j = id + 1;
        }
    }

    void setName(String name) {
        this.name = name;
    }

    void setDate(ZonedDateTime date) {
        this.date = date;
    }

    void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    long getId() {
        return id;
    }

    String getName() {
        return name;
    }

    ZonedDateTime getDate() {
        return date;
    }

    EventType getType() {
        return eventType;
        //return eventType == null ? "" : String.valueOf(eventType);
    }

    private static long j;
    private long id;
    private String name;
    private ZonedDateTime date;
    private EventType eventType;
}
