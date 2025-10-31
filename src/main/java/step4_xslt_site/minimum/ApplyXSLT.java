package step4_xslt_site.minimum;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * Transformation XSLT minimale.
 * Transforme le XML minimum en page HTML simple.
 */
public class ApplyXSLT {
    public static void main(String[] args) {
        String xmlPath = "src/main/java/step1_csv_to_xml/minimum/output.xml";
        String xslPath = "src/main/java/step4_xslt_site/minimum/style.xsl";
        String htmlPath = "src/main/java/step4_xslt_site/minimum/output.html";
        
        if (args.length >= 3) {
            xmlPath = args[0];
            xslPath = args[1];
            htmlPath = args[2];
        }
        
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new File(xslPath)));
            transformer.transform(new StreamSource(new File(xmlPath)), new StreamResult(new File(htmlPath)));
            System.out.println("HTML généré : " + htmlPath);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}


