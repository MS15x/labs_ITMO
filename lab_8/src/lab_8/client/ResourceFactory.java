package lab_8.client;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ResourceBundle;

/**
 * Предоставляет доступ к данным известного ListResourceBundle файла
 */
public class ResourceFactory {
    private static ResourceFactory instance;
    private ObjectProperty<ResourceBundle> resources = new SimpleObjectProperty<>();

    private ResourceFactory() {
    }

    /**
     * Сингелтон конструктор
     *
     * @return создаёт новый класс или возвращает уже существующий
     */
    public static ResourceFactory getInstance() {
        if (instance == null)
            instance = new ResourceFactory();
        return instance;
    }

    /**
     * Назначение файла, к которому необходимо подключиться
     *
     * @param resources файл
     */
    public void setResources(ResourceBundle resources) {
        this.resources.set(resources);
    }

    /**
     * Получение строки из файла по ключу
     *
     * @param key ключ
     * @return строка из файла
     */
    public String getString(String key) {
        return resources.get().getString(key);
    }

    /**
     * Получение строки, обёрнутой в StringBinding, из файла по ключу
     *
     * @param key ключ
     * @return строка
     */
    public StringBinding get(String key) {
        return new StringBinding() {
            {
                bind(resources);
            }

            @Override
            public String computeValue() {
                return resources.get().getString(key);
            }
        };
    }

    /**
     * Получение строки с добавлением постфикса, обёрнутой в StringBinding, из файла по ключу
     *
     * @param key ключ
     * @param add постфикс
     * @return строка
     */
    public StringBinding get(String key, String add) {
        return new StringBinding() {
            {
                bind(resources);
            }

            @Override
            public String computeValue() {
                return resources.get().getString(key) + add;
            }
        };
    }
}
