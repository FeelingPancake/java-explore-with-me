package com.ewm.service.location;

import dtostorage.main.location.CreateLocationRequest;
import dtostorage.main.location.LocationDto;
import dtostorage.main.location.UpdateLocationRequest;

import java.util.List;

public interface LocationService {
    LocationDto getLocation(Long locationId);

    List<LocationDto> getLocations(Integer from, Integer size);

    LocationDto createLocation(CreateLocationRequest createLocationRequest);

    LocationDto updateLocation(Long locationId, UpdateLocationRequest updateLocationRequest);

    void deleteLocation(Long locationId);
}
