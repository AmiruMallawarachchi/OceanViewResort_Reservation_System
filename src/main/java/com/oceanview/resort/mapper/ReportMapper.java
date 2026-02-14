package com.oceanview.resort.mapper;

import com.oceanview.resort.dto.ReportDTO;
import com.oceanview.resort.model.Report;
import com.oceanview.resort.util.DateUtil;

public final class ReportMapper {
    private ReportMapper() {
    }

    public static ReportDTO toDTO(Report report) {
        if (report == null) {
            return null;
        }
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setReportType(report.getReportType() == null ? null : report.getReportType().name());
        dto.setFormat(report.getFormat() == null ? null : report.getFormat().name());
        dto.setGeneratedBy(report.getGeneratedBy() == null ? null : report.getGeneratedBy().getUsername());
        dto.setGeneratedAt(DateUtil.formatDateTime(report.getGeneratedAt()));
        dto.setParameters(report.getParameters());
        return dto;
    }
}
