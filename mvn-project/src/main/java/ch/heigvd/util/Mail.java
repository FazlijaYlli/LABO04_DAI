package ch.heigvd.util;

import java.util.List;

public class Mail {
    private final String subject;
    private final String content;
    private final Person from;
    private final List<Person> to;
    private final Person cc;

    public Mail(String subject, String content, Person from, List<Person> to, Person cc) {
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

    public Person getFrom() {
        return from;
    }

    public List<Person> getTo() {
        return to;
    }

    public Person getCc() {
        return cc;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nSENDER : ").append(from);
        sb.append("\nTO : ");
        for(Person p : to) {
            sb.append(p).append(", ");
        }
        sb.append("\nSUBJECT : ").append(subject);
        sb.append("\nCONTENT : ").append(content);
        return sb.toString();
    }
}
