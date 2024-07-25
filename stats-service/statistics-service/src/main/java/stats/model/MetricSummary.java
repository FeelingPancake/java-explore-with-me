package stats.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MetricSummary {
    String app;
    String uri;
    Long hits;

    public MetricSummary(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
