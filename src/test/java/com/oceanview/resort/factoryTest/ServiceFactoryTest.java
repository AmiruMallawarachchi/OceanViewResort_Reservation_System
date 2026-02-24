package com.oceanview.resort.factoryTest;

import com.oceanview.resort.factory.ServiceFactory;
import com.oceanview.resort.service.*;
import org.junit.Assert;
import org.junit.Test;

public class ServiceFactoryTest {

    @Test
    public void getInstance_returnsSingleton() {
        ServiceFactory first = ServiceFactory.getInstance();
        ServiceFactory second = ServiceFactory.getInstance();

        Assert.assertNotNull(first);
        Assert.assertSame("ServiceFactory should be a singleton", first, second);
    }

    @Test
    public void services_areNonNullAndStablePerFactory() {
        ServiceFactory factory = ServiceFactory.getInstance();

        UserService userService1 = factory.getUserService();
        UserService userService2 = factory.getUserService();
        Assert.assertNotNull(userService1);
        Assert.assertSame(userService1, userService2);

        ReservationService reservationService1 = factory.getReservationService();
        ReservationService reservationService2 = factory.getReservationService();
        Assert.assertNotNull(reservationService1);
        Assert.assertSame(reservationService1, reservationService2);

        RoomService roomService1 = factory.getRoomService();
        RoomService roomService2 = factory.getRoomService();
        Assert.assertNotNull(roomService1);
        Assert.assertSame(roomService1, roomService2);

        RoomTypeService roomTypeService1 = factory.getRoomTypeService();
        RoomTypeService roomTypeService2 = factory.getRoomTypeService();
        Assert.assertNotNull(roomTypeService1);
        Assert.assertSame(roomTypeService1, roomTypeService2);

        BillService billService1 = factory.getBillService();
        BillService billService2 = factory.getBillService();
        Assert.assertNotNull(billService1);
        Assert.assertSame(billService1, billService2);

        DiscountService discountService1 = factory.getDiscountService();
        DiscountService discountService2 = factory.getDiscountService();
        Assert.assertNotNull(discountService1);
        Assert.assertSame(discountService1, discountService2);

        GuestService guestService1 = factory.getGuestService();
        GuestService guestService2 = factory.getGuestService();
        Assert.assertNotNull(guestService1);
        Assert.assertSame(guestService1, guestService2);

        ReportService reportService1 = factory.getReportService();
        ReportService reportService2 = factory.getReportService();
        Assert.assertNotNull(reportService1);
        Assert.assertSame(reportService1, reportService2);

        ConfigService configService1 = factory.getConfigService();
        ConfigService configService2 = factory.getConfigService();
        Assert.assertNotNull(configService1);
        Assert.assertSame(configService1, configService2);
    }
}

