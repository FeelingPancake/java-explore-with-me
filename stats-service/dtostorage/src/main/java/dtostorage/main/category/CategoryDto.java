package dtostorage.main.category;

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
public class CategoryDto {
    Long id;
    String name;
}
