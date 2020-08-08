package lab_8.client;

import lab_8.common.AllCommands;
import lab_8.common.ServerCommand;

/**
 * Класс реализует систему входа в систему со стороны пользователя
 */
public class ClientIdentification {
    private static ClientIdentification instance;
    private static String user, password;

    private ClientIdentification() {
    }

    public static ClientIdentification getInstance(String user, String password) {
        ClientIdentification.user = user;
        ClientIdentification.password = password;
        if (instance == null)
            instance = new ClientIdentification();
        return instance;
    }

    public static ClientIdentification getInstance() {
        if (instance == null)
            instance = new ClientIdentification();
        return instance;
    }

    public ServerCommand authorization() {
        return Client.connect(new ServerCommand(AllCommands.check));
    }

    public ServerCommand registration() {
        return Client.connect(new ServerCommand(AllCommands.registration));
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
