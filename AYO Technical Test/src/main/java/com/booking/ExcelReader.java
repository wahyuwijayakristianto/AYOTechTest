package com.booking;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExcelReader {

    public static List<Map<String, String>> read(String filePath) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();

        ZipFile zip = new ZipFile(filePath);

        List<String> sharedStrings = readSharedStrings(zip);

        InputStream is = zip.getInputStream(zip.getEntry("xl/worksheets/sheet1.xml"));
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(is);
        doc.getDocumentElement().normalize();

        NodeList rows = doc.getElementsByTagName("row");
        List<String> headers = new ArrayList<>();

        for (int i = 0; i < rows.getLength(); i++) {
            Element row = (Element) rows.item(i);
            int rowNum = Integer.parseInt(row.getAttribute("r"));
            List<String> values = getCellValues(row, sharedStrings);

            if (rowNum == 1) {
                headers = values;
            } else {
                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    rowData.put(headers.get(j), j < values.size() ? values.get(j) : "");
                }
                result.add(rowData);
            }
        }

        zip.close();
        return result;
    }

    private static List<String> readSharedStrings(ZipFile zip) throws Exception {
        List<String> list = new ArrayList<>();
        ZipEntry entry = zip.getEntry("xl/sharedStrings.xml");
        if (entry == null) return list;

        InputStream is = zip.getInputStream(entry);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(is);
        doc.getDocumentElement().normalize();

        NodeList nodes = doc.getElementsByTagName("t");
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(nodes.item(i).getTextContent());
        }
        return list;
    }

    private static List<String> getCellValues(Element row, List<String> sharedStrings) {
        List<String> values = new ArrayList<>();
        NodeList cells = row.getElementsByTagName("c");
        int lastCol = 0;

        for (int i = 0; i < cells.getLength(); i++) {
            Element cell = (Element) cells.item(i);
            int colIdx = colToNumber(cell.getAttribute("r").replaceAll("[0-9]", ""));
            String type = cell.getAttribute("t");

            while (lastCol < colIdx - 1) {
                values.add("");
                lastCol++;
            }

            String value = "";
            NodeList t = cell.getElementsByTagName("t");
            NodeList v = cell.getElementsByTagName("v");

            if (type.equals("s") && v.getLength() > 0) {
                int idx = Integer.parseInt(v.item(0).getTextContent().trim());
                value = sharedStrings.get(idx);
            } else if (type.equals("inlineStr") && t.getLength() > 0) {
                value = t.item(0).getTextContent().trim();
            } else if (v.getLength() > 0) {
                value = v.item(0).getTextContent().trim();
            }

            values.add(value);
            lastCol = colIdx;
        }

        return values;
    }

    private static int colToNumber(String col) {
        int result = 0;
        for (char c : col.toCharArray()) {
            result = result * 26 + (c - 'A' + 1);
        }
        return result;
    }
}
