package com.oceanview.resort.service.impl;

import com.oceanview.resort.dto.RoomDTO;
import com.oceanview.resort.mapper.RoomMapper;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.service.RoomService;
import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.security.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class RoomServiceImpl implements RoomService {
    private final RoomRepository repository;
    private final ReservationRepository reservationRepository;

    public RoomServiceImpl(RoomRepository repository) {
        this(repository, null);
    }

    public RoomServiceImpl(RoomRepository repository, ReservationRepository reservationRepository) {
        this.repository = repository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public RoomDTO create(RoomDTO dto) {
        ValidationUtil.requireNonBlank(dto.getRoomNumber(), "Room number is required");
        Room room = RoomMapper.toEntity(dto);
        return RoomMapper.toDTO(repository.create(room));
    }

    @Override
    public RoomDTO update(RoomDTO dto) {
        Room room = RoomMapper.toEntity(dto);
        return RoomMapper.toDTO(repository.update(room));
    }

    @Override
    public boolean delete(long id) {
        if (reservationRepository != null && reservationRepository.countByRoomId(id) > 0) {
            throw new IllegalArgumentException("Cannot delete room: it has associated reservations. Cancel or reassign the reservations first.");
        }
        return repository.delete(id);
    }

    @Override
    public RoomDTO findById(long id) {
        return RoomMapper.toDTO(repository.findById(id));
    }

    @Override
    public List<RoomDTO> findAll() {
        return repository.findAll().stream().map(RoomMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> findAvailable(LocalDate checkIn, LocalDate checkOut) {
        boolean useProcedure = AppConfig.getBoolean("db.useStoredProcedure.availability", false);
        List<Room> rooms = useProcedure
                ? repository.findAvailableUsingProcedure(checkIn, checkOut)
                : repository.findAvailable(checkIn, checkOut);
        return rooms.stream().map(RoomMapper::toDTO).collect(Collectors.toList());
    }
}
