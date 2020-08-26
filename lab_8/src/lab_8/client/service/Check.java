package lab_8.client.service;

/**
 * Класс реализует проверку чисел на соответствие типу
 */
public abstract class Check {
    /**
     * Проверяет является ли число типом long
     *
     * @param string проверяемое число в виде строки
     * @return возвращает true, если число соответствует типу long, и false, если нет
     */
    public static boolean checkLong(String string) {
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
    public static boolean checkInt(String string) {
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
    public static boolean checkFloat(String string) {
        try {
            Float.parseFloat(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
