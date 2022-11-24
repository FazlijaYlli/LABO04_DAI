package ch.heigvd.util;

// Sert à stocker une adresse. Peu d'informations suppémentaires sont présentes mais pratique
// si l'on souhaite ajouter des propriétés plus tard.
public class Person {
    private String address;

    public Person(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String toString() {
        return address;
    }
}
