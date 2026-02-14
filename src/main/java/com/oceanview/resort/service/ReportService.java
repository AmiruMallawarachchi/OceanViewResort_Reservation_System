package com.oceanview.resort.service;

import com.oceanview.resort.dto.ReportDTO;
import com.oceanview.resort.dto.DashboardSummaryDTO;

import java.util.List;

public interface ReportService {
    ReportDTO generate(ReportDTO dto, long generatedById);
    ReportDTO findById(long id);
    List<ReportDTO> findAll();
    byte[] getContent(long id);
    String getFormat(long id);
    /**
     * Provides a summary view of key KPIs for the admin dashboard.
     */
    DashboardSummaryDTO getDashboardSummary();
}
