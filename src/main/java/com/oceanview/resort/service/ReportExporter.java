package com.oceanview.resort.service;

import java.util.List;

/**
 * Strategy interface for exporting report content in different formats.
 * Implementations (PDF, Excel, etc.) can be added without changing report generation logic (Open/Closed Principle).
 */
public interface ReportExporter {

    /**
     * Export report rows to binary content in the format supported by this exporter.
     *
     * @param title report title
     * @param rows  list of [metric, value] pairs
     * @return exported bytes (e.g. PDF or Excel)
     */
    byte[] export(String title, List<String[]> rows);

    /**
     * Format identifier (e.g. "PDF", "EXCEL") used to select this strategy.
     */
    String getFormatKey();
}
