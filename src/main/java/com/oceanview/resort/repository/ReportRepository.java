package com.oceanview.resort.repository;

import com.oceanview.resort.model.Report;

import java.util.List;

public interface ReportRepository {
    Report create(Report report);
    Report findById(long id);
    List<Report> findAll();
}
