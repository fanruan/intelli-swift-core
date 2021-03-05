package com.fr.swift.cloud.query.info;

import com.fr.swift.cloud.query.info.element.dimension.Dimension;
import com.fr.swift.cloud.query.info.group.post.PostQueryInfo;
import com.fr.swift.cloud.query.query.QueryInfo;
import com.fr.swift.cloud.result.SwiftResultSet;

import java.util.List;

/**
 * todo 这个info通过多个表关联成大表再做groupBy是不是好一点，虽然我们内部可以不通过建关联来做
 * Created by Lyon on 2018/5/31.
 */
public interface ResultJoinQueryInfo<T extends SwiftResultSet> extends QueryInfo {

    // TODO: 2018/6/7 这边的queryInfo只能是GroupQueryInfo？
    List<QueryInfo> getQueryInfoList();

    List<Dimension> getJoinedDimensions();

    List<PostQueryInfo> getPostQueryInfoList();
}
