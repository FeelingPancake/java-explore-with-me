package stats.repository;

import stats.model.Metric;
import stats.model.MetricSummary;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MetricRepositoryCustomImpl implements MetricRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<MetricSummary> findMetricSummaries(String[] uris, LocalDateTime start, LocalDateTime end,
                                                   boolean unique) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MetricSummary> query = cb.createQuery(MetricSummary.class);
        Root<Metric> root = query.from(Metric.class);

        List<Predicate> predicates = new ArrayList<>();

        if (uris != null && uris.length > 0) {
            predicates.add(root.get("uri").in((Object[]) uris));
        }

        predicates.add(cb.between(root.get("timestamp"), start, end));

        query.groupBy(root.get("app"), root.get("uri"));

        query.multiselect(
            root.get("app"),
            root.get("uri"),
            unique ? cb.countDistinct(root.get("ip")) : cb.count(root.get("ip"))
        );

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.desc(unique ? cb.countDistinct(root.get("ip")) : cb.count(root.get("ip"))));

        return entityManager.createQuery(query).getResultList();
    }
}

