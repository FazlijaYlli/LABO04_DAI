package ch.heigvd.smtp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

import ch.heigvd.util.Mail;
import ch.heigvd.smtp.ISmtpCommands;

public class SmtpClient {
    private static final Logger LOGGER = Logger.getLogger(SmtpClient.class.getName());
    private static final String END = "\r\n";
    private final String serverAddress;
    private final int serverPort;

    private BufferedWriter w;
    private BufferedReader r;
    private String buf;

    public SmtpClient(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }

    private void check() throws IOException {
        buf = r.readLine();
        if (!buf.startsWith("250")) {
            throw new IOException("SMTP server did not return a success : " + buf);
        }
    }

    private void send(String s, boolean check) throws IOException {
        w.write(s + END);
        if (check) {
            w.flush();
            check();
        }
    }

    public void send(Mail m) throws IOException {
        Socket socket = new Socket(serverAddress, serverPort);
        w = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        r = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        buf = r.readLine();
        LOGGER.info(buf);

        // Introducing ourselves to the server
        send(ISmtpCommands.EHLO + serverAddress, true);

        // Getting the address of the sender
        send(ISmtpCommands.MAIL_FROM + m.getFrom(), true);

        // Writing all the receivers in one mail
        for (String to : m.getTo()) {
            send(ISmtpCommands.RCPT_TO + to, true);
        }

        // Hidden receiver
        send(ISmtpCommands.RCPT_TO + m.getCc(), true);

        // Header of content section
        send(ISmtpCommands.DATA, true);
        send(ISmtpCommands.CONTENT_TYPE + END, false);
        send(ISmtpCommands.FROM + m.getFrom() + END, false);
        send(ISmtpCommands.TO + m.getTo().get(0) + END, false);
        for (int i = 1; i < m.getTo().size(); ++i) {
            send(", " + m.getTo().get(i), false);
        }
        send(END, false);

        // Section empruntée du site web dont le lien est sur le github du labo.
        send(ISmtpCommands.SUBJECT_UTF8 +
                Base64.getEncoder().encodeToString(m.getSubject().getBytes()) +
                "?=" + END, false);
        send(ISmtpCommands.SUBJECT + m.getSubject() + END, false);
        send(END, false);
        w.flush();
        // Fin de section empruntée.

        // Content section
        send(m.getContent() + END, false);
        send(".",true);

        buf = r.readLine();
        LOGGER.info(buf);

        // Quitting the server
        send(ISmtpCommands.QUIT, false);
        r.close();
        w.close();
        socket.close();
    }
}
