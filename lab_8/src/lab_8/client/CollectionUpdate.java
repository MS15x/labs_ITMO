package lab_8.client;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import lab_8.client.service.ClientConnection;
import lab_8.client.service.ResourceFactory;
import lab_8.common.ServerCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Работа с коллекцией билетов
 */
public class CollectionUpdate {

    private ObservableList<TicketView> tickets;
    private Label info_label;
    private ResourceFactory rf;

    /**
     * @param tickets    коллекция билетов
     * @param info_label строка для вывода сообщений на экран
     * @param rf         фабрика для получения доступа к языковым пакетам
     */
    public CollectionUpdate(ObservableList<TicketView> tickets,
                            Label info_label,
                            ResourceFactory rf) {
        this.tickets = tickets;
        this.info_label = info_label;
        this.rf = rf;
    }

    /**
     * Выполняет команды над коллекцией билетов
     *
     * @param command команда
     * @param connect следует ли перед выполнением отправлять запрос на сервер true-да false-нет
     */
    public void update(ServerCommand command, boolean connect) {
        ServerCommand answer = connect ? ClientConnection.connect(command) : command;
        ArrayList<String> serverInfo = answer.getInformation();
        if (serverInfo.get(0) != null && !serverInfo.get(0).equals(""))
            info_label.textProperty().bind(rf.get(serverInfo.get(0)));
        serverInfo.remove(0);

        switch (answer.getCommand()) {
            case add:
                TicketView ticket0 = new TicketView(serverInfo);
                tickets.add(ticket0);
                break;
            case remove_by_id:
            case clear:
                List<TicketView> found_tickets = tickets.stream().filter(
                        a -> serverInfo.contains(a.getId())).collect(Collectors.toList());
                tickets.removeAll(found_tickets);
                break;
            case update:
                TicketView ticket = tickets.stream().filter(a -> a.getId().equals(answer.getInformation().get(0))).findAny().orElse(null);
                if (ticket != null)
                    switch (Integer.parseInt(answer.getInformation().get(1))) {
                        case 2:
                            ticket.setTicketDate(answer.getInformation().get(2));
                            break;
                        case 3:
                            ticket.setTicketName(answer.getInformation().get(2));
                            break;
                        case 4:
                            ticket.setTicketX(answer.getInformation().get(2));
                            break;
                        case 5:
                            ticket.setTicketY(answer.getInformation().get(2));
                            break;
                        case 6:
                            ticket.setTicketPrice(answer.getInformation().get(2));
                            break;
                        case 7:
                            ticket.setTicketType(answer.getInformation().get(2));
                            break;
                        case 8:
                            ticket.setEventName(answer.getInformation().get(2));
                            ticket.setEventType(answer.getInformation().get(3));
                            ticket.setEventDate(answer.getInformation().get(4));
                            break;
                        case 9:
                            ticket.setEventType(answer.getInformation().get(2));
                            break;
                        default:
                            break;
                    }
                break;
            default:
                break;
        }
    }
}
