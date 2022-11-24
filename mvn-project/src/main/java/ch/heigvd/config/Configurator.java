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

// Permets d'accéder aux fichiers de configurations et créer les différents groupes et mails
// demandées par le cahier des charges.
public class Configurator {
    // Taille d'un groupe minimum.
    public static int MIN_GROUP_SIZE = 3;

    // Variables à configurer.
    private int nbGroups;
    private String serverAdress;
    private int serverPort;
    private Person carbonTarget;

    private List<Group> groups;
    private List<Mail> mails;

    // Quand le configurator est créé, il lit les fichiers de configurations et crée les objets adéquats.
    public Configurator() throws Exception {
        updateSettings(".\\src\\main\\config\\settings.properties");
        updateProfiles(".\\src\\main\\config\\profiles.xml");
        updateMails(".\\src\\main\\config\\mails.xml");
    }

    // Va chercher les paramètres dans un fichier "properties".
    private void updateSettings(String path) throws IOException {
        // Création d'un reader sur le fichier
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));

        // Utilisation d'un objet properties
        Properties p = new Properties();
        p.load(r);
        serverAdress = p.getProperty("serverAdress");
        serverPort = parseInt(p.getProperty("serverPort"));
        carbonTarget = new Person(p.getProperty("carbonTarget"));
        nbGroups = parseInt(p.getProperty("nbGroups"));
    }

    private void updateProfiles(String path) throws ParserConfigurationException, IOException, SAXException {
        // Création des objets requis pour parser des fichiers XML.
        File xml = new File(path);
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        Document doc = b.parse(xml);

        // Initialisation de la liste des groupes.
        groups = new ArrayList<>();

        // On itère sur le nombre de groupe
        NodeList people = doc.getElementsByTagName("person");
        for (int i = 0; i < nbGroups; i++) {

            // On ajoute un groupe par itération dans la liste des groupes.
            List<Person> l = new ArrayList<>();
            groups.add(new Group(i,l));

            //  Itération sur le nombre de personnes
            for (int j = 0 ; j < people.getLength() ; ++j) {

                Node n = people.item(j);
                if (n.getNodeType() == Node.ELEMENT_NODE) {

                    // Ajout de chaque personne au groupe correspond si et seulement s'il possède un id correspondant
                    // à l'id du groupe qui itère actuellement.
                    Element e = (Element) n;
                    Person p = new Person(e.getElementsByTagName("address").item(0).getTextContent());

                    for (int k = 0 ; k < e.getElementsByTagName("group").getLength() ; ++k) {
                        if (parseInt(e.getElementsByTagName("group").item(k).getTextContent()) == i) {
                            groups.get(i).getReceivers().add(p);
                        }
                    }
                }
            }
            // On choisit le sender du groupe.
            groups.get(i).chooseSender();
        }
    }

    private void updateMails(String path) throws ParserConfigurationException, IOException, SAXException {
        // Création des objets requis pour parser des fichiers XML.
        File xml = new File(path);
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        DocumentBuilder b = f.newDocumentBuilder();
        Document doc = b.parse(xml);
        mails = new ArrayList<>();

        // Prendre tous les éléments <mail>
        NodeList mail_nodes = doc.getElementsByTagName("mail");

        // On itère sur les groupes
        for (int i = 0; i < nbGroups; i++) {
            // Puis on itère sur le nombre de mails dans le fichier mails.xml
            for (int j = 0 ; j < mail_nodes.getLength() ; ++j) {
                // On séléctionne le contenu de chaque mail
                Node n = mail_nodes.item(j);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    // On crée un mail avec les éléments contenus dans le fichier et on l'ajoute à la liste
                    // des mails du configurator.
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
