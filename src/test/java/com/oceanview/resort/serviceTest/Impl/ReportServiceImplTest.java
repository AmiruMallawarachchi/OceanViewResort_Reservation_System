package com.oceanview.resort.serviceTest.Impl;

import com.oceanview.resort.dto.ReportDTO;
import com.oceanview.resort.model.Report;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.model.enums.ReportType;
import com.oceanview.resort.repository.*;
import com.oceanview.resort.service.ReportExporter;
import com.oceanview.resort.service.impl.ReportServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private BillRepository billRepository;
    @Mock
    private GuestRepository guestRepository;
    @Mock
    private ReportExporter pdfExporter;

    private ReportServiceImpl reportService;

    @Before
    public void setup() {
        Map<String, ReportExporter> exporters = new HashMap<>();
        exporters.put("PDF", pdfExporter);
        exporters.put("EXCEL", pdfExporter); // use same mock for both for simplicity
        reportService = new ReportServiceImpl(reportRepository, userRepository, reservationRepository,
                roomRepository, billRepository, guestRepository, exporters);
    }

    @Test
    public void testGenerate_success_returnsReportDTO() {
        ReportDTO dto = new ReportDTO();
        dto.setReportType("OCCUPANCY");
        dto.setFormat("PDF");
        dto.setPeriod("DAILY");

        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        when(userRepository.findById(1L)).thenReturn(user);
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());
        when(pdfExporter.export(any(String.class), any())).thenReturn(new byte[]{1, 2, 3});

        Report saved = new Report();
        saved.setId(10L);
        saved.setReportType(ReportType.OCCUPANCY);
        saved.setFormat(ReportFormat.PDF);
        when(reportRepository.create(any(Report.class))).thenReturn(saved);

        ReportDTO result = reportService.generate(dto, 1L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(reportRepository).create(any(Report.class));
        verify(pdfExporter).export(any(String.class), any());
    }

    @Test(expected = IllegalStateException.class)
    public void testGenerate_noExporterRegistered_throws() {
        ReportServiceImpl serviceNoExporters = new ReportServiceImpl(reportRepository, userRepository,
                reservationRepository, roomRepository, billRepository, guestRepository, new HashMap<>());
        ReportDTO dto = new ReportDTO();
        dto.setReportType("OCCUPANCY");
        dto.setFormat("PDF");
        // roomRepository.findAll() is called in buildReportRows before exception
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());
        serviceNoExporters.generate(dto, 1L);
    }
}
