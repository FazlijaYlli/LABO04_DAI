package ch.heigvd.smtp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.heigvd.util.Mail;
import ch.heigvd.util.Person;

// Permets de se connecter au serveur SMTP avec les arguments du constructeur.
public class SmtpClient {
    // Objet logger nous permettant de lire les retours du serveur depuis notre application client.
    private static final Logger LOGGER = Logger.getLogger(SmtpClient.class.getName());
    // Les lignes envoyées au serveur SMTP doivent être <CR><LF>.
    private static final String ENDL = "\r\n";
    // Adresse IP du serveur SMTP.
    private final String serverAddress;
    // Port que le serveur SMTP utilise.
    private final int serverPort;

    // w et r seront nos writers en readers utilisés lors d'écriture dans les sockets.
    private BufferedWriter w;
    private BufferedReader r;

    // String dans laquelle nous stockerons la ligne lue depuis le serveur, une à la fois.
    private String buf;

    // Constructeur de la classe, prend l'adresse IP et le port du serveur SMTP.
    public SmtpClient(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }

    // Permets de logger and vérifier que chaque ligne fut acceptée par le serveur.
    private void check() throws IOException {
        buf = r.readLine();
        if (!buf.startsWith("250")) {
            throw new IOException("SMTP server did not return a success : " + buf);
        }
        logInfo(buf);
    }

    // Permets d'envoyer une chaîne de caractères au serveur.
    // Si check == true, on demande de flush le buffer et vérifier que le serveur valide la ligne.
    private void send(String s, boolean check) throws IOException {
        w.write(s + ENDL);
        if (check) {
            w.flush();
            check();
        }
    }

    // Permets d'envoyer un mail complet au serveur SMTP.
    public void send(Mail m) throws IOException {
        logInfo("Creating socket to connect to server...");
        Socket socket = new Socket(serverAddress, serverPort);
        w = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        r = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        buf = r.readLine();
        logInfo(buf);

        // Introducing ourselves to the server
        send(ISmtpCommands.EHLO + serverAddress, true);

        // Getting the address of the sender
        send(ISmtpCommands.MAIL_FROM + m.getFrom(), true);

        // Writing all the receivers in one mail
        for (Person to : m.getTo()) {
            send(ISmtpCommands.RCPT_TO + to, true);
        }

        // Hidden receiver
        send(ISmtpCommands.RCPT_TO + m.getCc(), true);

        // Header of content section
        send(ISmtpCommands.DATA, true);
        send(ISmtpCommands.CONTENT_TYPE, false);
        send(ISmtpCommands.FROM + m.getFrom(), false);
        w.write(ISmtpCommands.TO + m.getTo().get(0));
        for (int i = 1; i < m.getTo().size(); ++i) {
            w.write(", " + m.getTo().get(i));
        }
        w.write(ENDL);

        send(ISmtpCommands.CC + m.getCc(), false);

        // Section empruntée du site web dont le lien est sur le github du labo.
        send(ISmtpCommands.SUBJECT_UTF8 +
                Base64.getEncoder().encodeToString(m.getSubject().getBytes()) +
                "?=", false);
        send(ISmtpCommands.SUBJECT + m.getSubject(), false);
        // Fin de section empruntée.

        // Content section
        send(m.getContent() + ENDL+"."+ENDL, true);

        buf = r.readLine();
        logInfo(buf);

        // Quitting the server
        send(ISmtpCommands.QUIT, true);
        r.close();
        w.close();
        socket.close();
    }

    private void logInfo(String s) {
        LOGGER.log(Level.INFO,"\u001B[34m" + s + "\u001B[0m");
    }
}
