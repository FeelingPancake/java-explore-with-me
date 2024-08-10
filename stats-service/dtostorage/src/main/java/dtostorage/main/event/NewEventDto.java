package dtostorage.main.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import dtostorage.main.Location;
import dtostorage.util.validators.AfterTwoHours;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
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
    Location location;
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    Boolean requestModeration;
    @Size(min = 3, max = 120)
    @NotBlank
    String title;

}
