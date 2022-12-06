package ch.heigvd.config;

import ch.heigvd.smtp.SmtpClient;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;


// Permets d'accéder aux fichiers de configurations et créer les différents groupes et mails
// demandées par le cahier des charges.
public class PrankRobot {
    // Taille d'un groupe minimum.
    public static int MIN_GROUP_SIZE = 3;

    // Objet logger nous permettant de lire les retours du serveur depuis notre application client.
    private static final Logger LOGGER = Logger.getLogger(PrankRobot.class.getName());
    private static final String emailValidationRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(.[A-Za-z0-9-]+)*(.[A-Za-z]{2,})$";
    // Variables à configurer.
    private int nbGroups;
    private String serverAdress;
    private int serverPort;
    private Person carbonTarget;

    private List<Group> groups;
    private List<Mail> mails;

    // Quand le configurator est créé, il lit les fichiers de configurations et crée les objets adéquats.
    public PrankRobot() throws Exception {
        LOGGER.log(Level.INFO, "\n" + """
                ===============================================
                |          PRANKROBOT STARTING...             |
                ===============================================
                """);
        updateSettings("..\\src\\main\\config\\settings.properties");
        updateProfiles("..\\src\\main\\config\\profiles.xml");
        updateMails("..\\src\\main\\config\\mails.xml");
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
        if(nbGroups <= 0) {
            throw new RuntimeException("Nombre de groupes erronées !");
        }
        if (!(Pattern.compile(emailValidationRegex).matcher(carbonTarget.getAddress()).matches())) {
            throw new RuntimeException("Addresse <"+carbonTarget+"> de copie carbone est erronée !");
        }
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
        NodeList nodes_addresses = doc.getElementsByTagName("address");

        if(nodes_addresses.getLength() < 3) {
            throw new RuntimeException("Il n'y a pas assez de personnes pour créer des groupes !");
        }

        ArrayList<String> addresses = new ArrayList<>();

        for (int i = 0; i < nodes_addresses.getLength(); i++) {
            String a = nodes_addresses.item(i).getTextContent();
            if (Pattern.compile(emailValidationRegex).matcher(a).matches()) {
                addresses.add(a);
            } else {
                LOGGER.log(Level.WARNING,"\u001B[33m" + "Addresse <" + a + "> n'est pas une adresse e-mail valide !" + "\u001B[0m");
            }
        }
        
        for (int i = 0; i < nbGroups; i++) {

            // On ajoute un groupe par itération dans la liste des groupes.
            List<Person> l = new ArrayList<>();
            groups.add(new Group(i,l));
            
            // On ajoute 1 pour obtenir les valeurs corectes avec nextInt car max exclusif.
            int randGroupSize = ThreadLocalRandom.current().nextInt(3, addresses.size() + 1);

            //  Itération sur le nombre de personnes aléatoire
            List<Integer> usedIds = new ArrayList<>();
            for (int j = 0 ; j < randGroupSize ; ++j) {
                int randPerson;
                do {
                    randPerson = ThreadLocalRandom.current().nextInt(0, addresses.size());
                } while (usedIds.contains(randPerson));

                usedIds.add(randPerson);
                Person p = new Person(addresses.get(randPerson));
                groups.get(i).getReceivers().add(p);
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
            int randNbMailToSend = ThreadLocalRandom.current().nextInt(1, mail_nodes.getLength() + 1);

            //  Itération sur le nombre de personnes
            List<Integer> usedMails = new ArrayList<>();
            for (int j = 0 ; j < randNbMailToSend ; ++j) {

                int randMail;
                do {
                    randMail = ThreadLocalRandom.current().nextInt(0, mail_nodes.getLength());
                } while (usedMails.contains(randMail));
                usedMails.add(randMail);

                // On séléctionne le contenu de chaque mail
                Node n = mail_nodes.item(randMail);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    // On crée un mail avec les éléments contenus dans le fichier et on l'ajoute à la liste
                    // des mails du configurator.
                    Element e = (Element) n;
                    String subject = e.getElementsByTagName("subject").item(0).getTextContent();
                    String content = e.getElementsByTagName("content").item(0).getTextContent();
                    groups.get(i).getMailstoSend().add(new Mail(subject, content, groups.get(i).getSender(), groups.get(i).getReceivers(), carbonTarget));
                }
            }
        }
    }

    public void prank() throws IOException {
        SmtpClient client = new SmtpClient(getServerAdress(), getServerPort());
        for(Group g : groups) {
            for(Mail m : g.getMailstoSend()) {
                client.send(m);
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
