package pl.travelagency.service;

import org.springframework.stereotype.Service;
import pl.travelagency.db.TripEntity;
import pl.travelagency.db.UserEntity;
import pl.travelagency.db.UserRole;
import pl.travelagency.dto.AddTripRequest;
import pl.travelagency.dto.PutTripByIdRequest;
import pl.travelagency.dto.TripDto;
import pl.travelagency.repo.TripRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<TripDto> getAll() {
        return tripRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public Optional<TripDto> getById(Integer id) {
        return tripRepository.findById(id)
                .map(this::mapToDto);
    }

    public Optional<TripDto> add(AddTripRequest request, UserEntity currentUser) {
        if (currentUser == null || currentUser.getRole() == UserRole.user) {
            return Optional.empty();
        }

        TripEntity entity = new TripEntity();
        updateEntity(entity, request);

        TripEntity saved = tripRepository.save(entity);
        return Optional.of(mapToDto(saved));
    }

    public Optional<TripDto> update(PutTripByIdRequest request, UserEntity currentUser) {
        if (currentUser == null || currentUser.getRole() == UserRole.user) {
            return Optional.empty();
        }

        return tripRepository.findById(request.getTripId())
                .map(entity -> {
                    updateEntity(entity, request);
                    return mapToDto(tripRepository.save(entity));
                });
    }

    public Optional<TripDto> deleteById(Integer tripId, UserEntity currentUser) {
        if (currentUser == null || currentUser.getRole() == UserRole.user) {
            return Optional.empty();
        }

        return tripRepository.findById(tripId)
                .map(entity -> {
                    TripDto dto = mapToDto(entity);
                    tripRepository.delete(entity);
                    return dto;
                });
    }

    private void updateEntity(TripEntity entity, AddTripRequest request) {
        entity.setHotelName(request.getHotelName());
        entity.setHotelDescription(request.getHotelDescription());
        entity.setCountry(request.getCountry());
        entity.setCity(request.getCity());
        entity.setPricePerAdult(request.getPricePerAdult());
        entity.setPricePerKid(request.getPricePerKid());
        entity.setStartDate(request.getStartDate());
        entity.setStopDate(request.getStopDate());
        entity.setDeparture(request.getDeparture());
        entity.setFood(request.getFood());
        entity.setRequiredDocuments(request.getRequiredDocuments());
    }

    private void updateEntity(TripEntity entity, PutTripByIdRequest request) {
        entity.setHotelName(request.getHotelName());
        entity.setHotelDescription(request.getHotelDescription());
        entity.setCountry(request.getCountry());
        entity.setCity(request.getCity());
        entity.setPricePerAdult(request.getPricePerAdult());
        entity.setPricePerKid(request.getPricePerKid());
        entity.setStartDate(request.getStartDate());
        entity.setStopDate(request.getStopDate());
        entity.setDeparture(request.getDeparture());
        entity.setFood(request.getFood());
        entity.setRequiredDocuments(request.getRequiredDocuments());
    }

    private TripDto mapToDto(TripEntity entity) {
        TripDto dto = new TripDto();
        dto.setId(entity.getId());
        dto.setHotelName(entity.getHotelName());
        dto.setHotelDescription(entity.getHotelDescription());
        dto.setCountry(entity.getCountry());
        dto.setCity(entity.getCity());
        dto.setPricePerAdult(entity.getPricePerAdult());
        dto.setPricePerKid(entity.getPricePerKid());
        dto.setStartDate(entity.getStartDate());
        dto.setStopDate(entity.getStopDate());
        dto.setDeparture(entity.getDeparture());
        dto.setFood(entity.getFood());
        dto.setRequiredDocuments(entity.getRequiredDocuments());
        return dto;
    }
}