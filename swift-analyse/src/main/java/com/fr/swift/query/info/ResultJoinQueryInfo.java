package com.fr.swift.query.info;

import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.info.dimension.Dimension;

import java.util.List;

/**
 * Created by Lyon on 2018/5/31.
 */
public interface ResultJoinQueryInfo<T> extends QueryInfo<T> {

    List<QueryInfo<T>> getQueryInfoList();

    List<Dimension> getJoinedDimensions();
}
