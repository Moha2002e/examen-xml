package step1_csv_to_xml.minimum;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Convertisseur CSV→XML de niveau minimum.
 *
 * <p>Cette version lit un fichier CSV séparé par des virgules sans gérer les
 * champs contenant des virgules ou des guillemets.  Elle génère un fichier
 * XML simple en utilisant uniquement quelques colonnes : l'ID de l'image,
 * l'ID du patient et la position de vue.  Aucune structure de listes n'est
 * interprétée et aucune validation n'est effectuée.  Cette implémentation
 * répond au niveau « Au minimum » de l'énoncé.</p>
 */
public class CSVToXML {
    public static void main(String[] args) {
        // Valeurs par défaut si aucun argument n'est fourni
        String csvPath = "src/main/java/data/PADCHEST_chest_x_ray_images_labels_160K_01.02.19.csv";
        String xmlPath = "src/main/java/step1_csv_to_xml/minimum/output.xml";
        
        if (args.length >= 2) {
            csvPath = args[0];
            xmlPath = args[1];
        }
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvPath), StandardCharsets.UTF_8));
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlPath), StandardCharsets.UTF_8))) {
            String header = br.readLine();
            if (header == null) {
                throw new IOException("Fichier CSV vide");
            }
            String[] columns = header.split(",");
            // Trouver les indices des colonnes d'intérêt
            int idxImage = -1, idxPatient = -1, idxView = -1;
            for (int i = 0; i < columns.length; i++) {
                String col = columns[i].trim();
                if (col.equals("ImageID")) idxImage = i;
                if (col.equals("PatientID")) idxPatient = i;
                if (col.equals("ViewPosition_DICOM")) idxView = i;
            }
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<!DOCTYPE images SYSTEM \"../../step2_structures/minimum/images.dtd\">\n");
            bw.write("<images>\n");
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length <= Math.max(idxImage, Math.max(idxPatient, idxView))) continue;
                String img = fields[idxImage];
                String patient = fields[idxPatient];
                String view = fields[idxView];
                bw.write("  <image id=\"" + escape(img) + "\">\n");
                bw.write("    <patientId>" + escape(patient) + "</patientId>\n");
                bw.write("    <viewPosition>" + escape(view) + "</viewPosition>\n");
                bw.write("  </image>\n");
            }
            bw.write("</images>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}