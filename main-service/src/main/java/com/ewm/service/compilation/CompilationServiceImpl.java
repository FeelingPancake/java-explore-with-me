package com.ewm.service.compilation;

import com.ewm.exception.NotExistsExeption;
import com.ewm.model.Compilation;
import com.ewm.repository.CompilationRepository;
import com.ewm.repository.EventRepository;
import com.ewm.util.mapper.compilation.CompilationMapper;
import dtostorage.main.compilation.CompilationDto;
import dtostorage.main.compilation.NewCompilationDto;
import dtostorage.main.compilation.UpdateCompilationRequest;
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
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper = CompilationMapper.INSTANCE;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto, eventRepository);

        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
            new NotExistsExeption("Подборки - " + compId + " нет."));

        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation existCompilation = compilationRepository.findById(compId).orElseThrow(() ->
            new NotExistsExeption("Подборки - " + compId + " нет."));

        Compilation templateCompilation =
            compilationMapper.toCompilation(compId, updateCompilationRequest, eventRepository);

        Compilation updateCompilation = templateCompilation.toBuilder()
            .title(
                templateCompilation.getTitle() == null ? existCompilation.getTitle() : templateCompilation.getTitle())
            .pinned(templateCompilation.getPinned() == null ? existCompilation.getPinned() :
                templateCompilation.getPinned())
            .build();

        return compilationMapper.toCompilationDto(compilationRepository.save(updateCompilation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        List<CompilationDto> compilations;

        if (pinned != null) {
            return compilations = compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(compilationMapper::toCompilationDto).collect(Collectors.toList());
        } else {
            return compilations = compilationRepository.findAll(pageable).toList().stream()
                .map(compilationMapper::toCompilationDto).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
            new NotExistsExeption("Подборки с " + compId + " нет."));

        return compilationMapper.toCompilationDto(compilation);
    }


}
