package lab_8.client.windows;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.*;
import java.io.IOException;

import javafx.util.Duration;
import lab_8.client.*;
import lab_8.client.service.ClientConnection;
import lab_8.client.service.ResourceFactory;
import lab_8.common.AllCommands;
import lab_8.common.ServerCommand;

/**
 * Контролёр главного окна
 */
public class MainController {
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
    private ResourceFactory rf;
    private CollectionUpdate collectionUpdate;


    @FXML
    private void addButtonClick() throws IOException {
        Stage add_stage = new Stage();
        add_stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("AddWindow.fxml"))));
        add_stage.setTitle("1Xбилетик-777");
        add_stage.getIcons().add(new Image("file:sources/ticket.png"));
        add_stage.setResizable(false);
        add_stage.showAndWait();
        if (AddController.getCommand() != null)
            collectionUpdate.update(AddController.getCommand(), false);
    }

    @FXML
    private void deleteButtonClick() {
        collectionUpdate.update(new ServerCommand(AllCommands.remove_by_id,
                ticket_table.getSelectionModel().getSelectedItem().getId()), true);
    }

    @FXML
    private void clearButtonClick() {
        collectionUpdate.update(new ServerCommand(AllCommands.clear), true);
    }

    @FXML
    private void initialize() {
        setLocale();
        add_button.textProperty().bind(rf.get("Create"));
        delete_button.textProperty().bind(rf.get("Delete"));
        clear_button.textProperty().bind(rf.get("Clear"));
        scale_label.textProperty().bind(rf.get("Scale"));
        name_label.textProperty().bind(rf.get("Username", ": " + ClientMain.getUser().getUser()));

        ArrayList<String> serverTable = ClientConnection.connect(new ServerCommand(AllCommands.show)).getInformation();
        serverTable.remove(0);
        for (int i = 0; i < serverTable.size() / 11; i++)
            tickets.add(new TicketView(serverTable.subList(i * 11, (i + 1) * 11)));

        collectionUpdate = new CollectionUpdate(tickets, info_label, rf);

        CanvasWork canvasWork = new CanvasWork(main_canvas, zoom_spinner.valueProperty(), tickets, ticket_table);
        ChangeListener<Number> ruler = (observable, oldValue, newValue) -> {
            canvasWork.setX_angle((canvas_pane.getContent().getBoundsInParent().getWidth() -
                    canvas_pane.viewportBoundsProperty().get().getWidth()) * canvas_pane.getHvalue() / zoom_spinner.getValue());
            canvasWork.setY_angle((canvas_pane.getContent().getBoundsInParent().getHeight() -
                    canvas_pane.viewportBoundsProperty().get().getHeight()) * canvas_pane.getVvalue() / zoom_spinner.getValue());
        };
        canvas_pane.vvalueProperty().addListener(ruler);
        canvas_pane.hvalueProperty().addListener(ruler);
        canvas_pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        canvas_pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        new TableWork(ticket_table, tickets, collectionUpdate);

        zoom_spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 2, 1, 0.1));

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(200),
                ae -> {
                    ServerCommand command = ClientConnection.receive(true);
                    if (command != null)
                        collectionUpdate.update(command,false);
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
}