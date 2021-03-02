package com.fr.swift.cloud.query.post;

import com.fr.swift.cloud.result.qrs.QueryResultSet;

/**
 * @author Lyon
 * @date 2018/4/26
 * <p>
 * 负责对查询结果最后一步的处理，比如根据结果计算的计算指标、结果过滤、结果过滤之后再汇总等
 */
public interface PostQuery<Q extends QueryResultSet<?>> {
}
