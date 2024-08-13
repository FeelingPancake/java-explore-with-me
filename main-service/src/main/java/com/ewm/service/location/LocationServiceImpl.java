package com.ewm.service.location;

import com.ewm.exception.NotExistsExeption;
import com.ewm.model.Location;
import com.ewm.repository.LocationRepository;
import com.ewm.util.mapper.location.LocationMapper;
import dtostorage.main.location.CreateLocationRequest;
import dtostorage.main.location.LocationDto;
import dtostorage.main.location.UpdateLocationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper = LocationMapper.INSTANCE;

    @Override
    public LocationDto getLocation(Long locationId) {
        log.debug("Запрос на получение локации по id {}", locationId);
        Location location = locationRepository.findById(locationId).orElseThrow(
            () -> new NotExistsExeption("Локации с " + locationId + " не существует")
        );

        log.debug("Получена локация - {}", location);

        return locationMapper.toLocationDto(location);
    }

    @Override
    public List<LocationDto> getLocations(Integer from, Integer size) {
        log.debug("Запрос на получение локаций с {} до {}", from, size);
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "name"));
        List<Location> locations = locationRepository.findAll(pageable).toList();
        log.debug("Получены локации - {}", locations.stream().map(Location::getId).collect(Collectors.toList()));

        return locations.stream().map(locationMapper::toLocationDto).collect(Collectors.toList());
    }

    @Override
    public LocationDto createLocation(CreateLocationRequest createLocationRequest) {
        log.debug("Запрос на создание локации - {}", createLocationRequest);
        Location location = locationMapper.toLocation(createLocationRequest);
        Location savedLocation = locationRepository.save(location);
        log.debug("Создана локация - {}", savedLocation);

        return locationMapper.toLocationDto(savedLocation);
    }

    @Override
    public LocationDto updateLocation(Long locationId, UpdateLocationRequest updateLocationRequest) {
        log.debug("Запрос на обновление локации - {}", locationId);
        Location existingLocation = locationRepository.findById(locationId).orElseThrow(
            () -> new NotExistsExeption("Локации с " + locationId + " не существуеет")
        );
        Location updatedLocation =
            locationRepository.save(locationMapper.toLocation(updateLocationRequest, existingLocation));
        log.debug("Обновленная локация - {}", updatedLocation);

        return locationMapper.toLocationDto(updatedLocation);
    }

    @Override
    public void deleteLocation(Long locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new NotExistsExeption("Локации - " + locationId + " нет.");
        }
        locationRepository.deleteById(locationId);
    }
}
