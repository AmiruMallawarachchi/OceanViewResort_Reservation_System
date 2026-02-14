package com.oceanview.resort.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.service.ReportExporter;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Strategy implementation: export report data as PDF.
 */
public class PdfReportExporter implements ReportExporter {

    @Override
    public byte[] export(String title, List<String[]> rows) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph(title, new Font(Font.HELVETICA, 16, Font.BOLD)));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.addCell("Metric");
            table.addCell("Value");
            for (String[] row : rows) {
                table.addCell(row[0]);
                table.addCell(row[1]);
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate PDF report", ex);
        }
    }

    @Override
    public String getFormatKey() {
        return ReportFormat.PDF.name();
    }
}
