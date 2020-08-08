package lab_8.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lab_8.common.ServerCommand;

public class AuthorisationController {

    private boolean newUser;
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

    @FXML
    private void onEnterClickMethod() {
        ClientIdentification clientIdentification;
        if (!newUser) {
            clientIdentification = ClientIdentification.getInstance(name_field.getText(), pass_field.getText());
            ServerCommand command = clientIdentification.authorization();
            if (command.getResult()) {
                Stage stage = (Stage) main_grid.getScene().getWindow();
                stage.close();
            } else
                info_label.setText(command.getInformation().get(0));
        } else {
            if (pass_field.getText().equals(pass_confirm_field.getText())) {
                clientIdentification = ClientIdentification.getInstance(name_field.getText(), pass_field.getText());
                info_label.setText(clientIdentification.registration().getInformation().get(0));
            } else
                info_label.setText("Введённые пароли не совпадают");
        }
    }

    @FXML
    private void onNewClickMethod() {
        if (!newUser) {
            pass_field.clear();
            name_field.clear();
            info_label.setText("");
            newUser = true;
            main_grid.getRowConstraints().get(4).setMaxHeight(Region.USE_COMPUTED_SIZE);
            main_grid.getRowConstraints().get(4).setPrefHeight(Region.USE_COMPUTED_SIZE);
            main_grid.getRowConstraints().get(4).setMinHeight(Region.USE_COMPUTED_SIZE);
            main_grid.getRowConstraints().get(5).setMaxHeight(Region.USE_COMPUTED_SIZE);
            pass_confirm_label.setVisible(true);
            pass_confirm_field.setVisible(true);
            user_label.setText("Имя нового пользователя");
            registry_button.setText("Авторизоваться");
            main_grid.getScene().getWindow().setHeight(245);
        } else {
            pass_field.clear();
            name_field.clear();
            pass_confirm_field.clear();
            info_label.setText("");
            newUser = false;
            main_grid.getRowConstraints().get(4).setMaxHeight(0);
            main_grid.getRowConstraints().get(4).setPrefHeight(0);
            main_grid.getRowConstraints().get(4).setMinHeight(0);
            main_grid.getRowConstraints().get(5).setMaxHeight(0);
            pass_confirm_label.setVisible(false);
            pass_confirm_field.setVisible(false);
            user_label.setText("Имя пользователя");
            registry_button.setText("Зарегистрироваться");
            main_grid.getScene().getWindow().setHeight(210);
        }
    }
}
