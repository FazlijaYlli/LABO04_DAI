package ch.heigvd.config;

import ch.heigvd.util.Mail;
import ch.heigvd.util.Person;
import ch.heigvd.util.Group;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.lang.Integer.parseInt;

public class Configurator {
    public static int MIN_GROUP_SIZE = 3;
    private int nbGroups;
    private String serverAdress;
    private int serverPort;
    private Person carbonTarget;

    private List<Group> groups;
    private List<Mail> mails;

    public Configurator() throws Exception {
        updateSettings(".\\src\\main\\config\\settings.properties");
        updateProfiles(".\\src\\main\\config\\profiles.xml");
        for (Group g : groups) {
            g.chooseSender();
        }
        updateMails(".\\src\\main\\config\\mails.xml");
    }

    private void updateSettings(String path) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
        Properties p = new Properties();
        p.load(r);
        serverAdress = p.getProperty("serverAdress");
        serverPort = parseInt(p.getProperty("serverPort"));
        carbonTarget = new Person(p.getProperty("carbonTarget"));
        nbGroups = parseInt(p.getProperty("nbGroups"));
    }

    private void updateProfiles(String path) throws ParserConfigurationException, IOException, SAXException {
        File xml = new File(path);
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        Document doc = b.parse(xml);
        groups = new ArrayList<>();

        NodeList people = doc.getElementsByTagName("person");
        for (int i = 0; i < nbGroups; i++) {
            List<Person> l = new ArrayList<>();
            groups.add(new Group(i,l));

            for (int j = 0 ; j < people.getLength() ; ++j) {
                Node n = people.item(j);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) n;
                    Person p = new Person(e.getElementsByTagName("address").item(0).getTextContent());

                    for (int k = 0 ; k < e.getElementsByTagName("group").getLength() ; ++k) {
                        if (parseInt(e.getElementsByTagName("group").item(k).getTextContent()) == i) {
                            groups.get(i).getReceivers().add(p);
                        }
                    }
                }
            }
        }
    }

    private void updateMails(String path) throws ParserConfigurationException, IOException, SAXException {
        File xml = new File(path);
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        Document doc = b.parse(xml);
        mails = new ArrayList<>();

        NodeList mail_nodes = doc.getElementsByTagName("mail");

        for (int i = 0; i < nbGroups; i++) {
            for (int j = 0 ; j < mail_nodes.getLength() ; ++j) {
                Node n = mail_nodes.item(j);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) n;
                    String subject = e.getElementsByTagName("subject").item(0).getTextContent();
                    String content = e.getElementsByTagName("content").item(0).getTextContent();
                    mails.add(new Mail(subject, content, groups.get(i).getSender(), groups.get(i).getReceivers(), carbonTarget));
                }
            }
        }
    }

    public int getNbGroups() {
        return nbGroups;
    }

    public String getServerAdress() {
        return serverAdress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public Person getCarbonTarget() {
        return carbonTarget;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public List<Mail> getMails() {
        return mails;
    }
}
