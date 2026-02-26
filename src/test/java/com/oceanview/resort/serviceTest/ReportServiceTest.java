package com.oceanview.resort.serviceTest;

import com.oceanview.resort.dto.DashboardSummaryDTO;
import com.oceanview.resort.dto.ReportDTO;
import com.oceanview.resort.model.Report;
import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.repository.*;
import com.oceanview.resort.service.ReportExporter;
import com.oceanview.resort.service.impl.ReportServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

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
        reportService = new ReportServiceImpl(reportRepository, userRepository, reservationRepository,
                roomRepository, billRepository, guestRepository, exporters);
    }

    @Test
    public void testFindById() {
        Report report = new Report();
        report.setId(1L);
        report.setParameters("test");
        when(reportRepository.findById(1L)).thenReturn(report);

        ReportDTO dto = reportService.findById(1L);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        verify(reportRepository).findById(1L);
    }

    @Test
    public void testFindByIdNotFound() {
        when(reportRepository.findById(999L)).thenReturn(null);
        ReportDTO dto = reportService.findById(999L);
        assertNull(dto);
    }

    @Test
    public void testFindAll() {
        Report r1 = new Report();
        r1.setId(1L);
        when(reportRepository.findAll()).thenReturn(Arrays.asList(r1));

        List<ReportDTO> result = reportService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(reportRepository).findAll();
    }

    @Test
    public void testGetContent() {
        Report report = new Report();
        report.setId(1L);
        report.setContent(new byte[]{1, 2, 3});
        when(reportRepository.findById(1L)).thenReturn(report);

        byte[] content = reportService.getContent(1L);
        assertNotNull(content);
        assertArrayEquals(new byte[]{1, 2, 3}, content);
    }

    @Test
    public void testGetContentNotFound() {
        when(reportRepository.findById(999L)).thenReturn(null);
        byte[] content = reportService.getContent(999L);
        assertNull(content);
    }

    @Test
    public void testGetFormat() {
        Report report = new Report();
        report.setId(1L);
        report.setFormat(ReportFormat.PDF);
        when(reportRepository.findById(1L)).thenReturn(report);

        String format = reportService.getFormat(1L);
        assertEquals("PDF", format);
    }

    @Test
    public void testGetFormatNotFound() {
        when(reportRepository.findById(999L)).thenReturn(null);
        assertNull(reportService.getFormat(999L));
    }

    @Test
    public void testGetDashboardSummary() {
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());
        when(reservationRepository.findAll()).thenReturn(Collections.emptyList());
        when(billRepository.findAll()).thenReturn(Collections.emptyList());

        DashboardSummaryDTO dto = reportService.getDashboardSummary();
        assertNotNull(dto);
        assertEquals(0L, dto.getTotalRooms());
        assertEquals(0L, dto.getTotalReservations());
        verify(roomRepository).findAll();
        verify(reservationRepository).findAll();
        verify(billRepository).findAll();
    }
}
