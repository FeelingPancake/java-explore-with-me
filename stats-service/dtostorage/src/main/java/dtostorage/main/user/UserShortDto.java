package dtostorage.main.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {
    Long id;
    String name;
}
