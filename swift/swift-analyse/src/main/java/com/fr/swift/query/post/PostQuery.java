package com.fr.swift.query.post;

import com.fr.swift.query.query.Query;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * Created by Lyon on 2018/4/26.
 */
public interface PostQuery<T extends QueryResultSet> extends Query<T> {

    /**
     * 负责对查询结果最后一步的处理，比如根据结果计算的计算指标、结果过滤、结果过滤之后再汇总等
     */
}
