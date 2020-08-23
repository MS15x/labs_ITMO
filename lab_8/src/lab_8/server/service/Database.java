package lab_8.server.service;

import lab_8.common.ticket.EventType;
import lab_8.common.ticket.TicketType;
import lab_8.common.ticket.Coordinates;
import lab_8.common.ticket.Event;
import lab_8.common.ticket.Ticket;

import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Реализует взаимодйствие с базой данных билетов
 */
public abstract class Database {
    private static final Logger logger = Logger.getLogger(Database.class.getName());
    private static final String url = "jdbc:postgresql://localhost:5432/postgres"; //"jdbc:postgresql://pg:5432/studs";
    private static final String user = "postgres"; //"s284774";
    private static final String password = "vaksa"; //"asl503";
    private static Connection connection;
    private static Statement statement;

    /**
     * Получение информации из бызы данных
     *
     * @return коллекция билетов
     */
    public static ArrayList<Ticket> open() {
        ArrayList<Ticket> all_tickets = new ArrayList<>();
        try {
            connect();
            ResultSet resultSet = statement.executeQuery("SELECT * from tickets");
            while (resultSet.next()) {
                Ticket ticket = new Ticket(resultSet.getInt(1),
                        resultSet.getString(2),
                        new Coordinates(resultSet.getFloat(3),
                                resultSet.getLong(4)),
                        resultSet.getDate(5).toLocalDate(),
                        resultSet.getInt(6),
                        (resultSet.getString(7) == null ? null : TicketType.valueOf(resultSet.getString(7))),
                        (resultSet.getLong(8) == -1 ? null : new Event(resultSet.getLong(8),
                                resultSet.getString(9),
                                resultSet.getTimestamp(10).toLocalDateTime().atZone(ZoneId.of("Europe/Moscow")),
                                (resultSet.getString(11) == null ? null : EventType.valueOf(resultSet.getString(11))))),
                        resultSet.getString(12));
                all_tickets.add(ticket);
            }
            statement.close();
            resultSet.close();
            connection.close();
        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Не удалось загрузить базу данных", e);
            return null;
        }
        logger.info("База данных загружена");
        return all_tickets;
    }

    /**
     * Загрузка информации в базу данных
     *
     * @param all_tickets коллекция билетов
     */
    public static void save(ArrayList<Ticket> all_tickets) {
        try {
            connect();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tickets " +
                    "(ticket_id," +
                    "ticket_name," +
                    "coordinate_x," +
                    "coordinate_y," +
                    "local_date," +
                    "price," +
                    "ticket_type," +
                    "event_id," +
                    "event_name," +
                    "event_time," +
                    "event_type," +
                    "users)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.executeUpdate("TRUNCATE tickets");
            for (Ticket ticket : all_tickets) {
                preparedStatement.setInt(1, ticket.getId());
                preparedStatement.setString(2, ticket.getName());
                preparedStatement.setFloat(3, ticket.getCoordinates().getX());
                preparedStatement.setLong(4, ticket.getCoordinates().getY());
                preparedStatement.setDate(5, Date.valueOf(ticket.getCreationDate()));
                preparedStatement.setInt(6, ticket.getPrice());
                preparedStatement.setString(7, (ticket.getType() == null ? null : ticket.getType().toString()));
                if (ticket.getEvent() != null) {
                    preparedStatement.setLong(8, ticket.getEvent().getId());
                    preparedStatement.setString(9, ticket.getEvent().getName());
                    preparedStatement.setTimestamp(10, Timestamp.valueOf(ticket.getEvent().getDate().toLocalDateTime()));
                    preparedStatement.setString(11, (ticket.getEvent().getType() == null ? null : ticket.getEvent().getType().toString()));
                } else {
                    preparedStatement.setLong(8, -1);
                    preparedStatement.setString(9, null);
                    preparedStatement.setTimestamp(10, null);
                    preparedStatement.setString(11, null);
                }
                preparedStatement.setString(12, ticket.getUser());
                preparedStatement.executeUpdate();
            }
            statement.close();
            preparedStatement.close();
            connection.close();
            logger.info("База данных сохранена");
        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Не удалось сохранить базу данных", e);
        }
    }

    private static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
        create_tickets();
        create_users();
    }

    private static void create_tickets() throws SQLException {
        final String create_ticket = "CREATE TABLE IF NOT EXISTS tickets("
                + "ticket_id INT,"
                + "ticket_name varchar(64),"
                + "coordinate_x FLOAT,"
                + "coordinate_y BIGINT,"
                + "local_date DATE,"
                + "price INT,"
                + "ticket_type varchar(64),"
                + "event_id BIGINT,"
                + "event_name varchar(64),"
                + "event_time TIMESTAMP,"
                + "event_type varchar(64),"
                + "users varchar(64))";
        statement.execute(create_ticket);
    }

    private static void create_users() throws SQLException {
        final String create_users = "CREATE TABLE IF NOT EXISTS users("
                + "users varchar(64),"
                + "password varchar(128))";
        statement.execute(create_users);
    }
}
