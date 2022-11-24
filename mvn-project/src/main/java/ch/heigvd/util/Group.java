package ch.heigvd.util;

import java.util.Collections;
import java.util.List;

import ch.heigvd.config.Configurator;

// Sert à stocker une liste de personnes, un envoyeur et un id.
// Permets de savoir qui doit envoyer un mail et à qui.
public class Group {
    private final int id;
    private final List<Person> receivers;

    private Person sender;

    public Group(int id, List<Person> receivers) {
        this.id = id;
        this.receivers = receivers;
    }

    public void chooseSender() {
        if (this.receivers.size() < Configurator.MIN_GROUP_SIZE) {
            throw new RuntimeException("Group is below the size required. The minimum size is <"
                    + Configurator.MIN_GROUP_SIZE + ">");
        }
        Collections.shuffle(this.receivers);
        this.sender = this.receivers.get(0);
        this.receivers.remove(0);
    }

    public Person getSender() {
        return sender;
    }

    public List<Person> getReceivers() {
        return receivers;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GROUPE ").append(id).append("\nSENDER : ").append(sender.getAddress()).append("\n");
        for(Person p : receivers) {
            sb.append(p.getAddress()).append(", ");
        }
        sb.append("\n");
        return sb.toString();
    }
}
