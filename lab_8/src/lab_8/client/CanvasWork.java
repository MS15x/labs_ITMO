package lab_8.client;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

/**
 * Определяет поведение полотна для рисования
 */
public class CanvasWork {
    private final double height_base = 720 + 40, width_base = 900 + 100;
    private double x_angle, y_angle, height, width;

    /**
     * @param canvas       канва для рисования
     * @param zoom         увеличение
     * @param tickets      коллекция билетов
     * @param ticket_table таблица, в которую необходимо передать данные из канвы
     */
    public CanvasWork(Canvas canvas,
                      ReadOnlyObjectProperty<Double> zoom,
                      final ObservableList<TicketView> tickets,
                      TableView<TicketView> ticket_table) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        zoom.addListener((observable, oldValue, newValue) -> {
            width = width_base * zoom.get();
            height = height_base * zoom.get();
        });
        canvas.setWidth(width);
        canvas.setHeight(height);
        canvas.setOnMouseClicked(event -> {
            double eventX = event.getX() / zoom.getValue(),
                    eventY = event.getY() / zoom.getValue();
            TicketView clickedTicket = tickets.stream().filter(a -> Math.pow(a.getX() + width / 2 - eventX, 2) +
                    Math.pow(a.getY() + height / 2 - eventY, 2) < Math.pow(a.getR(), 2)).min((a, b) -> {
                        if (Math.pow(a.getX() - eventX, 2) + Math.pow(a.getY() - eventY, 2) <
                                Math.pow(b.getX() - eventX, 2) + Math.pow(b.getY() - eventY, 2))
                            return -1;
                        else
                            return 1;
                    }
            ).orElse(null);
            if (clickedTicket != null)
                for (int i = 0; i < ticket_table.getItems().size(); i++)
                    if (ticket_table.getItems().get(i).getId().equals(clickedTicket.getId()))
                        ticket_table.getSelectionModel().clearAndSelect(i);
        });

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.setTransform(zoom.get(), 0, 0, zoom.get(), 0, 0);
                canvas.setWidth(width);
                canvas.setHeight(height);
                table_draw(gc);
                for (TicketView ticket : tickets) {
                    ticket.update(now);
                    ticket.draw(gc, width / 2, height / 2);
                }
                ruler_draw(gc);
            }
        };
        animationTimer.start();
    }

    private void ruler_draw(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        double y_border = 15;
        gc.fillRect(x_angle, y_angle, width, y_border);
        gc.setFill(Color.BLACK);
        for (int i = 50, i1 = -9; i <= width; i += 50, i1++) {
            gc.strokeLine(i, y_angle, i, y_angle + y_border);
            gc.fillText((i1 < 0 ? "-" : "") + (i1 == 0 ? "0" :
                    (Math.abs(i1) == 1 ? "10" : ("10^" + Math.abs(i1)))), i + 2, y_angle + 12);
        }

        gc.setFill(Color.YELLOW);
        double x_border = 42;
        gc.fillRect(x_angle, y_angle, x_border, height);
        gc.setFill(Color.BLACK);
        for (int j = 20, j1 = -18; j <= height; j += 20, j1++) {
            gc.strokeLine(x_angle, j, x_angle + x_border, j);
            gc.fillText((j1 > 0 ? "-" : "") + (j1 == 0 ? "0" :
                    (Math.abs(j1) == 1 ? "10" : ("10^" + Math.abs(j1)))), x_angle, j - 2);
        }

        gc.setFill(Color.WHITE);
        gc.fillRect(x_angle, y_angle, x_border, y_border);
    }

    private void table_draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);
        gc.setFill(Color.BLACK);
        for (int j = 20, j1 = -18; j <= height; j += 20, j1++)
            gc.strokeLine(0, j, width, j);
        for (int i = 50, i1 = -9; i <= width; i += 50, i1++)
            gc.strokeLine(i, 0, i, height);
    }

    /**
     * Устанавливает горизонтальное положение рамки с рулеткой
     *
     * @param x_angle x-координата видимой части канвы
     */
    public void setX_angle(double x_angle) {
        this.x_angle = x_angle < 0 ? 0 : x_angle;
    }

    /**
     * Устанавливает вертикальное положение рамки с рулеткой
     *
     * @param y_angle y-координата видимой части канвы
     */
    public void setY_angle(double y_angle) {
        this.y_angle = y_angle < 0 ? 0 : y_angle;
    }
}
