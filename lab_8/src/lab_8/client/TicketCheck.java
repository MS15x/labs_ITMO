package lab_8.client;

import lab_8.common.ticket.EventType;
import lab_8.common.ticket.TicketType;

/**
 * Класс реализует проверку строк на соответствие перечислительным типам
 */
public abstract class TicketCheck {
    /**
     * Проверяет является ли число перечисляемым типом TicketType
     *
     * @param string проверяемое число в виде строки
     * @return возвращает true, если число соответствует перечисляемому типом класса TicketType false если нет
     */
    public static boolean checkTicketEnum(String string) {
        try {
            TicketType.valueOf(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Проверяет является ли число перечисляемым типом EventType
     *
     * @param string проверяемое число в виде строки
     * @return возвращает true, если число соответствует перечисляемому типом класса EventType false если нет
     */
    public static boolean checkEventEnum(String string) {
        try {
            EventType.valueOf(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
