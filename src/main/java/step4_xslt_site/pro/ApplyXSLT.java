package step4_xslt_site.pro;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * Transformation XSLT version pro.
 * Transforme le XML pro en page HTML avec labels et localisations.
 */
public class ApplyXSLT {
    public static void main(String[] args) {
        String xmlPath = "src/main/java/step1_csv_to_xml/pro/output.xml";
        String xslPath = "src/main/java/step4_xslt_site/pro/style.xsl";
        String htmlPath = "src/main/java/step4_xslt_site/pro/output.html";
        
        if (args.length >= 3) {
            xmlPath = args[0];
            xslPath = args[1];
            htmlPath = args[2];
        }
        
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new File(xslPath)));
            
            // Appliquer la transformation
            transformer.transform(new StreamSource(new File(xmlPath)), new StreamResult(new File(htmlPath)));
            
            System.out.println("Transformation XSLT réussie !");
            System.out.println("HTML généré : " + new File(htmlPath).getAbsolutePath());
        } catch (TransformerException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}


