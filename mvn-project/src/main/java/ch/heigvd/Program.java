package ch.heigvd;

import ch.heigvd.config.Configurator;
import ch.heigvd.smtp.SmtpClient;
import ch.heigvd.util.Group;
import ch.heigvd.util.Mail;
import ch.heigvd.util.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Program {
    public static void main(String[] args) throws Exception {
        startPranks();
    }

    // Fonction temporaire pour tester l'envoi de mail.
    private static void startPranks() throws Exception {
        Configurator c = new Configurator();
        SmtpClient client = new SmtpClient(c.getServerAdress(), c.getServerPort());
        for (Mail m : c.getMails()) {
            client.send(m);
        }
    }
}
