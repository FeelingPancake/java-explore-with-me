package com.ewm.util.mapper.location;

import com.ewm.model.Location;
import dtostorage.main.location.CreateLocationRequest;
import dtostorage.main.location.LocationDto;
import dtostorage.main.location.UpdateLocationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    LocationDto toLocationDto(Location location);

    @Mapping(target = "id", ignore = true)
    Location toLocation(CreateLocationRequest createLocationRequest);

    @Mapping(target = "id", source = "existingLocation.id")
    @Mapping(target = "name", source = "updateLocationRequest.name")
    @Mapping(target = "latitude", expression = "java(updateLocationRequest.getLatitude() == null ? existingLocation.getLatitude() : updateLocationRequest.getLatitude())")
    @Mapping(target = "longitude", expression = "java(updateLocationRequest.getLongitude() == null ? existingLocation.getLongitude() : updateLocationRequest.getLongitude())")
    @Mapping(target = "radius", expression = "java(updateLocationRequest.getRadius() == null ? existingLocation.getRadius() : updateLocationRequest.getRadius())")
    Location toLocation(UpdateLocationRequest updateLocationRequest, Location existingLocation);
}
