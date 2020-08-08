package lab_8.server.service;

import lab_8.common.AllCommands;
import lab_8.common.ServerCommand;

import java.sql.*;

/**
 * Класс реализует систему входа в систему со стороны сервера
 */
public abstract class UserConnection {
    private static final String url = "jdbc:postgresql://localhost:5432/postgres";//"jdbc:postgresql://pg:5432/studs";
    private static final String user = "postgres";//"s284774";
    private static final String password = "vaksa";//"asl503";
    private static Connection connection;
    private static ResultSet resultSet;

    private static void connect() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM users");
    }

    /**
     * проверяет зарегистрирован ли пользователь
     *
     * @param command команда от пользователя
     * @return ответная команда
     */
    public static ServerCommand check(ServerCommand command) {
        if (command.getUser() == null)
            return new ServerCommand(AllCommands.check, true);
        else try {
            connect();
            while (resultSet.next())
                if (resultSet.getString(1).equals(command.getUser()))
                    if (resultSet.getString(2).equals(PassHash.hash(command.getPassword())))
                        return new ServerCommand(AllCommands.check, true);
                    else
                        return new ServerCommand(AllCommands.check, false, "Пароль введён неверно");
            return new ServerCommand(AllCommands.check, false, "Пользователя с таким именем не зарегистировано");

            /*if (command.getPassword() == null) {
                while (resultSet.next())
                    if (resultSet.getString(1).equals(command.getUser()))
                        return new ServerCommand(AllCommands.check, true);
                return new ServerCommand(AllCommands.check, false, "Пользователя с таким именем не зарегистировано");
            } else {
                while (resultSet.next())
                    if (resultSet.getString(1).equals(command.getUser()) &
                            resultSet.getString(2).equals(PassHash.hash(command.getPassword())))
                        return new ServerCommand(AllCommands.check, true);
                return new ServerCommand(AllCommands.check, false, "Пароль введён неверно");
            }*/
        } catch (Exception e) {
            return new ServerCommand(AllCommands.check, false, "Произошла ошибка на стороне сервера");
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
            connect();
            while (resultSet.next())
                if (resultSet.getString(1).equals(command.getUser()))
                    return new ServerCommand(AllCommands.registration, false, "Пользователь с таким именем уже зарегистирован");
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users(users,password) VALUES (?,?)");
            preparedStatement.setString(1, command.getUser());
            preparedStatement.setString(2, PassHash.hash(command.getPassword()));
            preparedStatement.executeUpdate();
            return new ServerCommand(AllCommands.registration, true, "Пользователь был успешно зарегистрирован");
        } catch (Exception e) {
            return new ServerCommand(AllCommands.check, false, "Произошла ошибка на стороне сервера");
        }
    }
}
