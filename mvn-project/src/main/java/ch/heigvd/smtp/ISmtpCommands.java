package ch.heigvd.smtp;

// TODO : Demander si ça vaut la peine de faire comme ceci.

public interface ISmtpCommands {
    String EHLO = "EHLO ";
    String MAIL_FROM = "MAIL FROM:";
    String RCPT_TO = "RCPT TO:";
    String DATA = "DATA";
    String CONTENT_TYPE = "Content-type: text/plain; charset=utf-8";
    String FROM = "From: ";
    String TO = "To: ";
    String SUBJECT_UTF8 = "Subject: =?utf-8?B?";
    String SUBJECT = "Subject: ";
    String QUIT = "QUIT";
}
