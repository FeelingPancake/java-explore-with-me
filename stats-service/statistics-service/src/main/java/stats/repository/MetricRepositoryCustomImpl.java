package stats.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import stats.model.Metric;
import stats.model.MetricSummary;

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

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        query.groupBy(root.get("app"), root.get("uri"));

        Expression<Long> countExpression = unique ? cb.countDistinct(root.get("ip")) : cb.count(root.get("ip"));
        query.select(cb.construct(MetricSummary.class, root.get("app"), root.get("uri"), countExpression));

        query.orderBy(cb.desc(countExpression));

        return entityManager.createQuery(query).getResultList();
    }
}

