package com.fr.swift.query.builder;

import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * Created by pony on 2017/12/13.
 */
class DetailQueryBuilder {

    /**
     * 给最外层查询节点（查询服务节点）条用并构建query，根据segment分布信息区分本地query和远程query
     *
     * @param bean
     * @return
     */
    static <T extends QueryResultSet> Query<T> buildQuery(DetailQueryInfoBean bean) throws Exception {
        DetailQueryInfo info = (DetailQueryInfo) QueryInfoParser.parse(bean);
        Query<T> query;
        if (info.hasSort()) {
            query = LocalDetailQueryBuilder.GROUP.buildLocalQuery(info);
        } else {
            query = LocalDetailQueryBuilder.NORMAL.buildLocalQuery(info);
        }
        return query;
    }
}
