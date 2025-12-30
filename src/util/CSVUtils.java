package util;

import java.io.*;
import java.util.*;

public class CSVUtils {

    // robustness: handles commas inside quotes
    public static List<String> parseLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes; // toggle state
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString().trim());
                field.setLength(0); // reset buffer
            } else {
                field.append(c);
            }
        }
        result.add(field.toString().trim());
        return result;
    }

    public static String toCSVField(String field) {
        if (field == null) return "";
        if (field.contains(",")) {
            return "\"" + field + "\"";
        }
        return field;
    }
}