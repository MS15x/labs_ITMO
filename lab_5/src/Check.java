package lab5;

/**
 * Класс реализует проверку чисел на соответствие типу
 */
abstract class Check {
    /**
     * Проверяет является ли число типом long
     *
     * @param string проверяемое число в виде строки
     * @return возвращает true, если число соответствует типу long, и false, если нет
     */
    static boolean checkLong(String string) {
        try {
            Long.parseLong(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Проверяет является ли число типом int
     *
     * @param string проверяемое число в виде строки
     * @return возвращает true, если число соответствует типу int, и int, если нет
     */
    static boolean checkInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Проверяет является ли число типом float
     *
     * @param string проверяемое число в виде строки
     * @return возвращает true, если число соответствует типу float, и false, если нет
     */
    static boolean checkFloat(String string) {
        try {
            Float.parseFloat(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Проверяет является ли число перечисляемым типом класса TicketType
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
     * Проверяет является ли число перечисляемым типом класса EventType
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
