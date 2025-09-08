package com.parser.core.report.util;

import java.util.List;
import java.util.stream.Collectors;

public class CsvUtil {
    private static final String BOM = "\uFEFF";

    public static String createCsvDocumentWithBom(List<String> headers, List<List<Object>> dataRows) {
        StringBuilder sb = new StringBuilder();
        sb.append(BOM);
        sb.append(String.join(",", headers)).append("\n");
        for (List<Object> row : dataRows) {
            sb.append(row.stream()
                    .map(obj -> obj == null ? "" : obj.toString().replace("\"", "\"\""))
                    .map(val -> "\"" + val + "\"")
                    .collect(Collectors.joining(",")))
              .append("\n");
        }
        return sb.toString();
    }
} 