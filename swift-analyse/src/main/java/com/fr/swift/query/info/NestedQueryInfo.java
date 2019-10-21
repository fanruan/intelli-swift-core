package com.fr.swift.query.info;

import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.query.QueryInfo;

import java.util.List;

/**
 * Created by Lyon on 2018/6/6.
 */
public interface NestedQueryInfo extends QueryInfo {

    QueryInfo getSubQueryInfo();

    List<PostQueryInfo> getPostQueryInfoList();
}
