package lab_8.client;

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
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.io.IOException;
import java.time.LocalDate;

import lab_8.common.AllCommands;
import lab_8.common.ServerCommand;
import lab_8.common.ticket.EventType;
import lab_8.common.ticket.TicketType;

public class MainController {

    private HashMap<String, Color> colors = new HashMap<>();

    private ObservableList<TicketView> tickets = FXCollections.observableArrayList();
    @FXML
    private Button add_button;
    @FXML
    private Label name_label;
    @FXML
    public Label info_label;
    @FXML
    private TableView<TicketView> ticket_table;
    @FXML
    private Canvas main_canvas;

    @FXML
    private void addButtonClick() throws IOException {
        Stage add_stage = new Stage();
        add_stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../client/AddWindow.fxml"))));
        add_stage.setTitle("1Xбилетик-777");
        add_stage.getIcons().add(new Image("file:sources/ticket.png"));
        add_stage.setResizable(false);
        add_stage.showAndWait();
        if (AddController.getCommand() != null)
            table_update(AddController.getCommand());
    }

    @FXML
    private void deleteButtonClick() {
        table_update(Client.connect(new ServerCommand(AllCommands.remove_by_id,
                ticket_table.getSelectionModel().getSelectedItem().getId())));
    }

    @FXML
    private void clearButtonClick() {
        table_update(Client.connect(new ServerCommand(AllCommands.clear)));
    }

