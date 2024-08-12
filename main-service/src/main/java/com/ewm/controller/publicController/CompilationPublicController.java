package com.ewm.controller.publicController;

import com.ewm.service.compilation.CompilationService;
import dtostorage.main.compilation.CompilationDto;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero
                                                Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") @Positive
                                                Integer size) {

        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable(name = "compId") Long compId) {
        return compilationService.getCompilation(compId);
    }
}
