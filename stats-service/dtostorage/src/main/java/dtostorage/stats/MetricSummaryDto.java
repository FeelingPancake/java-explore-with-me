package dtostorage.stats;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MetricSummaryDto {
    String app;
    String uri;
    Long hits;
}
