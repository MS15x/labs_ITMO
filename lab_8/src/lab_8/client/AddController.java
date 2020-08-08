package lab_8.client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lab_8.common.AllCommands;
import lab_8.common.ServerCommand;
import lab_8.common.ticket.EventType;
import lab_8.common.ticket.TicketType;

public class AddController {

    private static ServerCommand command;
    @FXML
    private Label info_label;
    @FXML
    private Button exit_button;
    @FXML
    private Button add_button;
    @FXML
    private TextField event_field;
    @FXML
    private TextField price_field;
    @FXML
    private TextField y_field;
    @FXML
    private TextField x_field;
    @FXML
    private DatePicker date_field;
    @FXML
    private TextField ticket_field;
    @FXML
    private ComboBox<String> event_type_field;
    @FXML
    private ComboBox<String> ticket_type_field;

    @FXML
    private void initialize() {
        for (TicketType type : TicketType.values())
            ticket_type_field.getItems().add(type.toString());
        for (EventType type : EventType.values())
            event_type_field.getItems().add(type.toString());
        ticket_type_field.getItems().add("Без типа");
        event_type_field.getItems().add("Без типа");
        ticket_type_field.getSelectionModel().selectLast();
        event_type_field.getSelectionModel().selectLast();

        x_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(Check.checkFloat(newValue) | newValue.isEmpty()) | newValue.length() > 8)
                x_field.setText(oldValue);
        });
        y_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(Check.checkLong(newValue) | newValue.isEmpty()))
                y_field.setText(oldValue);
        });
        price_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!(Check.checkInt(newValue) | newValue.isEmpty()))
                price_field.setText(oldValue);
        });
    }

    @FXML
    private void exitButtonPress() {
        Stage stage = (Stage) exit_button.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void addButtonPress() {
        if (ticket_field.getText().isEmpty() |
                date_field.getValue() == null |
                x_field.getText().isEmpty() |
                y_field.getText().isEmpty() |
                price_field.getText().isEmpty()) {
            info_label.setText("Заполните все обязательные поля помеченные *");
        } else {
            command = Client.connect(new ServerCommand(AllCommands.add,
                    ticket_field.getText(),
                    date_field.getValue().toString(),
                    x_field.getText(),
                    y_field.getText(),
                    price_field.getText(),
                    ticket_type_field.getValue().equals("Без типа") ? "" : ticket_type_field.getValue(),
                    event_field.getText(),
                    event_type_field.getValue().equals("Без типа") ? "" : event_type_field.getValue()));
            exitButtonPress();
        }
    }

    public static ServerCommand getCommand() {
        return command;
    }
}
