package lab_8.client.windows;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lab_8.client.Check;
import lab_8.client.ClientConnection;
import lab_8.client.ResourceFactory;
import lab_8.common.AllCommands;
import lab_8.common.ServerCommand;
import lab_8.common.ticket.EventType;
import lab_8.common.ticket.TicketType;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Контролёр окна добавления элемента
 */
public class AddController {

    private static ServerCommand command;
    @FXML
    private ComboBox<String> language_box;
    @FXML
    private Label name_label, date_label, price_label, ticket_type_label,
            event_type_label, event_name_label, info_label;
    @FXML
    private Button exit_button, add_button;
    @FXML
    private TextField event_field, price_field, y_field, x_field, ticket_field;
    @FXML
    private DatePicker date_field;
    @FXML
    private ComboBox<String> event_type_field, ticket_type_field;
    private ResourceFactory rf;

    @FXML
    private void initialize() {
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
        });

        name_label.textProperty().bind(rf.get("ticketNameCol", ": *"));
        date_label.textProperty().bind(rf.get("ticketDateCol", ": *"));
        price_label.textProperty().bind(rf.get("ticketPriceCol", ": *"));
        ticket_type_label.textProperty().bind(rf.get("ticketTypeCol"));
        event_type_label.textProperty().bind(rf.get("eventTypeCol"));
        event_name_label.textProperty().bind(rf.get("eventNameCol"));
        add_button.textProperty().bind(rf.get("Add"));
        exit_button.textProperty().bind(rf.get("Exit"));


        for (TicketType type : TicketType.values())
            ticket_type_field.getItems().add(type.toString());
        for (EventType type : EventType.values())
            event_type_field.getItems().add(type.toString());
        ticket_type_field.getItems().add("—");
        event_type_field.getItems().add("—");
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
            info_label.textProperty().bind(rf.get("Ans-103"));
        } else {
            command = ClientConnection.connect(new ServerCommand(AllCommands.add,
                    ticket_field.getText(),
                    date_field.getValue().toString(),
                    x_field.getText(),
                    y_field.getText(),
                    price_field.getText(),
                    ticket_type_field.getValue().equals("—") ? "" : ticket_type_field.getValue(),
                    event_field.getText(),
                    event_type_field.getValue().equals("—") ? "" : event_type_field.getValue()));
            exitButtonPress();
        }
    }

    /**
     * Возвращает команду, полученную от сервера, после добавления элемента от сервера
     * @return команда, полученная от сервера
     */
    public static ServerCommand getCommand() {
        return command;
    }
}
