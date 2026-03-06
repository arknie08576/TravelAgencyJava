package pl.travelagency.service;

import org.springframework.stereotype.Service;
import pl.travelagency.db.ReservationEntity;
import pl.travelagency.db.TripEntity;
import pl.travelagency.db.UserEntity;
import pl.travelagency.db.UserRole;
import pl.travelagency.dto.AddReservationRequest;
import pl.travelagency.dto.PutReservationByIdRequest;
import pl.travelagency.dto.ReservationDto;
import pl.travelagency.repo.ReservationRepository;
import pl.travelagency.repo.TripRepository;
import pl.travelagency.repo.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            TripRepository tripRepository,
            UserRepository userRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public List<ReservationDto> getAll() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public Optional<ReservationDto> getById(Integer reservationId) {
        return reservationRepository.findById(reservationId)
                .map(this::mapToDto);
    }

    public Optional<ReservationDto> add(AddReservationRequest request, UserEntity currentUser) {
        if (currentUser == null || currentUser.getRole() == UserRole.admin) {
            return Optional.empty();
        }

        Optional<TripEntity> tripOpt = tripRepository.findById(request.getTripId());
        Optional<UserEntity> userOpt = userRepository.findById(request.getUserId());

        if (tripOpt.isEmpty() || userOpt.isEmpty()) {
            return Optional.empty();
        }

        ReservationEntity entity = new ReservationEntity();
        entity.setTrip(tripOpt.get());
        entity.setUser(userOpt.get());
        entity.setAdultsNumber(request.getAdultsNumber());
        entity.setKidsNumber(request.getKidsNumber());
        entity.setPricePaid(request.getPricePaid());

        ReservationEntity saved = reservationRepository.save(entity);
        return Optional.of(mapToDto(saved));
    }

    public Optional<ReservationDto> update(PutReservationByIdRequest request, UserEntity currentUser) {
        if (currentUser == null || currentUser.getRole() == UserRole.user) {
            return Optional.empty();
        }

        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(request.getReservationId());
        Optional<TripEntity> tripOpt = tripRepository.findById(request.getTripId());
        Optional<UserEntity> userOpt = userRepository.findById(request.getUserId());

        if (reservationOpt.isEmpty() || tripOpt.isEmpty() || userOpt.isEmpty()) {
            return Optional.empty();
        }

        ReservationEntity entity = reservationOpt.get();
        entity.setTrip(tripOpt.get());
        entity.setUser(userOpt.get());
        entity.setAdultsNumber(request.getAdultsNumber());
        entity.setKidsNumber(request.getKidsNumber());
        entity.setPricePaid(request.getPricePaid());

        ReservationEntity saved = reservationRepository.save(entity);
        return Optional.of(mapToDto(saved));
    }

    public Optional<ReservationDto> deleteById(Integer reservationId, UserEntity currentUser) {
        if (currentUser == null || currentUser.getRole() == UserRole.user) {
            return Optional.empty();
        }

        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isEmpty()) {
            return Optional.empty();
        }

        ReservationEntity entity = reservationOpt.get();
        ReservationDto dto = mapToDto(entity);
        reservationRepository.delete(entity);
        return Optional.of(dto);
    }

    private ReservationDto mapToDto(ReservationEntity entity) {
        ReservationDto dto = new ReservationDto();
        dto.setId(entity.getId());
        dto.setTripId(entity.getTrip().getId());
        dto.setUserId(entity.getUser().getId());
        dto.setAdultsNumber(entity.getAdultsNumber());
        dto.setKidsNumber(entity.getKidsNumber());
        dto.setPricePaid(entity.getPricePaid());
        return dto;
    }
}