package lab5;

/**
 * Класс реализует проверку строк на соответствие перечислительным типам
 */
abstract class TicketCheck {
    /**
     * Проверяет является ли число перечисляемым типом TicketType
     *
     * @param string проверяемое число в виде строки
     * @return возвращает true, если число соответствует перечисляемому типом класса TicketType false если нет
     */
    static boolean checkTicketEnum(String string) {
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
    static boolean checkEventEnum(String string) {
        try {
            EventType.valueOf(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
