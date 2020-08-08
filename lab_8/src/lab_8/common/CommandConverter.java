package lab_8.common;

import java.io.*;

/**
 * Сериализует соманды пользователя
 */
public abstract class CommandConverter implements Serializable {
    /**
     * Превращает объект в массив байтов
     *
     * @param command команда в виде класса
     * @return команда в виде массива байтов
     * @throws IOException не удалось сериализовать данные
     */
    public static byte[] toByteCommand(ServerCommand command) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
        objStream.writeObject(command);
        return byteStream.toByteArray();
    }

    /**
     * Превращает массив байтов в объект
     *
     * @param byteCommand массив байтов
     * @return команда в виде класса
     * @throws IOException            не удалось десериализовать данные
     * @throws ClassNotFoundException не удалось привести тип
     */
    public static ServerCommand fromByteCommand(byte[] byteCommand) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(byteCommand);
        ObjectInputStream objStream = new ObjectInputStream(byteStream);
        return (ServerCommand) objStream.readObject();
    }
}
