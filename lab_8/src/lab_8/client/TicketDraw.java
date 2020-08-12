package lab_8.client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TicketDraw {
    private Color color;
    private double r, x, y;
    private long time;

    public TicketDraw(double r, double x, double y, Color color) {
        this.r = r;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public void update(long time, double x_new, double y_new, double r_new) {
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillOval(x - r, y - r, 2 * r, 2 * r);
    }

}
