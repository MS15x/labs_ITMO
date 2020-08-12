package lab_8.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

import static java.lang.Math.*;

public class TicketView {
    private Color color;
    private double r;
    private double x;
    private double y;
    private double r_new;
    private double x_new;
    private double y_new;
    private double x_last;
    private double y_last;
    private double v;
    private double t;
    private double t_last;
    private int i;

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

    public void update(long now) {
        //x_new = Double.parseDouble(ticketX.get());
        //y_new = Double.parseDouble(ticketY.get());
        //r_new = Double.parseDouble(ticketPrice.get());

        t = ((double) now) / 1000000000;
        // if (//sqrt(pow(x_new - x, 2) + pow(y_new - y, 2)) > 2 &&
        //         x_new == Double.parseDouble(ticketX.get()) &&
        //                 y_new == Double.parseDouble(ticketY.get())) {
        if (sqrt(pow(x_new - x, 2) + pow(y_new - y, 2)) > 2) {
            double alpha = atan((y_new - y_last) / (x_new - x_last)),
                    x_new_n = x_new * cos(alpha) + y_new * sin(alpha),
                    x_last_n = x_last * cos(alpha) + y_last * sin(alpha),
                    y_last_n = y_last * cos(alpha) - x_last * sin(alpha),
                    g = 15;
            if (v == 0) {
                v = signum(x_new_n - x_last_n) * sqrt(g * abs(x_new_n - x_last_n) / 2);
                t_last = t;
            }
            x = x_last_n + v * (t - t_last);
            y = y_last_n - abs(v * (t - t_last)) + g * pow(t - t_last, 2) / 2;

            double x_extra = x;
            x = x * cos(alpha) - y * sin(alpha);
            y = x_extra * sin(alpha) + y * cos(alpha);

        } else {
            x = x_new;
            y = y_new;
            v = 0;
        }
        // } else {
        //     x_last = x;
        //     y_last = y;
        //      v = 0;
        // }

        x_new = Double.parseDouble(ticketX.get());
        y_new = Double.parseDouble(ticketY.get());
        r_new = Double.parseDouble(ticketPrice.get());
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillOval(x - r, y - r, 2 * r, 2 * r);
    }

    public void setColor(Color color) {
        this.color = color;
    }

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

        x_last = x = Double.parseDouble(ticketX.get());
        y_last = y = Double.parseDouble(ticketY.get());
        r = Double.parseDouble(ticketPrice.get());
    }

    public String getId() {
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
        x_last = x;
        y_last = y;
        x_new = Double.parseDouble(this.ticketX.get());
        v = 0;
    }

    public String getTicketX() {
        return ticketX.get();
    }

    public void setTicketY(String ticketY) {
        this.ticketY.set(ticketY);
        x_last = x;
        y_last = y;
        y_new = Double.parseDouble(this.ticketY.get());
        v = 0;
    }

    public String getTicketY() {
        return ticketY.get();
    }
}
