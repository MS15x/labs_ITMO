package lab_8.client.languages;

import java.util.ListResourceBundle;

public class MyLanguages_ru_RU extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"Username", "Имя пользователя"},
                {"Password", "Пароль"},
                {"Password_conf", "Повторите пароль"},
                {"Input", "Ввод"},
                {"Check in", "Регистрация"},
                {"New username", "Имя нового пользователя"},
                {"Login", "Вход"},

                {"Ans-201", "Пароль введён неверно"},
                {"Ans-202", "Пользователя с таким именем не зарегистрировано"},
                {"Ans-203", "Произошла ошибка на стороне сервера"},
                {"Ans-204", "Пользователь с таким именем уже зарегистрирован"},
                {"Ans-205", "Пользователь был успешно зарегистрирован"},
                {"Ans-206", "Все ваши элементы были удалены"},
                {"Ans-207", "Элемент был успешно удалён"},
                {"Ans-208", "Этот элемент вам не принадлежит"},
                {"Ans-209", "Элемент был успешно добавлен"},

                {"Ans-101", "Подключение к серверу отсутствует"},
                {"Ans-102", "Введённые пароли не совпадают"},
                {"Ans-103", "Заполните все обязательные поля помеченные *"},

                {"Create", "Создать"},
                {"Delete", "Удалить"},
                {"Clear", "Очистить"},
                {"Scale", "Масштаб"},

                {"userNameCol", "Пользователь"},
                {"ticketDateCol", "Дата"},
                {"ticketCol", "Билет"},
                {"ticketNameCol", "Название"},
                {"ticketPriceCol", "Цена"},
                {"ticketTypeCol", "Тип"},
                {"eventCol", "Событие"},
                {"eventNameCol", "Название"},
                {"eventTypeCol", "Тип"},
                {"eventDateCol", "Время создания"},

                {"Add", "Добавить"},
                {"Exit", "Выйти"},

                {"time", "Europe/Moscow"}
        };
    }
}
