package ch.heigvd.util;

import java.util.List;

public class Mail {
    private final String subject;
    private final String content;
    private final String from;
    private final List<String> to;
    private final String cc;

    public Mail(String subject, String content, String from, List<String> to, String cc) {
        this.subject = subject;
        this.content = content;
        this.from = from;
        this.to = to;
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getTo() {
        return to;
    }

    public String getCc() {
        return cc;
    }
}
