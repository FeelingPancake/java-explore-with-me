package dtostorage.main;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Jacksonized
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @NotNull
    Double lat;
    @NotNull
    Double lon;
}
