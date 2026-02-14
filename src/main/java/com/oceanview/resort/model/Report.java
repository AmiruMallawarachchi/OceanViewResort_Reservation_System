package com.oceanview.resort.model;

import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.model.enums.ReportType;
import java.time.LocalDateTime;

public class Report {
    private long id;
    private ReportType reportType;
    private ReportFormat format;
    private User generatedBy;
    private LocalDateTime generatedAt;
    private String parameters;
    private byte[] content;

    public Report() {
    }

    public Report(long id, ReportType reportType, ReportFormat format, User generatedBy, LocalDateTime generatedAt,
                  String parameters, byte[] content) {
        this.id = id;
        this.reportType = reportType;
        this.format = format;
        this.generatedBy = generatedBy;
        this.generatedAt = generatedAt;
        this.parameters = parameters;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public ReportFormat getFormat() {
        return format;
    }

    public void setFormat(ReportFormat format) {
        this.format = format;
    }

    public User getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(User generatedBy) {
        this.generatedBy = generatedBy;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
