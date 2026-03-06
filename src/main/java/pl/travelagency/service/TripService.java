package pl.travelagency.service;

import org.springframework.stereotype.Service;
import pl.travelagency.db.TripEntity;
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