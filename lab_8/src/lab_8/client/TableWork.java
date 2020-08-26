package lab_8.client;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import lab_8.client.service.Check;
import lab_8.client.service.ResourceFactory;
import lab_8.common.AllCommands;
import lab_8.common.ServerCommand;
import lab_8.server.ticket.EventType;
import lab_8.server.ticket.TicketType;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;

/**
 * Определяет поведение таблицы
 */
public class TableWork {
    private ResourceFactory rf = ResourceFactory.getInstance();
    private final ObservableList<TicketView> tickets;
    private CollectionUpdate collectionUpdate;

    /**
     *
     * @param ticket_table     таблица, поведение которой необходимо определить
     * @param tickets          коллекция билетов для использования в таблице
     * @param collectionUpdate класс, которому необходимо передавать сообщения по обновлению коллекции
     */
    public TableWork(TableView<TicketView> ticket_table,
                     ObservableList<TicketView> tickets,
                     final CollectionUpdate collectionUpdate) {
        this.tickets = tickets;
        this.collectionUpdate = collectionUpdate;

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
                return new SimpleObjectProperty<>(time.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)));
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
            collectionUpdate.update(new ServerCommand(AllCommands.update, person.getId(), "8", newType), true);
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
                                collectionUpdate.update(new ServerCommand(AllCommands.update, tickets.get(getIndex()).getId(), "2", String.valueOf(date)), true);
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
            collectionUpdate.update(new ServerCommand(AllCommands.update, person.getId(), "3", newType), true);
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
            if (Check.checkFloat(newType))
                collectionUpdate.update(new ServerCommand(AllCommands.update, person.getId(), "4", newType), true);
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
            if (Check.checkLong(newType))
                collectionUpdate.update(new ServerCommand(AllCommands.update, person.getId(), "5", newType), true);
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
            if (Check.checkInt(newType))
                collectionUpdate.update(new ServerCommand(AllCommands.update, person.getId(), "6", newType), true);
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
            collectionUpdate.update(new ServerCommand(AllCommands.update, person.getId(), "7", newType), true);
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
            collectionUpdate.update(new ServerCommand(AllCommands.update, person.getId(), "9", newType), true);
        });
    }
}
