package lab_8.client.languages;

import java.util.ListResourceBundle;

public class MyLanguages_da_DK extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"Username", "Brugernavn"},
                {"Password", "Adgangskode"},
                {"Password_conf", "Bekræft kodeord"},
                {"Input", "Input"},
                {"Check in", "Tjekke ind"},
                {"New username", "Nyt brugernavn"},
                {"Login", "Indgang"},

                {"Login err msg", "De indtastede adgangskoder stemmer ikke overens"},
                {"Add err msg", "Udfyld alle obligatoriske felter markeret med *"},

                {"Ans-201", "Adgangskode indtastet forkert"},
                {"Ans-202", "Ingen bruger med dette navn er registreret"},
                {"Ans-203", "Der opstod en fejl på serversiden"},
                {"Ans-204", "Bruger med dette navn er allerede registreret"},
                {"Ans-205", "Brugeren blev registreret med succes"},
                {"Ans-206", "Alle dine varer er blevet fjernet"},
                {"Ans-207", "Elementet blev slettet"},
                {"Ans-208", "Denne vare hører ikke til dig"},
                {"Ans-209", "Elementet blev tilføjet med succes"},

                {"Ans-101", "Der er ingen forbindelse til serveren"},
                {"Ans-102", "De indtastede adgangskoder stemmer ikke overens"},
                {"Ans-103", "Udfyld alle obligatoriske felter markeret med *"},

                {"Create", "Lave en"},
                {"Delete", "Slet"},
                {"Clear", "Klar"},
                {"Scale", "Vægt"},

                {"userNameCol", "Bruger"},
                {"ticketDateCol", "Dato"},
                {"ticketCol", "Billet"},
                {"ticketNameCol", "Navn"},
                {"ticketPriceCol", "Pris"},
                {"ticketTypeCol", "En type"},
                {"eventCol", "Begivenhed"},
                {"eventNameCol", "Navn"},
                {"eventTypeCol", "En type"},
                {"eventDateCol", "Oprettelsestidspunkt"},

                {"Add", "Tilføj til"},
                {"Exit", "Log af"},

                {"time", "Europe/Copenhagen"}
        };
    }
}
