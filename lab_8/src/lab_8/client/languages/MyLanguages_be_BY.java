package lab_8.client.languages;

import java.util.ListResourceBundle;

public class MyLanguages_be_BY extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"Username", "Імя карыстальніка"},
                {"Password", "Пароль"},
                {"Password_conf", "Паўтарыце пароль"},
                {"Input", "Увод"},
                {"Check in", "Рэгістрацыя"},
                {"New username", "Імя новага карыстальніка"},
                {"Login", "Уваход"},

                {"Ans-201", "Пароль ўведзены няправільна"},
                {"Ans-202", "Карыстальніка з такім імем не зарэгістравана"},
                {"Ans-203", "Адбылася памылка на боку сервера"},
                {"Ans-204", "Карыстальнік з такім імем ўжо зарэгістраваны"},
                {"Ans-205", "Карыстальнік быў паспяхова зарэгістраваны"},
                {"Ans-206", "Усе вашыя элементы былі выдалены"},
                {"Ans-207", "Элемент быў паспяхова выдалены"},
                {"Ans-208", "Гэты элемент вам не належыць"},
                {"Ans-209", "Элемент быў паспяхова дададзены"},

                {"Ans-101", "Падключэнне да сервера адсутнічае"},
                {"Ans-102", "Уведзеныя паролі не супадаюць"},
                {"Ans-103", "Запоўніце ўсе абавязковыя палі пазначаныя *"},

                {"Create", "Стварыць"},
                {"Delete", "Выдаліць"},
                {"Clear", "Ачысціць"},
                {"Scale", "Маштаб"},

                {"userNameCol", "Карыстальнік"},
                {"ticketDateCol", "Дата"},
                {"ticketCol", "Кквіток"},
                {"ticketNameCol", "Назва"},
                {"ticketPriceCol", "Цана"},
                {"ticketTypeCol", "Тып"},
                {"eventCol", "Падзея"},
                {"eventNameCol", "Назва"},
                {"eventTypeCol", "Тып"},
                {"eventDateCol", "Час стварэння"},

                {"Add","Дадаць"},
                {"Exit","Выйсці"},

                {"time","Europe/Minsk"}
        };
    }
}
