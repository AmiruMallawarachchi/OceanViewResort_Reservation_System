package com.oceanview.resort.util;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public final class ReportExportUtil {
    private ReportExportUtil() {
    }

    public static byte[] generatePdf(String title, List<String[]> rows) {
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

    public static byte[] generateExcel(String title, List<String[]> rows) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Report");
            int rowIdx = 0;

            Row titleRow = sheet.createRow(rowIdx++);
            titleRow.createCell(0).setCellValue(title);

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Metric");
            header.createCell(1).setCellValue("Value");

            for (String[] row : rows) {
                Row sheetRow = sheet.createRow(rowIdx++);
                sheetRow.createCell(0).setCellValue(row[0]);
                sheetRow.createCell(1).setCellValue(row[1]);
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate Excel report", ex);
        }
    }
}
