package lab_7.client;

import lab_7.common.AllCommands;
import lab_7.common.ServerCommand;

import java.util.Scanner;

/**
 * Класс реализует систему входа в систему со стороны пользователя
 */
public class ClientIdentification {

    private String user = null, password = null;
    private Scanner scanner;

    /**
     * Автоизация существующих пользователей и регистрация новых
     *
     * @param scanner сканер ввода
     */
    public void check_user(Scanner scanner) {
        this.scanner = scanner;
        while (true) {
            user = null;
            password = null;
            System.out.printf("%s", "Введите имя пользователя или NEW для регистрации: ");
            user = scanner.nextLine().trim();
            if (user.toLowerCase().equals("new"))
                registration();
            else if (authorization())
                break;
        }
    }

    private boolean authorization() {
        ServerCommand answer = Client.connect(new ServerCommand(AllCommands.check));
        if (answer == null) {
            System.out.printf("%s%n", "Подключение к серверу отсутствует, повторите попытку позже");
            return false;
        }
        try {
            System.out.println(answer.getInformation().get(0));
            return false;
        } catch (Exception ignored) {
        }
        while (true) {
            System.out.printf("%s", "Введите пароль: ");
            password = scanner.nextLine().trim();
            answer = Client.connect(new ServerCommand(AllCommands.check));
            if (answer == null) {
                System.out.printf("%s%n", "Подключение к серверу отсутствует, повторите попытку позже");
                return false;
            }
            try {
                System.out.println(answer.getInformation().get(0));
            } catch (Exception e) {
                return true;
            }
        }
    }

    private void registration() {
        while (true) {
            System.out.printf("%s", "Введите имя нового пользователя: ");
            user = scanner.nextLine().trim();
            ServerCommand answer = Client.connect(new ServerCommand(AllCommands.registration));
            if (answer == null) {
                System.out.printf("%s", "Подключение к серверу отсутствует, повторите попытку позже");
                return;
            }
            try {
                System.out.println(answer.getInformation().get(0));
            } catch (Exception e) {
                break;
            }
        }
        while (true) {
            System.out.printf("%s", "Введите пароль: ");
            password = scanner.nextLine().trim();
            System.out.printf("%s", "Повторите пароль: ");
            if (scanner.nextLine().trim().equals(password))
                break;
            System.out.printf("%s%n", "Введённые пароли не совпадают, повторите попытку");
        }
        ServerCommand answer = Client.connect(new ServerCommand(AllCommands.registration));
        if (answer == null) {
            System.out.printf("%s%n", "Подключение к серверу отсутствует, повторите попытку позже");
            return;
        }
        System.out.println(answer.getInformation().get(0));
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
