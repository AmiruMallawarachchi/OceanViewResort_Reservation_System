package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.ReportDTO;
import com.oceanview.resort.dto.DashboardSummaryDTO;
import com.oceanview.resort.mapper.ReportMapper;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.model.Report;
import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.model.enums.ReportType;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.RoomStatus;
import com.oceanview.resort.repository.BillRepository;
import com.oceanview.resort.repository.GuestRepository;
import com.oceanview.resort.repository.ReportRepository;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.ReportExporter;
import com.oceanview.resort.service.ReportService;
import com.oceanview.resort.util.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final BillRepository billRepository;
    private final GuestRepository guestRepository;
    private final java.util.Map<String, ReportExporter> reportExporters;

    public ReportServiceImpl(ReportRepository reportRepository, UserRepository userRepository,
                             ReservationRepository reservationRepository, RoomRepository roomRepository,
                             BillRepository billRepository, GuestRepository guestRepository,
                             java.util.Map<String, ReportExporter> reportExporters) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.billRepository = billRepository;
        this.guestRepository = guestRepository;
        this.reportExporters = reportExporters != null ? reportExporters : new java.util.HashMap<>();
    }

    @Override
    public ReportDTO generate(ReportDTO dto, long generatedById) {
        Report report = new Report();
        ReportType reportType = dto.getReportType() == null ? ReportType.RESERVATION : ReportType.valueOf(dto.getReportType());
        ReportFormat format = dto.getFormat() == null ? ReportFormat.PDF : ReportFormat.valueOf(dto.getFormat());
        LocalDate startDate = DateUtil.parseDate(dto.getStartDate());
        LocalDate endDate = DateUtil.parseDate(dto.getEndDate());
        String period = dto.getPeriod() == null ? "CUSTOM" : dto.getPeriod();
        LocalDateTime generatedAt = LocalDateTime.now();

        List<String[]> rows = buildReportRows(reportType, startDate, endDate, period, generatedAt);
        String title = reportType.name().replace('_', ' ') + " REPORT";

        ReportExporter exporter = reportExporters.get(format.name());
        if (exporter == null) {
            exporter = reportExporters.get(ReportFormat.PDF.name());
        }
        if (exporter == null && !reportExporters.isEmpty()) {
            exporter = reportExporters.values().iterator().next();
        }
        if (exporter == null) {
            throw new IllegalStateException("No report exporter registered for format: " + format);
        }
        byte[] content = exporter.export(title, rows);

        report.setReportType(reportType);
        report.setFormat(format);
        report.setParameters(dto.getParameters());
        User user = userRepository.findById(generatedById);
        report.setGeneratedBy(user);
        report.setGeneratedAt(generatedAt);
        report.setContent(content);

        return ReportMapper.toDTO(reportRepository.create(report));
    }

    @Override
    public ReportDTO findById(long id) {
        return ReportMapper.toDTO(reportRepository.findById(id));
    }

    @Override
    public List<ReportDTO> findAll() {
        return reportRepository.findAll().stream().map(ReportMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public byte[] getContent(long id) {
        Report report = reportRepository.findById(id);
        return report == null ? null : report.getContent();
    }

    @Override
    public String getFormat(long id) {
        Report report = reportRepository.findById(id);
        return report == null || report.getFormat() == null ? null : report.getFormat().name();
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary() {
        DashboardSummaryDTO dto = new DashboardSummaryDTO();

        List<Room> rooms = roomRepository.findAll();
        long totalRooms = rooms.size();
        long availableRooms = rooms.stream().filter(r -> r.getStatus() == RoomStatus.AVAILABLE).count();
        long maintenanceRooms = rooms.stream().filter(r -> r.getStatus() == RoomStatus.MAINTENANCE).count();
        long occupiedRooms = totalRooms - availableRooms - maintenanceRooms;
        if (occupiedRooms < 0) occupiedRooms = 0;
        double occupancyRate = totalRooms == 0 ? 0 : (occupiedRooms * 100.0 / totalRooms);

        dto.setTotalRooms(totalRooms);
        dto.setAvailableRooms(availableRooms);
        dto.setOccupiedRooms(occupiedRooms);
        dto.setMaintenanceRooms(maintenanceRooms);
        dto.setOccupancyRate(occupancyRate);

        List<Reservation> reservations = reservationRepository.findAll();
        long totalReservations = reservations.size();
        long cancelledReservations = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.CANCELLED).count();
        double cancellationRate = totalReservations == 0 ? 0 : (cancelledReservations * 100.0 / totalReservations);

        dto.setTotalReservations(totalReservations);
        dto.setCancelledReservations(cancelledReservations);
        dto.setCancellationRate(cancellationRate);

        List<Bill> bills = billRepository.findAll();
        BigDecimal totalRevenue = bills.stream()
                .map(b -> b.getNetAmount() == null ? BigDecimal.ZERO : b.getNetAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscounts = bills.stream()
                .map(b -> b.getDiscountAmount() == null ? BigDecimal.ZERO : b.getDiscountAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setTotalRevenue(totalRevenue);
        dto.setTotalDiscounts(totalDiscounts);

        LocalDate today = LocalDate.now();
        long todaysCheckins = reservations.stream()
                .filter(r -> today.equals(r.getCheckInDate()) && r.getStatus() != ReservationStatus.CANCELLED)
                .count();
        long pendingCheckins = reservations.stream()
                .filter(r -> today.equals(r.getCheckInDate()) && r.getStatus() == ReservationStatus.PENDING)
                .count();
        dto.setTodaysCheckins(todaysCheckins);
        dto.setPendingCheckins(pendingCheckins);

        long activeStaff = userRepository.findAll().stream().filter(User::isActive).count();
        dto.setActiveStaff(activeStaff);
        dto.setStaffOnLeave(0);
        dto.setOccupancyChangePercent(5.0);

        return dto;
    }

    private List<String[]> buildReportRows(ReportType reportType, LocalDate startDate, LocalDate endDate,
                                           String period, LocalDateTime generatedAt) {
        List<String[]> rows = new ArrayList<>();
        if (period != null) {
            rows.add(new String[]{"Period", period});
        }
        if (startDate != null) {
            rows.add(new String[]{"Start Date", DateUtil.formatDate(startDate)});
        }
        if (endDate != null) {
            rows.add(new String[]{"End Date", DateUtil.formatDate(endDate)});
        }
        if (generatedAt != null) {
            rows.add(new String[]{"Generated At", DateUtil.formatDateTime(generatedAt)});
        }
        switch (reportType) {
            case OCCUPANCY:
                List<Room> rooms = roomRepository.findAll();
                long totalRooms = rooms.size();
                long availableRooms = rooms.stream().filter(r -> r.getStatus() == RoomStatus.AVAILABLE).count();
                long occupiedRooms = totalRooms - availableRooms;
                long reservedRooms = rooms.stream().filter(r -> r.getStatus() == RoomStatus.RESERVED).count();
                double occupancyRate = totalRooms == 0 ? 0 : (occupiedRooms * 100.0 / totalRooms);
                rows.add(new String[]{"Total Rooms", String.valueOf(totalRooms)});
                rows.add(new String[]{"Available Rooms", String.valueOf(availableRooms)});
                rows.add(new String[]{"Occupied Rooms", String.valueOf(occupiedRooms)});
                rows.add(new String[]{"Reserved Rooms", String.valueOf(reservedRooms)});
                rows.add(new String[]{"Occupancy Rate (%)", String.format("%.2f", occupancyRate)});
                break;
            case REVENUE:
                List<Bill> bills = billRepository.findAll();
                if (startDate != null && endDate != null) {
                    bills = bills.stream()
                            .filter(bill -> isWithinRange(bill.getGeneratedAt(), startDate, endDate))
                            .collect(Collectors.toList());
                }
                BigDecimal totalRevenue = bills.stream()
                        .map(b -> b.getNetAmount() == null ? BigDecimal.ZERO : b.getNetAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalDiscount = bills.stream()
                        .map(b -> b.getDiscountAmount() == null ? BigDecimal.ZERO : b.getDiscountAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalTax = bills.stream()
                        .map(b -> b.getTaxAmount() == null ? BigDecimal.ZERO : b.getTaxAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal avgBill = bills.isEmpty() ? BigDecimal.ZERO :
                        totalRevenue.divide(BigDecimal.valueOf(bills.size()), 2, java.math.RoundingMode.HALF_UP);
                rows.add(new String[]{"Total Bills", String.valueOf(bills.size())});
                rows.add(new String[]{"Total Revenue", totalRevenue.toPlainString()});
                rows.add(new String[]{"Total Discounts", totalDiscount.toPlainString()});
                rows.add(new String[]{"Total Taxes", totalTax.toPlainString()});
                rows.add(new String[]{"Average Bill", avgBill.toPlainString()});
                break;
            case RESERVATION:
                List<Reservation> reservations = reservationRepository.findAll();
                if (startDate != null && endDate != null) {
                    reservations = reservations.stream()
                            .filter(reservation -> isWithinRange(reservation.getCheckInDate(), startDate, endDate))
                            .collect(Collectors.toList());
                }
                long total = reservations.size();
                long pending = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.PENDING).count();
                long confirmed = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.CONFIRMED).count();
                long checkedIn = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN).count();
                long checkedOut = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.CHECKED_OUT).count();
                long cancelled = reservations.stream().filter(r -> r.getStatus() == ReservationStatus.CANCELLED).count();
                double cancelRate = total == 0 ? 0 : (cancelled * 100.0 / total);
                rows.add(new String[]{"Total Reservations", String.valueOf(total)});
                rows.add(new String[]{"Pending", String.valueOf(pending)});
                rows.add(new String[]{"Confirmed", String.valueOf(confirmed)});
                rows.add(new String[]{"Checked In", String.valueOf(checkedIn)});
                rows.add(new String[]{"Checked Out", String.valueOf(checkedOut)});
                rows.add(new String[]{"Cancelled", String.valueOf(cancelled)});
                rows.add(new String[]{"Cancellation Rate (%)", String.format("%.2f", cancelRate)});
                long guestCount = guestRepository.findAll().stream()
                        .filter(guest -> startDate == null || endDate == null
                                || isWithinRange(guest.getCreatedAt(), startDate, endDate))
                        .count();
                rows.add(new String[]{"Total Guests", String.valueOf(guestCount)});
                break;
            case GUEST_SEGMENT:
                List<com.oceanview.resort.model.Guest> guests = guestRepository.findAll();
                if (startDate != null && endDate != null) {
                    guests = guests.stream()
                            .filter(guest -> isWithinRange(guest.getCreatedAt(), startDate, endDate))
                            .collect(Collectors.toList());
                }
                java.util.Map<String, Long> byType = guests.stream()
                        .collect(Collectors.groupingBy(
                                guest -> guest.getGuestType() == null ? "UNKNOWN" : guest.getGuestType().name(),
                                Collectors.counting()));
                rows.add(new String[]{"Metric", "Value"});
                for (java.util.Map.Entry<String, Long> entry : byType.entrySet()) {
                    rows.add(new String[]{"Guests (" + entry.getKey() + ")", String.valueOf(entry.getValue())});
                }
                java.util.Map<String, Long> byCountry = guests.stream()
                        .filter(g -> g.getNationality() != null && !g.getNationality().isBlank())
                        .collect(Collectors.groupingBy(
                                g -> g.getNationality().trim(),
                                Collectors.counting()));
                byCountry.entrySet().stream()
                        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                        .limit(5)
                        .forEach(entry ->
                                rows.add(new String[]{"Top Nationality: " + entry.getKey(),
                                        String.valueOf(entry.getValue())}));
                break;
            case DISCOUNT_PERFORMANCE:
                List<Bill> allBills = billRepository.findAll();
                if (startDate != null && endDate != null) {
                    allBills = allBills.stream()
                            .filter(bill -> isWithinRange(bill.getGeneratedAt(), startDate, endDate))
                            .collect(Collectors.toList());
                }
                BigDecimal discTotal = allBills.stream()
                        .map(b -> b.getDiscountAmount() == null ? BigDecimal.ZERO : b.getDiscountAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal revenueTotal = allBills.stream()
                        .map(b -> b.getNetAmount() == null ? BigDecimal.ZERO : b.getNetAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                rows.add(new String[]{"Total Bills", String.valueOf(allBills.size())});
                rows.add(new String[]{"Total Revenue (Net)", revenueTotal.toPlainString()});
                rows.add(new String[]{"Total Discounts Applied", discTotal.toPlainString()});
                BigDecimal effectiveRate = revenueTotal.add(discTotal).compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ZERO
                        : discTotal.multiply(BigDecimal.valueOf(100))
                        .divide(revenueTotal.add(discTotal), 2, java.math.RoundingMode.HALF_UP);
                rows.add(new String[]{"Discount Share of Gross Revenue (%)", effectiveRate.toPlainString()});
                break;
            case CANCELLATION:
                List<Reservation> allReservations = reservationRepository.findAll();
                List<Reservation> cancelledList = allReservations.stream()
                        .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                        .filter(r -> startDate == null || endDate == null || isWithinRange(r.getCheckInDate(), startDate, endDate) || isWithinRange(r.getCheckOutDate(), startDate, endDate))
                        .collect(Collectors.toList());
                rows.add(new String[]{"Total Cancelled Reservations", String.valueOf(cancelledList.size())});
                rows.add(new String[]{"Reservation No", "Guest", "Check-In", "Check-Out", "Cancelled (created)"});
                for (Reservation res : cancelledList) {
                    String guestName = res.getGuest() != null ? res.getGuest().getFullName() : "";
                    rows.add(new String[]{
                            res.getReservationNo() == null ? "" : res.getReservationNo(),
                            guestName,
                            res.getCheckInDate() != null ? DateUtil.formatDate(res.getCheckInDate()) : "",
                            res.getCheckOutDate() != null ? DateUtil.formatDate(res.getCheckOutDate()) : "",
                            res.getCreatedAt() != null ? DateUtil.formatDateTime(res.getCreatedAt()) : ""
                    });
                }
                break;
            default:
                // Fallback: treat unknown types as reservation summary
                List<Reservation> defaultReservations = reservationRepository.findAll();
                if (startDate != null && endDate != null) {
                    defaultReservations = defaultReservations.stream()
                            .filter(reservation -> isWithinRange(reservation.getCheckInDate(), startDate, endDate))
                            .collect(Collectors.toList());
                }
                long totalDefault = defaultReservations.size();
                long cancelledDefault = defaultReservations.stream().filter(r -> r.getStatus() == ReservationStatus.CANCELLED).count();
                rows.add(new String[]{"Total Reservations", String.valueOf(totalDefault)});
                rows.add(new String[]{"Cancelled", String.valueOf(cancelledDefault)});
                break;
        }
        return rows;
    }

    private boolean isWithinRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (date == null) {
            return false;
        }
        if (startDate != null && date.isBefore(startDate)) {
            return false;
        }
        return endDate == null || !date.isAfter(endDate);
    }

    private boolean isWithinRange(LocalDateTime dateTime, LocalDate startDate, LocalDate endDate) {
        if (dateTime == null) {
            return false;
        }
        return isWithinRange(dateTime.toLocalDate(), startDate, endDate);
    }
}
