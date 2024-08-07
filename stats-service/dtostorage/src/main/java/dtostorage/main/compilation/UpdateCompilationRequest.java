package dtostorage.main.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Size;
import java.util.List;

@Jacksonized
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    List<Long> events;
    Boolean pinned;
    @Size(min = 1, max = 50)
    String title;
}
