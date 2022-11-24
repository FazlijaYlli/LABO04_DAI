package ch.heigvd;

import ch.heigvd.smtp.SmtpClient;
import ch.heigvd.util.Mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Program {
    public static void main(String[] args) throws IOException {
        List<String> to = Arrays.asList(
                "kylian.manzini@heig-vd.ch",
                "simon.guggisberg@heig-vd.ch",
                "theo.yoshiura@heig-vd.ch",
                "jeremiah.steiner@heig.vd.ch"
        );
        Mail m = new Mail(
                "Premier e-mail !",
                "Voici mon premier e-mail.\nIl est codé directement en dur depuis mon projet Maven.",
                "ylli.fazlija@heig-vd.ch",
                new ArrayList<String>(to),
                "julien.leresche@heig-vd.ch"
        );
        SmtpClient client = new SmtpClient("localhost", 25);
        client.send(m);
    }
}
