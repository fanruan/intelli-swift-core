package com.fr.swift.cloud.query.info.detail;

import com.fr.swift.cloud.query.filter.info.FilterInfo;
import com.fr.swift.cloud.query.info.AbstractQueryInfo;
import com.fr.swift.cloud.query.info.element.dimension.Dimension;
import com.fr.swift.cloud.query.info.element.target.DetailTarget;
import com.fr.swift.cloud.query.limit.Limit;
import com.fr.swift.cloud.query.query.QueryType;
import com.fr.swift.cloud.query.sort.Sort;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.structure.Pair;

import java.util.Comparator;
import java.util.List;

/**
 * @author pony
 * @date 2017/12/11
 */
public class DetailQueryInfo extends AbstractQueryInfo {
    /**
     * 明细表的指标，目前只支持公式
     */
    private List<DetailTarget> targets;
    private List<Sort> sorts;
    private List<Pair<Sort, Comparator>> comparators;
    private Limit limit;

    public DetailQueryInfo(String queryId, int fetchSize, SourceKey table, FilterInfo filterInfo, List<Dimension> dimensions,
                           List<Sort> sorts, List<DetailTarget> targets, Limit limit) {
        super(queryId, fetchSize, table, filterInfo, dimensions);
        this.sorts = sorts;
        this.targets = targets;
        this.limit = limit;
    }

    public List<Pair<Sort, Comparator>> getComparators() {
        return comparators;
    }

    public void setComparators(List<Pair<Sort, Comparator>> comparators) {
        this.comparators = comparators;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public List<DetailTarget> getTargets() {
        return targets;
    }

    @Override
    public QueryType getType() {
        return QueryType.DETAIL;
    }

    public Limit getLimit() {
        return limit;
    }

    public boolean hasSort() {
        return sorts != null && !sorts.isEmpty();
    }
}
