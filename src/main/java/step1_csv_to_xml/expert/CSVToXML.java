package step1_csv_to_xml.expert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Convertisseur CSV→XML de niveau expert.
 *
 * <p>Cette version s'appuie sur la version pro mais ajoute un relevé de
 * performance.  Elle mesure le temps d'exécution et une estimation de la
 * mémoire utilisée pour la conversion.  L'objectif est d'aider à
 * comparer différentes approches (SAX vs DOM ou différentes tailles de
 * buffer) lorsque vous travaillez avec des fichiers volumineux.</p>
 */
public class CSVToXML {
    private static List<String> parseLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result;
    }
    private static List<String> parseArrayField(String listStr) {
        List<String> values = new ArrayList<>();
        if (listStr == null || listStr.isEmpty()) return values;
        String trimmed = listStr.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) trimmed = trimmed.substring(1, trimmed.length() - 1);
        trimmed = trimmed.replace("'", "");
        trimmed = trimmed.replaceAll("\\]\\s*,\\s*\\[", ",");
        trimmed = trimmed.replaceAll("\s*,\s*", ",");
        trimmed = trimmed.replaceAll("\s+", ",");
        for (String token : trimmed.split(",")) {
            String val = token.trim();
            if (!val.isEmpty()) values.add(val);
        }
        return values;
    }
    public static void main(String[] args) {
        // Valeurs par défaut si aucun argument n'est fourni
        String csvPath = "src/main/java/data/PADCHEST_chest_x_ray_images_labels_160K_01.02.19.csv";
        String xmlPath = "src/main/java/step1_csv_to_xml/expert/output.xml";
        int maxRecords = -1;
        
        if (args.length >= 2) {
            csvPath = args[0];
            xmlPath = args[1];
            if (args.length >= 3) {
                try { maxRecords = Integer.parseInt(args[2]); } catch (NumberFormatException ignored) {}
            }
        }
        
        File csvFile = new File(csvPath);
        File xmlFile = new File(xmlPath);
        long startTime = System.currentTimeMillis();
        Runtime rt = Runtime.getRuntime();
        long memBefore = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        int count = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8));
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), StandardCharsets.UTF_8))) {
            String headerLine = br.readLine();
            if (headerLine == null) throw new IOException("Fichier CSV vide");
            List<String> header = parseLine(headerLine);
            Map<String, Integer> indexMap = new HashMap<>();
            for (int i = 0; i < header.size(); i++) indexMap.put(header.get(i), i);
            int idxImageID = indexMap.getOrDefault("ImageID", -1);
            int idxPatientID = indexMap.getOrDefault("PatientID", -1);
            int idxViewPos = indexMap.getOrDefault("ViewPosition_DICOM", -1);
            int idxLabels = indexMap.getOrDefault("Labels", -1);
            int idxLocs = indexMap.getOrDefault("Localizations", -1);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<images xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
            bw.write("xsi:noNamespaceSchemaLocation=\"../../step2_structures/expert/images_extended.xsd\">\n");
            String line;
            while ((line = br.readLine()) != null) {
                if (maxRecords > 0 && count >= maxRecords) break;
                List<String> fields = parseLine(line);
                if (fields.size() != header.size()) continue;
                String imageId = idxImageID >= 0 ? fields.get(idxImageID) : "";
                String patientId = idxPatientID >= 0 ? fields.get(idxPatientID) : "";
                String viewPos = idxViewPos >= 0 ? fields.get(idxViewPos) : "";
                List<String> labels = idxLabels >= 0 ? parseArrayField(fields.get(idxLabels)) : Collections.emptyList();
                List<String> locs = idxLocs >= 0 ? parseArrayField(fields.get(idxLocs)) : Collections.emptyList();
                bw.write("  <image id=\"" + escapeXml(imageId) + "\">\n");
                bw.write("    <patientId>" + escapeXml(patientId) + "</patientId>\n");
                bw.write("    <viewPosition>" + escapeXml(viewPos) + "</viewPosition>\n");
                bw.write("    <labels>\n");
                for (String l : labels) bw.write("      <label>" + escapeXml(l) + "</label>\n");
                bw.write("    </labels>\n");
                bw.write("    <localizations>\n");
                for (String loc : locs) bw.write("      <localization>" + escapeXml(loc) + "</localization>\n");
                bw.write("    </localizations>\n");
                bw.write("  </image>\n");
                count++;
                if (count % 10000 == 0) {
                    System.out.println(count + " enregistrements traités...");
                }
            }
            bw.write("</images>\n");
        } catch (IOException e) { e.printStackTrace(); }
        long endTime = System.currentTimeMillis();
        long memAfter = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        System.out.println("Conversion terminée. Enregistrements : " + count);
        System.out.println("Temps écoulé : " + (endTime - startTime) + " ms");
        System.out.println("Mémoire utilisée (approx.) : " + (memAfter - memBefore) + " Mo");
    }
    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}