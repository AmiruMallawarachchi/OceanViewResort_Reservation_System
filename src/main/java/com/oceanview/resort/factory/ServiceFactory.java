package com.oceanview.resort.factory;

import com.oceanview.resort.dao.*;
import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.service.*;
import com.oceanview.resort.service.impl.*;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

/**
 * Factory for service and exporter instances. Centralises dependency construction so that
 * controllers depend on abstractions (service interfaces) rather than concrete DAOs and
 * implementations (Dependency Inversion Principle). Single place to wire dependencies.
 */
public final class ServiceFactory {

    private static final ServiceFactory INSTANCE = new ServiceFactory();

    private final UserService userService;
    private final ReservationService reservationService;
    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    private final BillService billService;
    private final DiscountService discountService;
    private final GuestService guestService;
    private final ReportService reportService;
    private final Map<String, ReportExporter> reportExporters;

    private ServiceFactory() {
        DataSource dataSource = DatabaseConnection.getDataSource();
        UserDAO userDAO = new UserDAO(dataSource);
        GuestDAO guestDAO = new GuestDAO(dataSource);
        ReservationDAO reservationDAO = new ReservationDAO(dataSource);
        RoomDAO roomDAO = new RoomDAO(dataSource);
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO(dataSource);
        BillDAO billDAO = new BillDAO(dataSource);
        DiscountDAO discountDAO = new DiscountDAO(dataSource);
        ReportDAO reportDAO = new ReportDAO(dataSource);

        userService = new UserServiceImpl(userDAO);
        guestService = new GuestServiceImpl(guestDAO);
        roomService = new RoomServiceImpl(roomDAO);
        roomTypeService = new RoomTypeServiceImpl(roomTypeDAO);
        ReservationNotificationService notificationService = new ReservationNotificationServiceImpl();
        reservationService = new ReservationServiceImpl(
                reservationDAO, guestDAO, roomDAO, userDAO, notificationService);
        billService = new BillServiceImpl(
                billDAO, reservationDAO, roomDAO, roomTypeDAO, discountDAO, userDAO);
        discountService = new DiscountServiceImpl(discountDAO);

        reportExporters = new HashMap<>();
        PdfReportExporter pdfExporter = new PdfReportExporter();
        reportExporters.put(pdfExporter.getFormatKey(), pdfExporter);
        ExcelReportExporter excelExporter = new ExcelReportExporter();
        reportExporters.put(excelExporter.getFormatKey(), excelExporter);
        reportService = new ReportServiceImpl(
                reportDAO, userDAO, reservationDAO, roomDAO, billDAO, guestDAO, reportExporters);
    }

    public static ServiceFactory getInstance() {
        return INSTANCE;
    }

    public UserService getUserService() {
        return userService;
    }

    public ReservationService getReservationService() {
        return reservationService;
    }

    public RoomService getRoomService() {
        return roomService;
    }

    public RoomTypeService getRoomTypeService() {
        return roomTypeService;
    }

    public BillService getBillService() {
        return billService;
    }

    public DiscountService getDiscountService() {
        return discountService;
    }

    public GuestService getGuestService() {
        return guestService;
    }

    public ReportService getReportService() {
        return reportService;
    }
}
