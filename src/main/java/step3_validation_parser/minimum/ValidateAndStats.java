package step3_validation_parser.minimum;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Parseur SAX minimal validant un document XML via la DTD et calculant
 * quelques statistiques simples.  Respecte les exigences « Au minimum » :
 * utilisation de SAX et validation DTD.
 */
public class ValidateAndStats {
    public static void main(String[] args) {
        // Valeur par défaut si aucun argument n'est fourni
        String xmlPath = "src/main/java/step1_csv_to_xml/minimum/output.xml";
        
        if (args.length >= 1) {
            xmlPath = args[0];
        }
        
        File xmlFile = new File(xmlPath);
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            Handler handler = new Handler();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            reader.setEntityResolver((publicId, systemId) -> {
                // rediriger vers la DTD minimale
                if (systemId != null && systemId.endsWith("images.dtd")) {
                    try {
                        // Essayer plusieurs chemins possibles
                        File dtdFile = new File(xmlFile.getParentFile(), "../step2_structures/minimum/images.dtd");
                        if (!dtdFile.exists()) {
                            dtdFile = new File(xmlFile.getParentFile(), "../step2_structures/images.dtd");
                        }
                        if (!dtdFile.exists()) {
                            dtdFile = new File("src/main/java/step2_structures/minimum/images.dtd");
                        }
                        if (!dtdFile.exists()) {
                            dtdFile = new File("src/main/java/step2_structures/images.dtd");
                        }
                        return new InputSource(new FileInputStream(dtdFile.getCanonicalFile()));
                    } catch (Exception e) {
                        System.err.println("Erreur lors de la résolution de la DTD: " + e.getMessage());
                    }
                }
                return null;
            });
            reader.parse(xmlFile.toURI().toString());
            System.out.println("Images avec 'loc right' : " + handler.getLocRightCount());
            System.out.println("Top labels : " + handler.getTopLabels(10));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static class Handler extends DefaultHandler {
        private boolean inLocalization = false;
        private boolean inLabel = false;
        private boolean hasLocRight = false;
        private List<String> labels = new ArrayList<>();
        private Map<String, Integer> labelCounts = new HashMap<>();
        private int locRightCount = 0;
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attrs) {
            if ("image".equals(qName)) {
                hasLocRight = false;
                labels.clear();
            } else if ("localization".equals(qName)) {
                inLocalization = true;
            } else if ("label".equals(qName)) {
                inLabel = true;
            }
        }
        @Override
        public void endElement(String uri, String localName, String qName) {
            if ("image".equals(qName)) {
                if (hasLocRight) locRightCount++;
                for (String lbl : labels) labelCounts.merge(lbl, 1, Integer::sum);
            } else if ("localization".equals(qName)) {
                inLocalization = false;
            } else if ("label".equals(qName)) {
                inLabel = false;
            }
        }
        @Override
        public void characters(char[] ch, int start, int length) {
            String txt = new String(ch, start, length).trim();
            if (txt.isEmpty()) return;
            if (inLocalization && "loc right".equalsIgnoreCase(txt)) {
                hasLocRight = true;
            } else if (inLabel) {
                labels.add(txt);
            }
        }
        public int getLocRightCount() { return locRightCount; }
        public List<String> getTopLabels(int n) {
            List<Map.Entry<String,Integer>> entries = new ArrayList<>(labelCounts.entrySet());
            entries.sort((a,b) -> b.getValue().compareTo(a.getValue()));
            List<String> result = new ArrayList<>();
            for (int i = 0; i < Math.min(n, entries.size()); i++) {
                result.add(entries.get(i).getKey() + " (" + entries.get(i).getValue() + ")");
            }
            return result;
        }
        // Validation errors
        @Override public void error(SAXParseException e) throws SAXException { throw e; }
        @Override public void fatalError(SAXParseException e) throws SAXException { throw e; }
    }
}