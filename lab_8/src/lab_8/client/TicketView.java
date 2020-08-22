package lab_8.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

import static java.lang.Math.*;

/**
 * Содержит данные, необходимые для отображения билета в таблице и графическом поле
 */
public class TicketView {
    private static double x_min = 10000000, y_min = 10000000,
            x_max = -10000000, y_max = -10000000;
    private Color color;
    private double r, x, y, r_new, x_new, y_new,
            x_last, y_last, v, t_last, t_last_r;

    private String id;
    private SimpleStringProperty userName, ticketDate,
            ticketName, ticketX, ticketY, ticketPrice,
            ticketType, eventName, eventType, eventDate;

    /**
     * Обновление данных, необходимых для прорисовки элемента на канве
     *
     * @param now время в наносекундах
     */
    public void update(long now) {
        double t = ((double) now) / 1000000000;
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
        if (abs(r - r_new) > 1) {
            double vr = 8;
            r = r + signum(r_new - r) * vr * (t - t_last_r);
        } else
            r = r_new;
        t_last_r = t;

        x_new = Double.parseDouble(ticketX.get());
        y_new = Double.parseDouble(ticketY.get());
        r_new = log1p(Double.parseDouble(ticketPrice.get()));
    }

    /**
     * Отрисовка элемента на канве
     *
     * @param gc GraphicsContext канвы
     */
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillOval(x - x_min - r, y - y_min - r, 2 * r, 2 * r);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public TicketView(List<String> all) {
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
        r = r_new = log1p(Double.parseDouble(ticketPrice.get()));
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
        r_new = log1p(Double.parseDouble(this.ticketPrice.get()));
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getR() {
        return r;
    }

    public static double getX_min() {
        return x_min;
    }

    public static double getY_min() {
        return y_min;
    }

    public static void setX_min(double x_min) {
        TicketView.x_min = x_min;
    }

    public static void setY_min(double y_min) {
        TicketView.y_min = y_min;
    }
}
