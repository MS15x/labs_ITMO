package lab_8.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lab_8.client.service.ClientIdentification;

/**
 * Главный класс приложения-клиента
 */
public class ClientMain extends Application {

    private static ClientIdentification clientIdentification = ClientIdentification.getInstance();

    public static void main(String[] args) {
        ClientMain.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage registry_stage = new Stage();
        registry_stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("windows/AuthorisationWindow.fxml"))));
        registry_stage.setTitle("1Xбилетик-777");
        registry_stage.getIcons().add(new Image("file:sources/ticket.png"));
        registry_stage.setResizable(false);
        registry_stage.setHeight(210);
        registry_stage.showAndWait();
        if (clientIdentification.getResult()) {
            primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("windows/MainWindow.fxml"))));
            primaryStage.setTitle("1Xбилетик-777");
            primaryStage.getIcons().add(new Image("file:sources/ticket.png"));
            primaryStage.show();
        }
    }

    /**
     * Необходим для передачи данных пользователя другим окнам приложения
     *
     * @return класс с данными пользователя
     */
    public static ClientIdentification getUser() {
        return clientIdentification;
    }
}
