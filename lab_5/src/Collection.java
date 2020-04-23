package lab5;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Главный класс
 */
public class Collection {

    public static void main(String[] args) {
        ZonedDateTime first_date = ZonedDateTime.now();
        Scanner scanner = new Scanner(System.in);
        HashSet<Ticket> tickets = new HashSet<>();
        String path  = args[0].trim();
        try {
            Scanner file_scanner = new Scanner(new File(path));
            first_date = ZonedDateTime.parse(file_scanner.nextLine());
            while (file_scanner.hasNext())
                tickets.add(Answers.add_script(file_scanner, ";"));
        } catch (FileNotFoundException e) {
            System.out.println("Не удалось найти или открыть файл");
        } catch (Exception e) {
            System.out.println("Данные в файле некорректны");
        }
        Answers.help_();
        while (true) {
            String[] commands = scanner.nextLine().trim().replaceAll(" {2,}", " ").toLowerCase().split(" ");//квантификатор
            Answers.do_command_(commands, tickets, first_date, path);
        }
    }
}
