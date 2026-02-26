package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.BillDTO;
import com.oceanview.resort.mapper.BillMapper;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.model.enums.GuestType;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.model.User;
import com.oceanview.resort.repository.BillRepository;
import com.oceanview.resort.repository.DiscountRepository;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.repository.RoomTypeRepository;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.BillService;
import com.oceanview.resort.service.ConfigService;
import com.oceanview.resort.strategy.DiscountCalculationContext;
import com.oceanview.resort.strategy.DiscountCalculationManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of BillService that uses the Strategy Pattern for discount calculation.
 * Different discount calculation strategies (guest type, promotion, manual) are
 * applied through the DiscountCalculationManager, making the system extensible
 * without modifying this class (Open/Closed Principle).
 */
public class BillServiceImpl implements BillService {
    private final BillRepository billRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final DiscountRepository discountRepository;
    private final UserRepository userRepository;
    private final ConfigService configService;
    private final DiscountCalculationManager discountCalculationManager;

    /**
     * Backwards-compatible constructor used in existing tests and older wiring code.
     * Defaults to a static 10% tax rate to preserve previous behaviour.
     * Creates a default DiscountCalculationManager with basic strategies.
     */
    public BillServiceImpl(BillRepository billRepository, ReservationRepository reservationRepository,
                           RoomRepository roomRepository, RoomTypeRepository roomTypeRepository,
                           DiscountRepository discountRepository, UserRepository userRepository) {
        this(billRepository, reservationRepository, roomRepository, roomTypeRepository,
                discountRepository, userRepository, new ConfigService() {
                    @Override
                    public double getTaxRateDecimal() {
                        return 0.10; // previous hard-coded tax rate
                    }

                    @Override
                    public void setTaxRatePercent(double percent) {
                        // no-op in legacy constructor
                    }
                }, null); // Will create default manager if null
    }

    public BillServiceImpl(BillRepository billRepository, ReservationRepository reservationRepository,
                           RoomRepository roomRepository, RoomTypeRepository roomTypeRepository,
                           DiscountRepository discountRepository, UserRepository userRepository,
                           ConfigService configService) {
        this(billRepository, reservationRepository, roomRepository, roomTypeRepository,
                discountRepository, userRepository, configService, null);
    }

    public BillServiceImpl(BillRepository billRepository, ReservationRepository reservationRepository,
                           RoomRepository roomRepository, RoomTypeRepository roomTypeRepository,
                           DiscountRepository discountRepository, UserRepository userRepository,
                           ConfigService configService, DiscountCalculationManager discountCalculationManager) {
        this.billRepository = billRepository;
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.discountRepository = discountRepository;
        this.userRepository = userRepository;
        this.configService = configService;
        
        // Create default manager if not provided (for backward compatibility)
        if (discountCalculationManager != null) {
            this.discountCalculationManager = discountCalculationManager;
        } else {
            // Create default strategies for backward compatibility
            java.util.List<com.oceanview.resort.strategy.DiscountCalculationStrategy> defaultStrategies = 
                java.util.List.of(
                    new com.oceanview.resort.strategy.impl.GuestTypeDiscountStrategy(),
                    new com.oceanview.resort.strategy.impl.PromotionDiscountStrategy(),
                    new com.oceanview.resort.strategy.impl.ManualDiscountStrategy()
                );
            this.discountCalculationManager = new DiscountCalculationManager(defaultStrategies, discountRepository);
        }
    }

    @Override
    public BillDTO generate(long reservationId, BigDecimal manualDiscountPercent, List<Long> discountIds,
                            LocalDate actualCheckoutDate, long generatedById) {
        Reservation reservation = reservationRepository.findById(reservationId);
        if (reservation == null) {
            return null;
        }
        Room room = roomRepository.findById(reservation.getRoom().getId());
        RoomType roomType = roomTypeRepository.findById(room.getRoomType().getId());

        LocalDate checkIn = reservation.getCheckInDate();
        LocalDate plannedCheckOut = reservation.getCheckOutDate();
        LocalDate effectiveCheckOut = plannedCheckOut;
        if (actualCheckoutDate != null) {
            if (checkIn == null || actualCheckoutDate.isBefore(checkIn)) {
                throw new IllegalArgumentException("Actual checkout must be on or after check-in date.");
            }
            if (plannedCheckOut != null && actualCheckoutDate.isAfter(plannedCheckOut)) {
                throw new IllegalArgumentException("Actual checkout cannot be after the reserved checkout date.");
            }
            effectiveCheckOut = actualCheckoutDate;
            reservation.setCheckOutDate(actualCheckoutDate);
            reservation.setStatus(ReservationStatus.CHECKED_OUT);
            reservationRepository.update(reservation);
        }

        long nights = calculateNights(checkIn, effectiveCheckOut);
        if (nights <= 0) {
            throw new IllegalArgumentException("Billing requires at least one night stay.");
        }
        BigDecimal rate = roomType.getRatePerNight();
        BigDecimal total = rate.multiply(BigDecimal.valueOf(nights));

        // Use Strategy Pattern for discount calculation.
        // If the user did not select any discount (no promotions, no manual),
        // treat this as \"no discount\" and skip automatic guest-type discounts.
        DiscountCalculationContext context = new DiscountCalculationContext(discountIds, manualDiscountPercent);
        boolean hasSelectedPromotions = discountIds != null && !discountIds.isEmpty();
        boolean hasManualDiscount = manualDiscountPercent != null && manualDiscountPercent.compareTo(BigDecimal.ZERO) > 0;
        BigDecimal discountPercent = BigDecimal.ZERO;
        if (hasSelectedPromotions || hasManualDiscount) {
            discountPercent = discountCalculationManager.calculateTotalDiscount(reservation, context);
        }
        BigDecimal discount = total.multiply(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        double taxRate = configService.getTaxRateDecimal();
        BigDecimal tax = total.multiply(BigDecimal.valueOf(taxRate)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal net = total.subtract(discount).add(tax);

        Bill bill = new Bill();
        bill.setBillNo("BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bill.setReservation(reservation);
        bill.setNumberOfNights((int) nights);
        bill.setRoomRate(rate);
        bill.setTotalAmount(total);
        bill.setDiscountAmount(discount);
        bill.setTaxAmount(tax);
        bill.setNetAmount(net);
        User user = userRepository.findById(generatedById);
        bill.setGeneratedBy(user);
        bill.setGeneratedAt(LocalDateTime.now());

        return BillMapper.toDTO(billRepository.create(bill));
    }

    @Override
    public BillDTO findById(long id) {
        return BillMapper.toDTO(billRepository.findById(id));
    }

    @Override
    public BillDTO findByReservationId(long reservationId) {
        return BillMapper.toDTO(billRepository.findByReservationId(reservationId));
    }

    @Override
    public List<BillDTO> findAll() {
        return billRepository.findAll().stream().map(BillMapper::toDTO).collect(Collectors.toList());
    }

    private long calculateNights(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /**
     * @deprecated This method is replaced by Strategy Pattern implementation.
     * Use DiscountCalculationManager instead.
     * Kept for backward compatibility with tests that may call it directly.
     */
    @Deprecated
    private BigDecimal resolveDiscountPercent(Reservation reservation, BigDecimal manualDiscountPercent, List<Long> discountIds) {
        // Delegate to Strategy Pattern implementation
        DiscountCalculationContext context = new DiscountCalculationContext(discountIds, manualDiscountPercent);
        return discountCalculationManager.calculateTotalDiscount(reservation, context);
    }
}
