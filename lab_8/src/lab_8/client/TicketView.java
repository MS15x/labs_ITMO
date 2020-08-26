package lab_8.client;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;

/**
 * Содержит данные, необходимые для отображения билета в таблице и графическом поле
 */
public class TicketView {
    private Color color;
    private double r, x, y, r_new, x_new, y_new,
            x_last, y_last, v, t_last, t_last_r;

    private String id;
    private SimpleStringProperty userName, ticketDate,
            ticketName, ticketX, ticketY, ticketPrice,
            ticketType, eventName, eventType, eventDate;
    private static HashMap<String, Color> colors = new HashMap<>();

    /**
     * Обновление данных, необходимых для прорисовки элемента на канве
     *
     * @param now время в наносекундах
     */
    public void update(long now) {
        double t = ((double) now) / 1000000000;
        if (sqrt(pow(x_new - x, 2) + pow(y_new - y, 2)) > 2) {
            final double alpha = atan((y_new - y_last) / (x_new - x_last)),
                    x_new_n = x_new * cos(alpha) + y_new * sin(alpha),
                    x_last_n = x_last * cos(alpha) + y_last * sin(alpha),
                    y_last_n = y_last * cos(alpha) - x_last * sin(alpha),
                    g = 20;
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
    }

    /**
     * Отрисовка элемента на канве
     *
     * @param gc      GraphicsContext канвы
     * @param delta_x смещение элемента по x
     * @param delta_y смещение элемента по y
     */
    public void draw(GraphicsContext gc, double delta_x, double delta_y) {
        gc.setFill(color);
        gc.fillOval(x - r + delta_x, y - r + delta_y, 2 * r, 2 * r);
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
        if (!colors.containsKey(userName.get()))
            colors.put(userName.get(), random_color());
        this.color = colors.get(userName.get());

        double i = Double.parseDouble(ticketX.get()),
                j = Double.parseDouble(ticketY.get());
        x_new = x_last = x = signum(i) * log10(1 + abs(i)) * 50;
        y_new = y_last = y = -signum(j) * log10(1 + abs(j)) * 20;
        r = r_new = log1p(Double.parseDouble(ticketPrice.get()));
    }

    private Color random_color() {
        Random rand = new Random();
        return Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
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
        double i = Double.parseDouble(this.ticketX.get());
        x_new = signum(i) * log10(1 + abs(i)) * 50;
        v = 0;
    }

    public String getTicketX() {
        return ticketX.get();
    }

    public void setTicketY(String ticketY) {
        this.ticketY.set(ticketY);
        x_last = x;
        y_last = y;
        double j = Double.parseDouble(this.ticketY.get());
        y_new = -signum(j) * log10(1 + abs(j)) * 20;
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
}
