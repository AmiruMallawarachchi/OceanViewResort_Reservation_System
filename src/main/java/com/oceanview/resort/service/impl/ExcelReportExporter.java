package com.oceanview.resort.service.impl;

import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.service.ReportExporter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Strategy implementation: export report data as Excel.
 */
public class ExcelReportExporter implements ReportExporter {

    @Override
    public byte[] export(String title, List<String[]> rows) {
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

    @Override
    public String getFormatKey() {
        return ReportFormat.EXCEL.name();
    }
}
