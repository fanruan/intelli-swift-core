package com.fr.swift.query.builder;

import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * Created by pony on 2017/12/14.
 */
class GroupQueryBuilder {

    /**
     * 给最外层查询节点（查询服务节点）条用并构建query，根据segment分布信息区分本地query和远程query
     *
     * @param bean
     * @return
     */
    static Query<QueryResultSet> buildQuery(GroupQueryInfoBean bean) throws Exception {
        GroupQueryInfo info = (GroupQueryInfo) QueryInfoParser.parse(bean);
        Query<QueryResultSet> query;
        if (GroupQueryInfoUtils.isPagingQuery(info)) {
            query = LocalGroupQueryBuilder.PAGING.buildLocalQuery(info);
        } else {
            query = LocalGroupQueryBuilder.ALL.buildLocalQuery(info);
        }
        return query;
    }
}
