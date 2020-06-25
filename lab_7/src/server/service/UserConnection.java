package lab_7.server.service;

import lab_7.common.AllCommands;
import lab_7.common.ServerCommand;

import java.sql.*;

/**
 * Класс реализует систему входа в систему со стороны сервера
 */
public abstract class UserConnection {
    private static final String url = "jdbc:postgresql://localhost:5432/postgres";
    private static final String user = "";
    private static final String password = "";

    /**
     * проверяет зарегистрирован ли пользователь
     *
     * @param command команда от пользователя
     * @return ответная команда
     */
    public static ServerCommand check(ServerCommand command) {
        if (command.getUser() == null)
            return new ServerCommand(AllCommands.check);
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            if (command.getPassword() == null) {
                while (resultSet.next())
                    if (resultSet.getString(1).equals(command.getUser()))
                        return new ServerCommand(AllCommands.check, false);
                return new ServerCommand(AllCommands.check, true, "Пользователя с таким именем не зарегистировано");
            } else {
                resultSet = statement.executeQuery("SELECT * FROM users");
                while (resultSet.next())
                    if (resultSet.getString(1).equals(command.getUser()) &
                            resultSet.getString(2).equals(PassHash.hash(command.getPassword())))
                        return new ServerCommand(AllCommands.check, true);
                return new ServerCommand(AllCommands.check, false, "Пароль введён неверно");
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * добавляет нового пользоваетля
     *
     * @param command команда от пользователя
     * @return ответная команда
     */
    public static ServerCommand add(ServerCommand command) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            if (command.getPassword() == null) {
                while (resultSet.next())
                    if (resultSet.getString(1).equals(command.getUser()))
                        return new ServerCommand(AllCommands.registration, false, "Пользователь с таким именем уже зарегистирован");
                return new ServerCommand(AllCommands.registration, true);
            } else {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users(users,password) VALUES (?,?)");
                preparedStatement.setString(1, command.getUser());
                preparedStatement.setString(2, PassHash.hash(command.getPassword()));
                preparedStatement.executeUpdate();
                return new ServerCommand(AllCommands.registration, true, "Пользователь был успешно зарегистрирован");
            }
        } catch (Exception e) {
            return null;
        }
    }
}
