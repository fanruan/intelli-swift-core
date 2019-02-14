package com.fr.swift.query.info;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * Created by Lyon on 2018/5/31.
 */
public interface SingleTableQueryInfo<T> extends QueryInfo<T> {

    /**
     * 查询对应的表
     *
     * @return
     */
    SourceKey getTable();

    /**
     * 明细过滤
     *
     * @return
     */
    FilterInfo getFilterInfo();

    /**
     * 维度
     *
     * @return
     */
    List<Dimension> getDimensions();
}
