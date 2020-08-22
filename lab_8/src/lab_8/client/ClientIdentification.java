package lab_8.client;

import lab_8.common.AllCommands;
import lab_8.common.ServerCommand;

/**
 * Класс реализует систему входа в систему со стороны пользователя
 */
public class ClientIdentification {
    private static ClientIdentification instance;
    private static String user, password;
    private boolean result;

    private ClientIdentification() {
    }

    /**
     * Сингелтон конструктор
     *
     * @param user     имя пользователя
     * @param password рароль
     * @return класс ClientIdentification
     */
    public static ClientIdentification getInstance(String user, String password) {
        if (instance == null)
            instance = new ClientIdentification();
        ClientIdentification.user = user;
        ClientIdentification.password = password;
        return instance;
    }

    /**
     * Сингелтон конструктор
     *
     * @return класс ClientIdentification
     */
    public static ClientIdentification getInstance() {
        if (instance == null)
            instance = new ClientIdentification();
        return instance;
    }

    /**
     * запрос авторизации на сервере
     *
     * @return ответ сервера
     */
    public ServerCommand authorization() {
        return ClientConnection.connect(new ServerCommand(AllCommands.check));
    }

    /**
     * запрос регистрации нового пользователя на сервере
     *
     * @return ответ сервера
     */
    public ServerCommand registration() {
        return ClientConnection.connect(new ServerCommand(AllCommands.registration));
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
