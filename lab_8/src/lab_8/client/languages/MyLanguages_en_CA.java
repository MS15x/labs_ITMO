package lab_8.client.languages;

import java.util.ListResourceBundle;

public class MyLanguages_en_CA extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"Username", "Username"},
                {"Password", "Password"},
                {"Password_conf", "Confirm password"},
                {"Input", "Input"},
                {"Check in", "Check in"},
                {"New username", "New username"},
                {"Login", "Login"},

                {"Ans-201", "Password entered incorrectly"},
                {"Ans-202", "User with this name isn't registered"},
                {"Ans-203", "A server side error has occurred"},
                {"Ans-204", "User with this name is already registered"},
                {"Ans-205", "User was registered successfully"},
                {"Ans-206", "All your items have been removed"},
                {"Ans-207", "The item was successfully deleted"},
                {"Ans-208", "This item does not belong to you"},
                {"Ans-209", "The item was added successfully"},

                {"Ans-101", "There is no connection to the server"},
                {"Ans-102", "The entered passwords don't match"},
                {"Ans-103", "Please fill in all required fields marked with *"},

                {"Create", "Create"},
                {"Delete", "Delete"},
                {"Clear", "Clear"},
                {"Scale", "Scale"},

                {"userNameCol", "Username"},
                {"ticketDateCol", "Date"},
                {"ticketCol", "Ticket"},
                {"ticketNameCol", "Name"},
                {"ticketPriceCol", "Price"},
                {"ticketTypeCol", "Type"},
                {"eventCol", "Event"},
                {"eventNameCol", "Name"},
                {"eventTypeCol", "Type"},
                {"eventDateCol", "Creation time"},

                {"Add", "Add"},
                {"Exit", "Exit"},

                {"time", "America/New_York"}
        };
    }
}
