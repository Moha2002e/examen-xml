package step3_validation_parser.pro;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.XMLConstants;
import java.io.File;
import java.util.*;

/**
 * Validateur DOM et analyseur statistique pour le niveau pro.
 *
 * <p>Cette version utilise l'API DOM pour charger entièrement le document XML,
 * le valider via le schéma XSD et parcourir l'arbre pour calculer les
 * statistiques requises.  L'utilisation de DOM est plus coûteuse en mémoire
 * mais offre un accès aléatoire à la structure du document.  Cette classe
 * illustre l'approche « Pour les pros ».</p>
 */
public class ValidateAndStats {
    public static void main(String[] args) {
        // Valeur par défaut si aucun argument n'est fourni
        String xmlPath = "src/main/java/step1_csv_to_xml/pro/output.xml";
        
        if (args.length >= 1) {
            xmlPath = args[0];
        }
        
        File xmlFile = new File(xmlPath);
        // Essayer plusieurs chemins possibles pour le XSD
        File xsdFile = new File(xmlFile.getParentFile(), "../../step2_structures/pro/images.xsd");
        if (!xsdFile.exists()) {
            xsdFile = new File("src/main/java/step2_structures/pro/images.xsd");
        }
        if (!xsdFile.exists()) {
            xsdFile = new File("src/main/java/step2_structures/images.xsd");
        }
        try {
            // Valider via XSD
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.validate(new javax.xml.transform.stream.StreamSource(xmlFile));
            // Charger le document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            // Parcourir les images
            NodeList images = doc.getElementsByTagName("image");
            int locRightCount = 0;
            Map<String, Integer> labelCounts = new HashMap<>();
            for (int i = 0; i < images.getLength(); i++) {
                Element image = (Element) images.item(i);
                boolean hasLocRight = false;
                NodeList locs = image.getElementsByTagName("localization");
                for (int j = 0; j < locs.getLength(); j++) {
                    String txt = locs.item(j).getTextContent().trim();
                    if ("loc right".equalsIgnoreCase(txt)) {
                        hasLocRight = true;
                        break;
                    }
                }
                if (hasLocRight) locRightCount++;
                NodeList labels = image.getElementsByTagName("label");
                for (int j = 0; j < labels.getLength(); j++) {
                    String lbl = labels.item(j).getTextContent().trim();
                    if (!lbl.isEmpty()) labelCounts.merge(lbl, 1, Integer::sum);
                }
            }
            System.out.println("Images avec 'loc right' : " + locRightCount);
            // Top 10 labels
            List<Map.Entry<String,Integer>> entries = new ArrayList<>(labelCounts.entrySet());
            entries.sort((a,b) -> b.getValue().compareTo(a.getValue()));
            System.out.println("Top 10 labels :");
            for (int i = 0; i < Math.min(10, entries.size()); i++) {
                Map.Entry<String,Integer> e = entries.get(i);
                System.out.println("- " + e.getKey() + " : " + e.getValue());
            }
        } catch (Exception e) {
            System.err.println("Erreur de validation ou de parsing : " + e.getMessage());
        }
    }
}