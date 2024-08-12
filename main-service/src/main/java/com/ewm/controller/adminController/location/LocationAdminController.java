package com.ewm.controller.adminController.location;

import com.ewm.service.location.LocationService;
import dtostorage.main.location.CreateLocationRequest;
import dtostorage.main.location.LocationDto;
import dtostorage.main.location.UpdateLocationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
public class LocationAdminController {
    private final LocationService locationService;

    @GetMapping
    public List<LocationDto> getLocations(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return locationService.getLocations(from, size);
    }

    @GetMapping("/{locationId}")
    public LocationDto getLocation(@PathVariable(name = "locationId") Long locationId) {
        return locationService.getLocation(locationId);
    }

    @PostMapping
    public LocationDto createLocation(@RequestBody @Valid CreateLocationRequest createLocationRequest) {
        return locationService.createLocation(createLocationRequest);
    }

    @PatchMapping("/{locationId}")
    public LocationDto updateLocation(@PathVariable(name = "locationId") Long locationId,
                                      @RequestBody @Valid UpdateLocationRequest updateLocationRequest) {
        return locationService.updateLocation(locationId, updateLocationRequest);
    }

    @DeleteMapping("/{locationId}")
    public void deleteLocation(Long locationId) {
        locationService.deleteLocation(locationId);
    }
}

