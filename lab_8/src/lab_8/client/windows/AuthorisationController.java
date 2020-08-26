package lab_8.client.windows;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lab_8.client.service.ClientIdentification;
import lab_8.client.service.ResourceFactory;
import lab_8.common.ServerCommand;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Контролёр окна авторизации/регистрации
 */
public class AuthorisationController {

    @FXML
    private ComboBox<String> language_box;
    @FXML
    private Label pass_label;
    @FXML
    private Button enter_button;
    @FXML
    private TextField name_field;
    @FXML
    private PasswordField pass_field;
    @FXML
    private GridPane main_grid;
    @FXML
    private Label pass_confirm_label;
    @FXML
    private PasswordField pass_confirm_field;
    @FXML
    private Label user_label;
    @FXML
    private Label info_label;
    @FXML
    private Button registry_button;
    private boolean newUser;
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
        user_label.textProperty().bind(rf.get("Username"));
        pass_label.textProperty().bind(rf.get("Password"));
        enter_button.textProperty().bind(rf.get("Input"));
        registry_button.textProperty().bind(rf.get("Check in"));
        pass_confirm_label.textProperty().bind(rf.get("Password_conf"));
    }

    @FXML
    private void onEnterClickMethod() {
        ClientIdentification clientIdentification;
        if (!newUser) {
            clientIdentification = ClientIdentification.getInstance(name_field.getText(), pass_field.getText());
            ServerCommand command = clientIdentification.authorization();
            if (command.getResult()) {
                Stage stage = (Stage) main_grid.getScene().getWindow();
                clientIdentification.setResult(true);
                stage.close();
            } else info_label.textProperty().bind(rf.get(command.getInformation().get(0)));
        } else if (pass_field.getText().equals(pass_confirm_field.getText())) {
            clientIdentification = ClientIdentification.getInstance(name_field.getText(), pass_field.getText());
            info_label.textProperty().bind(rf.get(clientIdentification.registration().getInformation().get(0)));
        } else info_label.textProperty().bind(rf.get("Ans-102"));
    }

    @FXML
    private void onNewClickMethod() {
        if (!newUser) {
            pass_field.clear();
            name_field.clear();
            info_label.textProperty().unbind();
            info_label.setText("");
            newUser = true;
            main_grid.getRowConstraints().get(4).setMaxHeight(Region.USE_COMPUTED_SIZE);
            main_grid.getRowConstraints().get(4).setPrefHeight(Region.USE_COMPUTED_SIZE);
            main_grid.getRowConstraints().get(4).setMinHeight(Region.USE_COMPUTED_SIZE);
            main_grid.getRowConstraints().get(5).setMaxHeight(Region.USE_COMPUTED_SIZE);
            pass_confirm_label.setVisible(true);
            pass_confirm_field.setVisible(true);
            user_label.textProperty().bind(rf.get("New username"));
            registry_button.textProperty().bind(rf.get("Login"));
            main_grid.getScene().getWindow().setHeight(245);
        } else {
            pass_field.clear();
            name_field.clear();
            pass_confirm_field.clear();
            info_label.textProperty().unbind();
            info_label.setText("");
            newUser = false;
            main_grid.getRowConstraints().get(4).setMaxHeight(0);
            main_grid.getRowConstraints().get(4).setPrefHeight(0);
            main_grid.getRowConstraints().get(4).setMinHeight(0);
            main_grid.getRowConstraints().get(5).setMaxHeight(0);
            pass_confirm_label.setVisible(false);
            pass_confirm_field.setVisible(false);
            user_label.textProperty().bind(rf.get("Username"));
            registry_button.textProperty().bind(rf.get("Check in"));
            main_grid.getScene().getWindow().setHeight(210);
        }
    }
}
