package com.fr.swift.query.builder;

import com.fr.swift.query.info.group.GroupQueryInfo;

/**
 * Created by Lyon on 2018/6/14.
 */
public class GroupQueryInfoUtils {

    public static boolean isPagingQuery(GroupQueryInfo info) {
        // TODO: 2018/6/14 暂时只要有postQuery都不分页，后面再细分
        return info.getPostQueryInfoList().isEmpty();
    }
}
