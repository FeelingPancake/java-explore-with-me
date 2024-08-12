package dtostorage.main.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import dtostorage.util.validators.AfterTwoHours;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Jacksonized
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @Size(min = 20, max = 2000)
    @NotBlank
    String annotation;
    @NotNull
    Long category;
    @Size(min = 20, max = 7000)
    @NotBlank
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @AfterTwoHours
    LocalDateTime eventDate;
    @NotNull
    EventLocation location;
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    Boolean requestModeration;
    @Size(min = 3, max = 120)
    @NotBlank
    String title;

}