    @FXML
    private void initialize() {

        ArrayList<String> serverTable = Client.connect(new ServerCommand(AllCommands.show)).getInformation();
        serverTable.remove(0);
        for (int i = 0; i < serverTable.size() / 11; i++)
            tickets.add(new TicketView(serverTable.subList(i * 11, (i + 1) * 11)));

        name_label.setText("Пользователь: " + ClientMain.getUser().getUser());
        table_creation();
        draw();

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(2000),
                ae -> {
                    ServerCommand command = Client.receive(true);
                    if (command != null)
                        table_update(command);
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    double x_max, y_max, x_min, y_min;

    public void draw() {
        double delta = 50;
        x_max = tickets.stream().map(a -> Double.valueOf(a.getTicketX())).max(Double::compareTo).get();
        y_max = tickets.stream().map(a -> Double.valueOf(a.getTicketY())).max(Double::compareTo).get();
        x_min = tickets.stream().map(a -> Double.valueOf(a.getTicketX())).min(Double::compareTo).get();
        y_min = tickets.stream().map(a -> Double.valueOf(a.getTicketY())).min(Double::compareTo).get();
        main_canvas.setWidth(x_max - x_min + delta);
        main_canvas.setHeight(y_max - y_min + delta);
        for (TicketView t : tickets)
            colors.put(t.getUserName(), random_color());
        GraphicsContext gc = main_canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, main_canvas.getWidth(), main_canvas.getHeight());
        for (TicketView ticket : tickets) {
            ticket.setColor(colors.get(ticket.getUserName()));
        //    ticket.draw(gc);
        }

        gc.translate(-x_min, -y_min);
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double x_min_e = tickets.stream().map(a -> Double.valueOf(a.getTicketX())).min(Double::compareTo).get(),
                        y_min_e = tickets.stream().map(a -> Double.valueOf(a.getTicketY())).min(Double::compareTo).get();
                gc.translate(x_min - x_min_e, y_min - y_min_e);
                x_min = x_min_e;
                y_min = y_min_e;
                gc.clearRect(x_min, y_min, x_max - x_min + delta, y_max - y_min + delta);
                for (TicketView ticket : tickets) {
                    ticket.update(now);
                    if (Double.parseDouble(ticket.getTicketX()) > x_max) {
                        x_max = Double.parseDouble(ticket.getTicketX());
                        main_canvas.setWidth(x_max - x_min + delta);
                    }
                    if (Double.parseDouble(ticket.getTicketY()) > y_max) {
                        main_canvas.setHeight(y_max - y_min + delta);
                        y_max = Double.parseDouble(ticket.getTicketY());
                    }
                    ticket.draw(gc);
                }
            }
        };
        animationTimer.start();
    }

    private Color random_color() {
        Random rand = new Random();
        return Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
    }

    //////////////////////////////////////
    public void table_update(ServerCommand command) {
        ArrayList<String> serverInfo = command.getInformation();
        if (serverInfo.get(0) != null)
            info_label.setText(serverInfo.get(0));
        serverInfo.remove(0);
        if (command.getCommand() == AllCommands.add)
            tickets.add(new TicketView(serverInfo));
        else if (command.getCommand() == AllCommands.remove_by_id |
                command.getCommand() == AllCommands.clear) {
            List<TicketView> found_tickets = tickets.stream().filter(
                    a -> serverInfo.contains(a.getId())).collect(Collectors.toList());
            tickets.removeAll(found_tickets);
        } else if (command.getCommand() == AllCommands.update) {
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
        }
    }

    private void table_creation() {
        TableColumn<TicketView, String> userNameCol = new TableColumn<>("Пользователь");
        TableColumn<TicketView, String> ticketDateCol = new TableColumn<>("Дата");
        TableColumn<TicketView, String> ticketCol = new TableColumn<>("Билет");
        TableColumn<TicketView, String> ticketNameCol = new TableColumn<>("Название");
        TableColumn<TicketView, String> ticketXCol = new TableColumn<>("X");
        TableColumn<TicketView, String> ticketYCol = new TableColumn<>("Y");
        TableColumn<TicketView, String> ticketPriceCol = new TableColumn<>("Цена");
        TableColumn<TicketView, String> ticketTypeCol = new TableColumn<>("Тип");
        TableColumn<TicketView, String> eventCol = new TableColumn<>("Событие");
        TableColumn<TicketView, String> eventNameCol = new TableColumn<>("Название");
        TableColumn<TicketView, String> eventTypeCol = new TableColumn<>("Тип");
        TableColumn<TicketView, String> eventDateCol = new TableColumn<>("Дата создания");
        eventCol.getColumns().addAll(eventNameCol, eventTypeCol, eventDateCol);
        ticketCol.getColumns().addAll(ticketNameCol, ticketXCol, ticketYCol, ticketPriceCol, ticketTypeCol);
        ticket_table.getColumns().addAll(userNameCol, ticketDateCol, ticketCol, eventCol);

        ticketTypeEdit(ticketTypeCol);
        eventTypeEdit(eventTypeCol);
        ticketNameEdit(ticketNameCol);
        ticketXEdit(ticketXCol);
        ticketYEdit(ticketYCol);
        ticketPriceEdit(ticketPriceCol);
        ticketDateEdit(ticketDateCol);
        eventNameEdit(eventNameCol);

        ticket_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        eventDateCol.setMinWidth(60);

        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        eventDateCol.setCellValueFactory(new PropertyValueFactory<>("eventDate"));

        ticket_table.setItems(tickets);
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
            //
            editEvent.getTableView().refresh();
            //
            table_update(Client.connect(new ServerCommand(AllCommands.update, person.getId(), "8", newType)));
        });
    }

    private void ticketDateEdit(TableColumn<TicketView, String> ticketDateCol) {
        ticketDateCol.setCellFactory(new Callback<TableColumn<TicketView, String>, TableCell<TicketView, String>>() {
            @Override
            public TableCell<TicketView, String> call(TableColumn<TicketView, String> param) {
                class DatePickerCell extends TableCell<TicketView, String> {
                    private DatePicker datePicker = new DatePicker();
                    private ObservableList<TicketView> tickets;

                    public DatePickerCell(ObservableList<TicketView> tickets) {
                        super();
                        this.tickets = tickets;
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
                            if (!tickets.get(getIndex()).getUserName().equals(ClientMain.getUser().getUser())) {
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
                                table_update(Client.connect(new ServerCommand(AllCommands.update, tickets.get(getIndex()).getId(), "2", String.valueOf(date))));
                        });
                    }
                }
                return new DatePickerCell(tickets);
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
            table_update(Client.connect(new ServerCommand(AllCommands.update, person.getId(), "3", newType)));
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
            table_update(Client.connect(new ServerCommand(AllCommands.update, person.getId(), "4", newType)));
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
            table_update(Client.connect(new ServerCommand(AllCommands.update, person.getId(), "5", newType)));
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
            table_update(Client.connect(new ServerCommand(AllCommands.update, person.getId(), "6", newType)));
        });
    }

    private void ticketTypeEdit(TableColumn<TicketView, String> ticketType) {
        ObservableList<String> ticketTypeList = FXCollections.observableArrayList();
        for (TicketType type : TicketType.values())
            ticketTypeList.add(type.toString());
        ticketTypeList.add("Без типа");

        ticketType.setCellValueFactory(param -> {
            String type = param.getValue().getTicketType();
            if (type.equals(""))
                type = "Без типа";
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
            if (newType.equals("Без типа"))
                newType = "";
            table_update(Client.connect(new ServerCommand(AllCommands.update, person.getId(), "7", newType)));
        });
    }

    private void eventTypeEdit(TableColumn<TicketView, String> eventType) {
        ObservableList<String> eventTypeList = FXCollections.observableArrayList();
        for (EventType type : EventType.values())
            eventTypeList.add(type.toString());
        eventTypeList.add("Без типа");

        eventType.setCellValueFactory(param -> {
            if (!param.getValue().getEventName().equals("")) {
                String type = param.getValue().getEventType();
                if (type.equals(""))
                    type = "Без типа";
                return new SimpleObjectProperty<>(type);
            } else return null;
        });

        eventType.setCellFactory(new Callback<TableColumn<TicketView, String>, TableCell<TicketView, String>>() {
            @Override
            public TableCell<TicketView, String> call(TableColumn<TicketView, String> paramTableColumn) {
                return new ComboBoxTableCell<TicketView, String>(eventTypeList) {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        TableRow row = getTableRow();
                        if (row != null & !empty & item != null) {
                            TicketView arg0 = (TicketView) row.getItem();
                            if (arg0 == null || arg0.getEventName().equals("")) {
                                //setEditable(false);
                                //setDisable(true);
                                //setVisible(false);
                            } else {
                                // setEditable(true);
                                // setDisable(false);
                                // setVisible(true);
                            }
                        }
                        super.updateItem(item, empty);
                    }

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
            if (newType.equals("Без типа"))
                newType = "";
            table_update(Client.connect(new ServerCommand(AllCommands.update, person.getId(), "9", newType)));
        });
    }
}