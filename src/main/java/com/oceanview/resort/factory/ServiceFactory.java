package com.oceanview.resort.factory;

import com.oceanview.resort.dao.*;
import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.observer.ReservationSubject;
import com.oceanview.resort.observer.impl.AuditLogObserver;
import com.oceanview.resort.observer.impl.EmailNotificationObserver;
import com.oceanview.resort.observer.impl.StatisticsObserver;
import com.oceanview.resort.service.*;
import com.oceanview.resort.service.impl.*;
import com.oceanview.resort.strategy.DiscountCalculationManager;
import com.oceanview.resort.strategy.DiscountCalculationStrategy;
import com.oceanview.resort.strategy.impl.GuestTypeDiscountStrategy;
import com.oceanview.resort.strategy.impl.ManualDiscountStrategy;
import com.oceanview.resort.strategy.impl.PromotionDiscountStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 * Factory for service and exporter instances. Centralizes dependency construction so that
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
    private final ConfigService configService;

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
        SystemConfigDAO systemConfigDAO = new SystemConfigDAO(dataSource);

        userService = new UserServiceImpl(userDAO);
        guestService = new GuestServiceImpl(guestDAO);
        roomService = new RoomServiceImpl(roomDAO, reservationDAO);
        roomTypeService = new RoomTypeServiceImpl(roomTypeDAO, roomDAO);
        
        // Set up Observer Pattern for reservation events
        ReservationSubject reservationSubject = new ReservationSubject();
        
        // Create and attach observers
        EmailNotificationObserver emailObserver = new EmailNotificationObserver();
        AuditLogObserver auditObserver = new AuditLogObserver();
        StatisticsObserver statisticsObserver = new StatisticsObserver();
        
        reservationSubject.attach(emailObserver);
        reservationSubject.attach(auditObserver);
        reservationSubject.attach(statisticsObserver);
        
        // Maintain backward compatibility with existing notification service
        ReservationNotificationService notificationService = new ReservationNotificationServiceImpl();
        
        reservationService = new ReservationServiceImpl(
                reservationDAO, guestDAO, roomDAO, userDAO, notificationService, reservationSubject);
        configService = new ConfigServiceImpl(systemConfigDAO);
        
        // Set up Strategy Pattern for discount calculation
        List<DiscountCalculationStrategy> discountStrategies = new ArrayList<>();
        discountStrategies.add(new GuestTypeDiscountStrategy());
        discountStrategies.add(new PromotionDiscountStrategy());
        discountStrategies.add(new ManualDiscountStrategy());
        
        DiscountCalculationManager discountCalculationManager = 
            new DiscountCalculationManager(discountStrategies, discountDAO);
        
        billService = new BillServiceImpl(
                billDAO, reservationDAO, roomDAO, roomTypeDAO, discountDAO, userDAO, 
                configService, discountCalculationManager);
        discountService = new DiscountServiceImpl(discountDAO);

        Map<String, ReportExporter> reportExporters = new HashMap<>();
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

    public ConfigService getConfigService() {
        return configService;
    }
}
