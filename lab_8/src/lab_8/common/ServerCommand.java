package lab_8.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Класс команд, которыми обмениваются клиент и сервер
 */
public class ServerCommand implements Serializable {

    private AllCommands command;
    private ArrayList<String> information = new ArrayList<>();
    private String user = null, password = null;
    private boolean result;

    /**
     * Создаёт команду имеющую тип команды
     *
     * @param command тип команды
     */
    public ServerCommand(AllCommands command) {
        this.command = command;
    }

    /**
     * Создаёт команду имеющую тип команды и доп информацию
     *
     * @param command     тип команды
     * @param information информация
     */
    public ServerCommand(AllCommands command, String... information) {
        this.command = command;
        this.information.addAll(Arrays.asList(information));
    }

    public ServerCommand(AllCommands command, boolean result) {
        this.result = result;
        this.command = command;
    }

    public ServerCommand(AllCommands command, boolean result, String... information) {
        this.result = result;
        this.command = command;
        this.information.addAll(Arrays.asList(information));
    }

    public ServerCommand(AllCommands command, boolean result, ArrayList<String> information) {
        this.result = result;
        this.command = command;
        this.information = information;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getResult() {
        return result;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public AllCommands getCommand() {
        return command;
    }

    public ArrayList<String> getInformation() {
        return information;
    }

    public ServerCommand toAll() {
        //if (command != AllCommands.add)
            information.set(0, null);
        return this;
    }
}
