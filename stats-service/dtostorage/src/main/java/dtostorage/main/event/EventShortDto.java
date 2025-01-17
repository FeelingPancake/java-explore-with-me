package dtostorage.main.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import dtostorage.main.category.CategoryDto;
import dtostorage.main.user.UserShortDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String date;
    Long id;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Long views;
}
