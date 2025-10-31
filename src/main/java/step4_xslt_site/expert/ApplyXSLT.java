package step4_xslt_site.expert;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * Transformation XSLT version expert.
 * Transforme le XML expert en page HTML avec mesure de performance.
 */
public class ApplyXSLT {
    public static void main(String[] args) {
        String xmlPath = "src/main/java/step1_csv_to_xml/expert/output.xml";
        String xslPath = "src/main/java/step4_xslt_site/expert/style.xsl";
        String htmlPath = "src/main/java/step4_xslt_site/expert/output.html";
        
        if (args.length >= 3) {
            xmlPath = args[0];
            xslPath = args[1];
            htmlPath = args[2];
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new File(xslPath)));
            
            // Appliquer la transformation
            transformer.transform(new StreamSource(new File(xmlPath)), new StreamResult(new File(htmlPath)));
            
            long elapsed = System.currentTimeMillis() - startTime;
            
            System.out.println("Transformation XSLT réussie !");
            System.out.println("Temps de transformation : " + elapsed + " ms");
            System.out.println("HTML généré : " + new File(htmlPath).getAbsolutePath());
            System.out.println("\nOuvrez le fichier dans votre navigateur pour voir le résultat.");
            
        } catch (TransformerException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}


