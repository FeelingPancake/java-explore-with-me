package com.ewm.util.mapper.compilation;

import com.ewm.model.Compilation;
import com.ewm.model.Event;
import com.ewm.repository.EventRepository;
import com.ewm.util.mapper.event.EventMapper;
import dtostorage.main.compilation.CompilationDto;
import dtostorage.main.compilation.NewCompilationDto;
import dtostorage.main.compilation.UpdateCompilationRequest;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    @Mapping(target = "id", source = "compId")
    @Mapping(target = "events", source = "updateCompilationRequest.events", qualifiedByName = "mapIdsToEvents")
    Compilation toCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest,
                              @Context EventRepository eventRepository);

    @Mapping(target = "events", source = "newCompilationDto.events", qualifiedByName = "mapIdsToEvents")
    Compilation toCompilation(NewCompilationDto newCompilationDto, @Context EventRepository eventRepository);

    CompilationDto toCompilationDto(Compilation compilation);

    @Named("mapIdsToEvents")
    default List<Event> mapIdsToEvents(List<Long> events, @Context EventRepository eventRepository) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyList();
        }
        return events.stream()
            .map(eventRepository::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Named("mapEventsToIds")
    default List<Long> mapEventsToIds(List<Event> events) {
        return events.stream()
            .map(Event::getId)
            .collect(Collectors.toList());
    }
}
