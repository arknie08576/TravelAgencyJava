package pl.travelagency.service;

import org.springframework.stereotype.Service;
import pl.travelagency.db.OpinionEntity;
import pl.travelagency.db.ReservationEntity;
import pl.travelagency.db.UserEntity;
import pl.travelagency.db.UserRole;
import pl.travelagency.dto.AddOpinionRequest;
import pl.travelagency.dto.OpinionDto;
import pl.travelagency.dto.PutOpinionByIdRequest;
import pl.travelagency.repo.OpinionRepository;
import pl.travelagency.repo.ReservationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OpinionService {

    private final OpinionRepository opinionRepository;
    private final ReservationRepository reservationRepository;

    public OpinionService(
            OpinionRepository opinionRepository,
            ReservationRepository reservationRepository
    ) {
        this.opinionRepository = opinionRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<OpinionDto> getAll() {
        return opinionRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public Optional<OpinionDto> getById(Integer opinionId) {
        return opinionRepository.findById(opinionId)
                .map(this::mapToDto);
    }

    public Optional<OpinionDto> add(AddOpinionRequest request, UserEntity currentUser) {
        if (currentUser == null || currentUser.getRole() == UserRole.admin) {
            return Optional.empty();
        }

        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(request.getReservationId());
        if (reservationOpt.isEmpty()) {
            return Optional.empty();
        }

        ReservationEntity reservation = reservationOpt.get();

        if (!reservation.getUser().getId().equals(currentUser.getId())) {
            return Optional.empty();
        }

        if (opinionRepository.existsByReservation_Id(reservation.getId())) {
            return Optional.empty();
        }

        OpinionEntity entity = new OpinionEntity();
        entity.setReservation(reservation);
        entity.setRating(request.getRating());
        entity.setDescription(request.getDescription());
        entity.setDate(request.getDate());

        OpinionEntity saved = opinionRepository.save(entity);
        return Optional.of(mapToDto(saved));
    }

    public Optional<OpinionDto> update(PutOpinionByIdRequest request, UserEntity currentUser) {
        if (currentUser == null || currentUser.getRole() == UserRole.user) {
            return Optional.empty();
        }

        Optional<OpinionEntity> opinionOpt = opinionRepository.findById(request.getOpinionId());
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(request.getReservationId());

        if (opinionOpt.isEmpty() || reservationOpt.isEmpty()) {
            return Optional.empty();
        }

        OpinionEntity entity = opinionOpt.get();
        entity.setReservation(reservationOpt.get());
        entity.setRating(request.getRating());
        entity.setDescription(request.getDescription());
        entity.setDate(request.getDate());

        OpinionEntity saved = opinionRepository.save(entity);
        return Optional.of(mapToDto(saved));
    }

    public Optional<OpinionDto> deleteById(Integer opinionId, UserEntity currentUser) {
        if (currentUser == null || currentUser.getRole() == UserRole.user) {
            return Optional.empty();
        }

        Optional<OpinionEntity> opinionOpt = opinionRepository.findById(opinionId);
        if (opinionOpt.isEmpty()) {
            return Optional.empty();
        }

        OpinionEntity entity = opinionOpt.get();
        OpinionDto dto = mapToDto(entity);
        opinionRepository.delete(entity);
        return Optional.of(dto);
    }

    private OpinionDto mapToDto(OpinionEntity entity) {
        OpinionDto dto = new OpinionDto();
        dto.setId(entity.getId());
        dto.setReservationId(entity.getReservation().getId());
        dto.setRating(entity.getRating());
        dto.setDescription(entity.getDescription());
        dto.setDate(entity.getDate());
        return dto;
    }
}