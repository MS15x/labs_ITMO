package lab_8.client;

import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class TicketView {
    private String id;
    private SimpleStringProperty userName,
            ticketDate,
            ticketName,
            ticketX,
            ticketY,
            ticketPrice,
            ticketType,
            eventName,
            eventType,
            eventDate;

    TicketView(List<String> all) {
        id = all.get(0);
        userName = new SimpleStringProperty(all.get(1));
        ticketDate = new SimpleStringProperty(all.get(2));
        ticketName = new SimpleStringProperty(all.get(3));
        ticketX = new SimpleStringProperty(all.get(4));
        ticketY = new SimpleStringProperty(all.get(5));
        ticketPrice = new SimpleStringProperty(all.get(6));
        ticketType = new SimpleStringProperty(all.get(7));
        eventName = new SimpleStringProperty(all.get(8));
        eventType = new SimpleStringProperty(all.get(9));
        eventDate = new SimpleStringProperty(all.get(10));
    }

    public String  getId() {
        return id;
    }

    public String getUserName() {
        return userName.get();
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public void setTicketDate(String ticketDate) {
        this.ticketDate.set(ticketDate);
    }

    public String getTicketDate() {
        return ticketDate.get();
    }

    public void setEventDate(String eventDate) {
        this.eventDate.set(eventDate);
    }

    public String getEventDate() {
        return eventDate.get();
    }

    public void setEventName(String eventName) {
        this.eventName.set(eventName);
    }

    public String getEventName() {
        return eventName.get();
    }

    public void setEventType(String eventType) {
        this.eventType.set(eventType);
    }

    public String getEventType() {
        return eventType.get();
    }

    public void setTicketName(String ticketName) {
        this.ticketName.set(ticketName);
    }

    public String getTicketName() {
        return ticketName.get();
    }

    public void setTicketPrice(String ticketPrice) {
        this.ticketPrice.set(ticketPrice);
    }

    public String getTicketPrice() {
        return ticketPrice.get();
    }

    public void setTicketType(String ticketType) {
        this.ticketType.set(ticketType);
    }

    public String getTicketType() {
        return ticketType.get();
    }

    public void setTicketX(String ticketX) {
        this.ticketX.set(ticketX);
    }

    public String getTicketX() {
        return ticketX.get();
    }

    public void setTicketY(String ticketY) {
        this.ticketY.set(ticketY);
    }

    public String getTicketY() {
        return ticketY.get();
    }
}
