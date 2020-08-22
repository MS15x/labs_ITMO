package lab_8.client.windows;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.time.LocalDate;

import lab_8.client.*;
import lab_8.common.AllCommands;
import lab_8.common.ServerCommand;
import lab_8.common.ticket.EventType;
import lab_8.common.ticket.TicketType;

/**
 * Контролёр главного окна
 */
public class MainController {

    private HashMap<String, Color> colors = new HashMap<>();
    private ObservableList<TicketView> tickets = FXCollections.observableArrayList();

    @FXML
    private Button clear_button, delete_button, add_button;
    @FXML
    private Spinner<Double> zoom_spinner;
    @FXML
    private Label name_label, info_label, scale_label;
    @FXML
    private TableView<TicketView> ticket_table;
    @FXML
    private Canvas main_canvas;
    @FXML
    private ScrollPane canvas_pane;
    @FXML
    private ComboBox<String> language_box;
    private GraphicsContext gc;
    private ResourceFactory rf;

    @FXML
    private void addButtonClick() throws IOException {
        Stage add_stage = new Stage();
        add_stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("AddWindow.fxml"))));
        add_stage.setTitle("1Xбилетик-777");
        add_stage.getIcons().add(new Image("file:sources/ticket.png"));
        add_stage.setResizable(false);
        add_stage.showAndWait();
        if (AddController.getCommand() != null)
            update(AddController.getCommand());
    }

    @FXML
    private void deleteButtonClick() {
        update(ClientConnection.connect(new ServerCommand(AllCommands.remove_by_id,
                ticket_table.getSelectionModel().getSelectedItem().getId())));
    }

    @FXML
    private void clearButtonClick() {
        update(ClientConnection.connect(new ServerCommand(AllCommands.clear)));
    }

    @FXML
    private void initialize() {
        setLocale();

        add_button.textProperty().bind(rf.get("Create"));
        delete_button.textProperty().bind(rf.get("Delete"));
        clear_button.textProperty().bind(rf.get("Clear"));
        scale_label.textProperty().bind(rf.get("Scale"));

        gc = main_canvas.getGraphicsContext2D();
        main_canvas.setOnMouseClicked(event -> {
            double eventX = event.getX() / zoom_spinner.getValue(),
                    eventY = event.getY() / zoom_spinner.getValue();
            TicketView clickedTicket = tickets.stream().filter(a ->
                    Math.pow(a.getX() - eventX, 2) + Math.pow(a.getY() - eventY, 2) <
                            Math.pow(a.getR(), 2)).min((a, b) -> {
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

        zoom_spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 5, 1, 0.1));
        ArrayList<String> serverTable = ClientConnection.connect(new ServerCommand(AllCommands.show)).getInformation();
        serverTable.remove(0);
        for (int i = 0; i < serverTable.size() / 11; i++)
            tickets.add(new TicketView(serverTable.subList(i * 11, (i + 1) * 11)));

        name_label.textProperty().bind(rf.get("Username", ": " + ClientMain.getUser().getUser()));
        table_creation();
        draw();

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(2000),
                ae -> {
                    ServerCommand command = ClientConnection.receive(true);
                    if (command != null)
                        update(command);
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void setLocale() {
        Locale[] locales = {
                new Locale("ru", "RU"),
                new Locale("en", "CA"),
                new Locale("da", "DK"),
                new Locale("be", "BY")
        };
        rf = ResourceFactory.getInstance();
        rf.setResources(ResourceBundle.getBundle("lab_8.client.languages.MyLanguages", locales[0]));

        language_box.getItems().addAll("Русский", "English", "Беларускі", "Dansk");
        language_box.valueProperty().addListener((observable, oldValue, newValue) -> {
            int i = 0;
            switch (newValue) {
                case "Русский":
                    i = 0;
                    break;
                case "English":
                    i = 1;
                    break;
                case "Dansk":
                    i = 2;
                    break;
                case "Беларускі":
                    i = 3;
            }
            rf.setResources(ResourceBundle.getBundle("lab_8.client.languages.MyLanguages", locales[i]));
            ticket_table.refresh();
        });
    }

    private double x_max, y_max, x_min, y_min, delta = 50;

    private void draw() {
        x_max = tickets.stream().map(a -> Double.valueOf(a.getTicketX())).max(Double::compareTo).orElse(0.0);
        y_max = tickets.stream().map(a -> Double.valueOf(a.getTicketY())).max(Double::compareTo).orElse(0.0);
        x_min = tickets.stream().map(a -> Double.valueOf(a.getTicketX())).min(Double::compareTo).orElse(0.0);
        y_min = tickets.stream().map(a -> Double.valueOf(a.getTicketY())).min(Double::compareTo).orElse(0.0);

        main_canvas.setWidth(x_max - x_min + 2 * delta);
        main_canvas.setHeight(y_max - y_min + 2 * delta);

        for (TicketView t : tickets) {
            if (!colors.containsKey(t.getUserName()))
                colors.put(t.getUserName(), random_color());
            t.setColor(colors.get(t.getUserName()));
        }

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                TicketView.setX_min(tickets.stream().map(a -> Double.valueOf(a.getTicketX())).min(Double::compareTo).orElse(0.0) - delta);
                TicketView.setY_min(tickets.stream().map(a -> Double.valueOf(a.getTicketY())).min(Double::compareTo).orElse(0.0) - delta);

                gc.setTransform(zoom_spinner.getValue(), 0, 0, zoom_spinner.getValue(), 0, 0);
                table_draw(gc);

                for (TicketView ticket : tickets) {
                    ticket.update(now);
                    if (Double.parseDouble(ticket.getTicketX()) > x_max)
                        x_max = Double.parseDouble(ticket.getTicketX());
                    if (Double.parseDouble(ticket.getTicketY()) > y_max)
                        y_max = Double.parseDouble(ticket.getTicketY());
                    ticket.draw(gc);
                }
            }
        };
        animationTimer.start();
    }

    private void table_draw(GraphicsContext gc) {
        gc.clearRect(x_min - delta, y_min - delta,
                Math.max(canvas_pane.getWidth() + 2 * delta, x_max - x_min + 2 * delta),
                Math.max(canvas_pane.getHeight() + 2 * delta, y_max - y_min + 2 * delta));
        main_canvas.setWidth(Math.max(canvas_pane.getWidth() * zoom_spinner.getValue(), x_max - x_min + 2 * delta));
        main_canvas.setHeight(Math.max(canvas_pane.getHeight() * zoom_spinner.getValue(), y_max - y_min + 2 * delta));
        for (double i = x_min - delta; i <= Math.max(canvas_pane.getWidth(), x_max + 2 * delta); i += 10)
            gc.strokeLine(i, y_min - delta, i, Math.max(canvas_pane.getHeight(), y_max + 2 * delta));
        for (double j = y_min - delta; j <= Math.max(canvas_pane.getHeight(), y_max + 2 * delta); j += 10)
            gc.strokeLine(x_min - delta, j, Math.max(canvas_pane.getWidth(), x_max + 2 * delta), j);
    }

    private Color random_color() {
        Random rand = new Random();
        return Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    }

    private void update(ServerCommand command) {
        ArrayList<String> serverInfo = command.getInformation();
        if (serverInfo.get(0) != null && !serverInfo.get(0).equals(""))
            info_label.textProperty().bind(rf.get(serverInfo.get(0)));
        serverInfo.remove(0);

        switch (command.getCommand()) {
            case add:
                TicketView ticket0 = new TicketView(serverInfo);
                if (!colors.containsKey(ticket0.getUserName()))
                    colors.put(ticket0.getUserName(), random_color());
                ticket0.setColor(colors.get(ticket0.getUserName()));
                tickets.add(ticket0);
                break;
            case remove_by_id:
            case clear:
                List<TicketView> found_tickets = tickets.stream().filter(
                        a -> serverInfo.contains(a.getId())).collect(Collectors.toList());
                tickets.removeAll(found_tickets);
                break;
            case update:
                TicketView ticket = tickets.stream().filter(a -> a.getId().equals(command.getInformation().get(0))).findAny().orElse(null);
                if (ticket != null)
                    switch (Integer.parseInt(command.getInformation().get(1))) {
                        case 2:
                            ticket.setTicketDate(command.getInformation().get(2));
                            break;
                        case 3:
                            ticket.setTicketName(command.getInformation().get(2));
                            break;
                        case 4:
                            ticket.setTicketX(command.getInformation().get(2));
                            break;
                        case 5:
                            ticket.setTicketY(command.getInformation().get(2));
                            break;
                        case 6:
                            ticket.setTicketPrice(command.getInformation().get(2));
                            break;
                        case 7:
                            ticket.setTicketType(command.getInformation().get(2));
                            break;
                        case 8:
                            ticket.setEventName(command.getInformation().get(2));
                            ticket.setEventType(command.getInformation().get(3));
                            ticket.setEventDate(command.getInformation().get(4));
                            break;
                        case 9:
                            ticket.setEventType(command.getInformation().get(2));
                            break;
                        default:
                            break;
                    }
                break;
            default:
                break;
        }
    }

    private void table_creation() {
        TableColumn<TicketView, String> userNameCol = new TableColumn<>();
        userNameCol.textProperty().bind(rf.get("userNameCol"));
        TableColumn<TicketView, String> ticketDateCol = new TableColumn<>();
        ticketDateCol.textProperty().bind(rf.get("ticketDateCol"));
        TableColumn<TicketView, String> ticketCol = new TableColumn<>();
        ticketCol.textProperty().bind(rf.get("ticketCol"));
        TableColumn<TicketView, String> ticketNameCol = new TableColumn<>();
        ticketNameCol.textProperty().bind(rf.get("ticketNameCol"));
        TableColumn<TicketView, String> ticketXCol = new TableColumn<>("X");
        TableColumn<TicketView, String> ticketYCol = new TableColumn<>("Y");
        TableColumn<TicketView, String> ticketPriceCol = new TableColumn<>();
        ticketPriceCol.textProperty().bind(rf.get("ticketPriceCol"));
        TableColumn<TicketView, String> ticketTypeCol = new TableColumn<>();
        ticketTypeCol.textProperty().bind(rf.get("ticketTypeCol"));
        TableColumn<TicketView, String> eventCol = new TableColumn<>();
        eventCol.textProperty().bind(rf.get("eventCol"));
        TableColumn<TicketView, String> eventNameCol = new TableColumn<>();
        eventNameCol.textProperty().bind(rf.get("eventNameCol"));
        TableColumn<TicketView, String> eventTypeCol = new TableColumn<>();
        eventTypeCol.textProperty().bind(rf.get("eventTypeCol"));
        TableColumn<TicketView, String> eventDateCol = new TableColumn<>();
        eventDateCol.textProperty().bind(rf.get("eventDateCol"));
        eventCol.getColumns().addAll(Arrays.asList(eventNameCol, eventTypeCol, eventDateCol));
        ticketCol.getColumns().addAll(Arrays.asList(ticketNameCol, ticketXCol, ticketYCol, ticketPriceCol, ticketTypeCol));
        ticket_table.getColumns().addAll(Arrays.asList(userNameCol, ticketDateCol, ticketCol, eventCol));

        ticketTypeEdit(ticketTypeCol);
        eventTypeEdit(eventTypeCol);
        ticketNameEdit(ticketNameCol);
        ticketXEdit(ticketXCol);
        ticketYEdit(ticketYCol);
        ticketPriceEdit(ticketPriceCol);
        ticketDateEdit(ticketDateCol);
        eventNameEdit(eventNameCol);
        eventTime(eventDateCol);

        ticket_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        eventDateCol.setMinWidth(60);

        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

        ticket_table.setItems(tickets);
    }

    private void eventTime(TableColumn<TicketView, String> eventDateCol) {
        eventDateCol.setCellValueFactory(param -> {
            if (!param.getValue().getEventDate().equals("")) {
                ZonedDateTime time = ZonedDateTime.parse(param.getValue().getEventDate());
                time = time.withZoneSameInstant(ZoneId.of(rf.getString("time")));
                String h = String.valueOf(time.getHour()),
                        m = String.valueOf(time.getMinute()),
                        s = String.valueOf(time.getSecond());
                if (h.length() < 2)
                    h = "0" + h;
                if (m.length() < 2)
                    m = "0" + m;
                if (s.length() < 2)
                    s = "0" + s;
                return new SimpleObjectProperty<>(h + ":" + m + ":" + s);
            } else return new SimpleObjectProperty<>("");
        });
    }

    private void eventNameEdit(TableColumn<TicketView, String> eventNameCol) {
        eventNameCol.setCellFactory(col -> new TextFieldTableCell<TicketView, String>(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }) {

            @Override
            public void startEdit() {
                TableRow row = getTableRow();
                if (row != null && ((TicketView) row.getItem()).getUserName().equals(ClientMain.getUser().getUser()))
                    super.startEdit();
            }
        });
        eventNameCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        eventNameCol.setOnEditCommit((TableColumn.CellEditEvent<TicketView, String> editEvent) -> {
            String newType = editEvent.getNewValue();
            TicketView person = editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow());
            editEvent.getTableView().refresh();
            update(ClientConnection.connect(new ServerCommand(AllCommands.update, person.getId(), "8", newType)));
        });
    }

    private void ticketDateEdit(TableColumn<TicketView, String> ticketDateCol) {
        ticketDateCol.setCellFactory(new Callback<TableColumn<TicketView, String>, TableCell<TicketView, String>>() {
            @Override
            public TableCell<TicketView, String> call(TableColumn<TicketView, String> param) {

                class DatePickerCell extends TableCell<TicketView, String> {
                    private DatePicker datePicker = new DatePicker();

                    public DatePickerCell() {
                        super();
                        createDatePicker();
                        setGraphic(datePicker);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        Platform.runLater(() -> datePicker.requestFocus());
                    }

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setGraphic(null);
                        else {
                            setDatePikerDate(item);
                            if (!this.getTableView().getItems().get(getIndex()).getUserName().equals(ClientMain.getUser().getUser())) {
                                datePicker.setDisable(true);
                                datePicker.setStyle("-fx-opacity: 1");
                                datePicker.getEditor().setStyle("-fx-opacity: 1");
                            }
                            setGraphic(this.datePicker);
                        }
                    }

                    private void setDatePikerDate(String dateAsStr) {
                        int day, month, year;
                        day = month = year = 0;
                        try {
                            year = Integer.parseInt(dateAsStr.substring(0, 4));
                            month = Integer.parseInt(dateAsStr.substring(5, 7));
                            day = Integer.parseInt(dateAsStr.substring(8));
                        } catch (NumberFormatException ignored) {
                        }
                        datePicker.setValue(LocalDate.of(year, month, day));
                    }

                    private void createDatePicker() {
                        datePicker.setEditable(false);
                        datePicker.setOnAction(t -> {
                            LocalDate date = datePicker.getValue();
                            if (tickets.get(getIndex()).getUserName().equals(ClientMain.getUser().getUser()) &&
                                    !tickets.get(getIndex()).getTicketDate().equals(String.valueOf(date)))
                                update(ClientConnection.connect(new ServerCommand(AllCommands.update, tickets.get(getIndex()).getId(), "2", String.valueOf(date))));
                        });
                    }
                }
                return new DatePickerCell();
            }
        });
        ticketDateCol.setCellValueFactory(new PropertyValueFactory<>("ticketDate"));
    }

    private void ticketNameEdit(TableColumn<TicketView, String> ticketNameCol) {
        ticketNameCol.setCellFactory(col -> new TextFieldTableCell<TicketView, String>(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }) {

            @Override
            public void updateItem(String item, boolean empty) {
                TableRow row = getTableRow();
                if (row != null & !empty & item != null) {
                    TicketView arg0 = (TicketView) row.getItem();
                    if (!item.isEmpty()) {
                        super.updateItem(item, false);
                    } else
                        super.updateItem(arg0.getTicketName(), false);
                } else
                    super.updateItem(item, empty);
            }

            @Override
            public void startEdit() {
                TableRow row = getTableRow();
                if (row != null && ((TicketView) row.getItem()).getUserName().equals(ClientMain.getUser().getUser()))
                    super.startEdit();
            }
        });
        ticketNameCol.setCellValueFactory(new PropertyValueFactory<>("ticketName"));
        ticketNameCol.setOnEditCommit((TableColumn.CellEditEvent<TicketView, String> editEvent) -> {
            String newType = editEvent.getNewValue();
            TicketView person = editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow());
            update(ClientConnection.connect(new ServerCommand(AllCommands.update, person.getId(), "3", newType)));
        });
    }

    private void ticketXEdit(TableColumn<TicketView, String> ticketXCol) {
        ticketXCol.setCellFactory(col -> new TextFieldTableCell<TicketView, String>(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }) {
            @Override
            public void updateItem(String item, boolean empty) {
                TableRow row = getTableRow();
                if (row != null & !empty & item != null) {
                    TicketView arg0 = (TicketView) row.getItem();
                    if (!item.isEmpty() & Check.checkFloat(item))
                        super.updateItem(item, false);
                    else
                        super.updateItem(arg0.getTicketX(), false);
                } else
                    super.updateItem(item, empty);
            }

            @Override
            public void startEdit() {
                TableRow row = getTableRow();
                if (row != null && ((TicketView) row.getItem()).getUserName().equals(ClientMain.getUser().getUser()))
                    super.startEdit();
            }
        });
        ticketXCol.setCellValueFactory(new PropertyValueFactory<>("ticketX"));
        ticketXCol.setOnEditCommit((TableColumn.CellEditEvent<TicketView, String> editEvent) -> {
            String newType = editEvent.getNewValue();
            TicketView person = editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow());
            update(ClientConnection.connect(new ServerCommand(AllCommands.update, person.getId(), "4", newType)));
        });
    }

    private void ticketYEdit(TableColumn<TicketView, String> ticketYCol) {
        ticketYCol.setCellFactory(col -> new TextFieldTableCell<TicketView, String>(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }) {
            @Override
            public void updateItem(String item, boolean empty) {
                TableRow row = getTableRow();
                if (row != null & !empty & item != null) {
                    TicketView arg0 = (TicketView) row.getItem();
                    if (!item.isEmpty() & Check.checkLong(item)) {
                        super.updateItem(item, false);
                    } else
                        super.updateItem(arg0.getTicketY(), false);
                } else
                    super.updateItem(item, empty);
            }

            @Override
            public void startEdit() {
                TableRow row = getTableRow();
                if (row != null && ((TicketView) row.getItem()).getUserName().equals(ClientMain.getUser().getUser()))
                    super.startEdit();
            }
        });
        ticketYCol.setCellValueFactory(new PropertyValueFactory<>("ticketY"));
        ticketYCol.setOnEditCommit((TableColumn.CellEditEvent<TicketView, String> editEvent) -> {
            String newType = editEvent.getNewValue();
            TicketView person = editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow());
            update(ClientConnection.connect(new ServerCommand(AllCommands.update, person.getId(), "5", newType)));
        });
    }

    private void ticketPriceEdit(TableColumn<TicketView, String> ticketPriceCol) {
        ticketPriceCol.setCellFactory(col -> new TextFieldTableCell<TicketView, String>(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }) {
            @Override
            public void updateItem(String item, boolean empty) {
                TableRow row = getTableRow();
                if (row != null & !empty & item != null) {
                    TicketView arg0 = (TicketView) row.getItem();
                    if (!item.isEmpty() & Check.checkInt(item)) {
                        super.updateItem(item, false);
                    } else
                        super.updateItem(arg0.getTicketPrice(), false);
                } else
                    super.updateItem(item, empty);
            }

            @Override
            public void startEdit() {
                TableRow row = getTableRow();
                if (row != null && ((TicketView) row.getItem()).getUserName().equals(ClientMain.getUser().getUser()))
                    super.startEdit();
            }
        });
        ticketPriceCol.setCellValueFactory(new PropertyValueFactory<>("ticketPrice"));
        ticketPriceCol.setOnEditCommit((TableColumn.CellEditEvent<TicketView, String> editEvent) -> {
            String newType = editEvent.getNewValue();
            TicketView person = editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow());
            update(ClientConnection.connect(new ServerCommand(AllCommands.update, person.getId(), "6", newType)));
        });
    }

    private void ticketTypeEdit(TableColumn<TicketView, String> ticketType) {
        ObservableList<String> ticketTypeList = FXCollections.observableArrayList();
        for (TicketType type : TicketType.values())
            ticketTypeList.add(type.toString());
        ticketTypeList.add("—");

        ticketType.setCellValueFactory(param -> {
            String type = param.getValue().getTicketType();
            if (type.equals(""))
                type = "—";
            return new SimpleObjectProperty<>(type);
        });

        ticketType.setCellFactory(new Callback<TableColumn<TicketView, String>, TableCell<TicketView, String>>() {
            @Override
            public TableCell<TicketView, String> call(TableColumn<TicketView, String> param) {
                return new ComboBoxTableCell<TicketView, String>(ticketTypeList) {

                    @Override
                    public void startEdit() {
                        TableRow row = getTableRow();
                        if (row != null && ((TicketView) row.getItem()).getUserName().equals(ClientMain.getUser().getUser()))
                            super.startEdit();
                    }
                };
            }
        });

        ticketType.setOnEditCommit((TableColumn.CellEditEvent<TicketView, String> editEvent) -> {
            String newType = editEvent.getNewValue();
            TicketView person = editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow());
            if (newType.equals("—"))
                newType = "";
            update(ClientConnection.connect(new ServerCommand(AllCommands.update, person.getId(), "7", newType)));
        });
    }

    private void eventTypeEdit(TableColumn<TicketView, String> eventType) {
        ObservableList<String> eventTypeList = FXCollections.observableArrayList();
        for (EventType type : EventType.values())
            eventTypeList.add(type.toString());
        eventTypeList.add("—");

        eventType.setCellValueFactory(param -> {
            if (!param.getValue().getEventName().equals("")) {
                String type = param.getValue().getEventType();
                if (type.equals(""))
                    type = "—";
                return new SimpleObjectProperty<>(type);
            } else return null;
        });

        eventType.setCellFactory(new Callback<TableColumn<TicketView, String>, TableCell<TicketView, String>>() {
            @Override
            public TableCell<TicketView, String> call(TableColumn<TicketView, String> paramTableColumn) {
                return new ComboBoxTableCell<TicketView, String>(eventTypeList) {
                    @Override
                    public void startEdit() {
                        TableRow row = getTableRow();
                        if (row != null && ((TicketView) row.getItem()).getUserName().equals(ClientMain.getUser().getUser()))
                            if (!((TicketView) row.getItem()).getEventName().equals("")) {
                                setVisible(true);
                                super.startEdit();
                            } else
                                setVisible(false);
                    }
                };
            }
        });

        eventType.setOnEditCommit((TableColumn.CellEditEvent<TicketView, String> editEvent) -> {
            String newType = editEvent.getNewValue();
            TicketView person = editEvent.getTableView().getItems().get(editEvent.getTablePosition().getRow());
            if (newType.equals("—"))
                newType = "";
            update(ClientConnection.connect(new ServerCommand(AllCommands.update, person.getId(), "9", newType)));
        });
    }
}