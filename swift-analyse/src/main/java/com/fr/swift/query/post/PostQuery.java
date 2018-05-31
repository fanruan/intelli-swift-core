package com.fr.swift.query.post;

import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by Lyon on 2018/4/26.
 */
public interface PostQuery<T extends SwiftResultSet> extends ResultQuery<T> {

    /**
     * 负责对查询结果最后一步的处理，比如根据结果计算的计算指标、结果过滤、结果过滤之后再汇总等
     */
}
