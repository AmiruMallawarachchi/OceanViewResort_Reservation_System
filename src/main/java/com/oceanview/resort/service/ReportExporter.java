package com.oceanview.resort.service;

import java.util.List;

/**
 * Strategy interface for exporting report content in different formats.
 * Implementations (PDF, Excel, etc.)
 */
public interface ReportExporter {


    byte[] export(String title, List<String[]> rows);

    String getFormatKey();
}
