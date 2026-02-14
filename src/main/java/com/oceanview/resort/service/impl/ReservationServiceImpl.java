package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.ReservationDTO;
import com.oceanview.resort.mapper.ReservationMapper;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.User;
import com.oceanview.resort.observer.ReservationEvent;
import com.oceanview.resort.observer.ReservationEventType;
import com.oceanview.resort.observer.ReservationSubject;
import com.oceanview.resort.repository.GuestRepository;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.repository.UserRepository;
import com.oceanview.resort.service.ReservationNotificationService;
import com.oceanview.resort.service.ReservationService;
import com.oceanview.resort.util.DateUtil;
import com.oceanview.resort.security.ValidationUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ReservationService that uses the Observer Pattern
 * to notify multiple observers when reservation events occur.
 */
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationNotificationService notificationService; // Kept for backward compatibility
    private final ReservationSubject reservationSubject; // Observer Pattern subject

    public ReservationServiceImpl(ReservationRepository reservationRepository, GuestRepository guestRepository,
                                  RoomRepository roomRepository, UserRepository userRepository,
                                  ReservationNotificationService notificationService,
                                  ReservationSubject reservationSubject) {
        this.reservationRepository = reservationRepository;
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.reservationSubject = reservationSubject;
    }

    @Override
    public ReservationDTO create(ReservationDTO dto, long createdById) {
        ValidationUtil.requireNonBlank(dto.getCheckInDate(), "Check-in date is required");
        ValidationUtil.requireNonBlank(dto.getCheckOutDate(), "Check-out date is required");
        LocalDate checkIn = DateUtil.parseDate(dto.getCheckInDate());
        LocalDate checkOut = DateUtil.parseDate(dto.getCheckOutDate());

        Reservation reservation = ReservationMapper.toEntity(dto);
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.validateDates();

        if (!reservationRepository.isRoomAvailable(dto.getRoomId(), checkIn, checkOut)) {
            throw new IllegalArgumentException("Room is not available for selected dates");
        }

        reservation.setStatus(ReservationStatus.PENDING);

        Guest guest = guestRepository.findById(dto.getGuestId());
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found");
        }
        reservation.setGuest(guest);

        Room room = roomRepository.findById(dto.getRoomId());
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        reservation.setRoom(room);
        reservation.setReservationNo(generateReservationNo(guest.getId(), room.getRoomNumber(), checkIn));

        User createdBy = userRepository.findById(createdById);
        if (createdBy == null) {
            throw new IllegalArgumentException("User not found");
        }
        reservation.setCreatedBy(createdBy);

        Reservation created = reservationRepository.create(reservation);
        
        // Notify observers using Observer Pattern
        ReservationEvent createdEvent = new ReservationEvent(ReservationEventType.CREATED, created);
        createdEvent.setDescription("New reservation created");
        reservationSubject.notifyObservers(createdEvent);
        
        // Maintain backward compatibility with existing notification service
        notificationService.publishConfirmation(created);
        
        return ReservationMapper.toDTO(created);
    }

    @Override
    public ReservationDTO update(ReservationDTO dto) {
        Reservation existing = reservationRepository.findById(dto.getId());
        Reservation reservation = ReservationMapper.toEntity(dto);
        if (reservation.getStatus() == null) {
            reservation.setStatus(ReservationStatus.PENDING);
        }
        Reservation updated = reservationRepository.update(reservation);
        
        // Determine event type based on status changes
        ReservationEventType eventType = ReservationEventType.UPDATED;
        if (existing != null) {
            ReservationStatus oldStatus = existing.getStatus();
            ReservationStatus newStatus = updated.getStatus();
            
            if (oldStatus != newStatus) {
                switch (newStatus) {
                    case CONFIRMED:
                        eventType = ReservationEventType.CONFIRMED;
                        break;
                    case CANCELLED:
                        eventType = ReservationEventType.CANCELLED;
                        break;
                    case CHECKED_IN:
                        eventType = ReservationEventType.CHECKED_IN;
                        break;
                    case CHECKED_OUT:
                        eventType = ReservationEventType.CHECKED_OUT;
                        break;
                    default:
                        eventType = ReservationEventType.UPDATED;
                }
            }
            
            // Notify observers using Observer Pattern
            ReservationEvent updateEvent = new ReservationEvent(eventType, updated, existing);
            updateEvent.setDescription("Reservation status changed from " + oldStatus + " to " + newStatus);
            reservationSubject.notifyObservers(updateEvent);
            
            // Maintain backward compatibility for cancellation
            if (oldStatus != ReservationStatus.CANCELLED && newStatus == ReservationStatus.CANCELLED) {
                notificationService.publishCancellation(existing);
            }
        } else {
            // No previous state, just notify of update
            ReservationEvent updateEvent = new ReservationEvent(ReservationEventType.UPDATED, updated);
            updateEvent.setDescription("Reservation updated");
            reservationSubject.notifyObservers(updateEvent);
        }
        
        return ReservationMapper.toDTO(updated);
    }

    @Override
    public boolean delete(long id) {
        return reservationRepository.delete(id);
    }

    @Override
    public ReservationDTO findById(long id) {
        return ReservationMapper.toDTO(reservationRepository.findById(id));
    }

    @Override
    public ReservationDTO findByReservationNo(String reservationNo) {
        return ReservationMapper.toDTO(reservationRepository.findByReservationNo(reservationNo));
    }

    @Override
    public List<ReservationDTO> findAll() {
        return reservationRepository.findAll().stream().map(ReservationMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> search(String keyword) {
        return reservationRepository.search(keyword).stream().map(ReservationMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> findWithFilters(String keyword, LocalDate fromDate, LocalDate toDate, String status) {
        return reservationRepository.findWithFilters(keyword, fromDate, toDate, status).stream()
                .map(ReservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    private String generateReservationNo(long guestId, String roomNumber, LocalDate checkIn) {
        String datePart = checkIn == null ? "DATE" : checkIn.format(DateTimeFormatter.BASIC_ISO_DATE);
        String base = "RES-" + guestId + "-" + (roomNumber == null ? "ROOM" : roomNumber) + "-" + datePart;
        String candidate = base;
        int counter = 1;
        while (reservationRepository.findByReservationNo(candidate) != null) {
            candidate = base + "-" + String.format("%02d", counter++);
        }
        return candidate;
    }
}
