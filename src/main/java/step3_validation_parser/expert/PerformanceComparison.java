package step3_validation_parser.expert;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.*;
import javax.xml.validation.*;
import javax.xml.XMLConstants;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Programme expert comparant les performances des parseurs SAX et DOM sur
 * le document XML issu de la conversion.  Il mesure le temps d'exécution
 * et l'empreinte mémoire approximative pour chacune des approches et
 * présente les statistiques demandées.
 */
public class PerformanceComparison {
    public static void main(String[] args) throws Exception {
        // Valeur par défaut si aucun argument n'est fourni
        String xmlPath = "src/main/java/step1_csv_to_xml/expert/output.xml";
        
        if (args.length >= 1) {
            xmlPath = args[0];
        }
        
        File xmlFile = new File(xmlPath);
        // SAX
        long saxTime, domTime;
        long saxMemBefore, saxMemAfter, domMemBefore, domMemAfter;
        Runtime rt = Runtime.getRuntime();
        System.gc();
        saxMemBefore = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long start = System.currentTimeMillis();
        Stats saxStats = runSax(xmlFile);
        saxTime = System.currentTimeMillis() - start;
        System.gc();
        saxMemAfter = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        // DOM
        System.gc();
        domMemBefore = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        start = System.currentTimeMillis();
        Stats domStats = runDom(xmlFile);
        domTime = System.currentTimeMillis() - start;
        System.gc();
        domMemAfter = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        // Affichage
        System.out.println("--- SAX ---");
        System.out.println("Temps : " + saxTime + " ms");
        System.out.println("Mémoire utilisée : " + (saxMemAfter - saxMemBefore) + " Mo");
        System.out.println("loc right : " + saxStats.locRight);
        System.out.println("Top 5 labels : " + saxStats.topLabels);
        System.out.println("--- DOM ---");
        System.out.println("Temps : " + domTime + " ms");
        System.out.println("Mémoire utilisée : " + (domMemAfter - domMemBefore) + " Mo");
        System.out.println("loc right : " + domStats.locRight);
        System.out.println("Top 5 labels : " + domStats.topLabels);
    }
    private static Stats runSax(File xmlFile) throws Exception {
        Stats stats = new Stats();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        DefaultHandler handler = new DefaultHandler() {
            private boolean inLocalization = false;
            private boolean inLabel = false;
            private boolean hasLocRight = false;
            private List<String> labels = new ArrayList<>();

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
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
                    if (hasLocRight) stats.locRight++;
                    for (String lbl : labels) stats.labelCounts.merge(lbl, 1, Integer::sum);
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
        };
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        // Ajouter un EntityResolver pour trouver la DTD
        reader.setEntityResolver((publicId, systemId) -> {
            if (systemId != null && systemId.endsWith("images.dtd")) {
                try {
                    File dtdFile = new File(xmlFile.getParentFile(), "../step2_structures/expert/images.dtd");
                    if (!dtdFile.exists()) {
                        dtdFile = new File(xmlFile.getParentFile(), "../step2_structures/images.dtd");
                    }
                    if (!dtdFile.exists()) {
                        dtdFile = new File("src/main/java/step2_structures/expert/images.dtd");
                    }
                    if (!dtdFile.exists()) {
                        dtdFile = new File("src/main/java/step2_structures/images.dtd");
                    }
                    return new InputSource(new FileInputStream(dtdFile.getCanonicalFile()));
                } catch (Exception ignored) {}
            }
            return null;
        });
        reader.parse(xmlFile.toURI().toString());
        stats.computeTopLabels(5);
        return stats;
    }
    private static Stats runDom(File xmlFile) throws Exception {
        Stats stats = new Stats();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        // Ajouter un EntityResolver pour trouver la DTD
        builder.setEntityResolver((publicId, systemId) -> {
            if (systemId != null && systemId.endsWith("images.dtd")) {
                try {
                    File dtdFile = new File(xmlFile.getParentFile(), "../step2_structures/expert/images.dtd");
                    if (!dtdFile.exists()) {
                        dtdFile = new File(xmlFile.getParentFile(), "../step2_structures/images.dtd");
                    }
                    if (!dtdFile.exists()) {
                        dtdFile = new File("src/main/java/step2_structures/expert/images.dtd");
                    }
                    if (!dtdFile.exists()) {
                        dtdFile = new File("src/main/java/step2_structures/images.dtd");
                    }
                    return new InputSource(new FileInputStream(dtdFile.getCanonicalFile()));
                } catch (Exception ignored) {}
            }
            return null;
        });
        Document doc = builder.parse(xmlFile);
        NodeList images = doc.getElementsByTagName("image");
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
            if (hasLocRight) stats.locRight++;
            NodeList labels = image.getElementsByTagName("label");
            for (int j = 0; j < labels.getLength(); j++) {
                String lbl = labels.item(j).getTextContent().trim();
                if (!lbl.isEmpty()) stats.labelCounts.merge(lbl, 1, Integer::sum);
            }
        }
        stats.computeTopLabels(5);
        return stats;
    }
    // Classe utilitaire pour stocker les résultats
    private static class Stats {
        int locRight = 0;
        Map<String,Integer> labelCounts = new HashMap<>();
        List<String> topLabels = new ArrayList<>();
        void computeTopLabels(int n) {
            List<Map.Entry<String,Integer>> entries = new ArrayList<>(labelCounts.entrySet());
            entries.sort((a,b) -> b.getValue().compareTo(a.getValue()));
            for (int i = 0; i < Math.min(n, entries.size()); i++) {
                Map.Entry<String,Integer> e = entries.get(i);
                topLabels.add(e.getKey() + "(" + e.getValue() + ")");
            }
        }
    }
}