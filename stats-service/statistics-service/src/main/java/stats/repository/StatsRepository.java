package stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stats.model.Metric;

public interface StatsRepository extends JpaRepository<Metric, Long>, MetricRepositoryCustom {
}
