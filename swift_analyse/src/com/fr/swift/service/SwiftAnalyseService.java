package com.fr.swift.service;

import com.fr.swift.cal.Query;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.builder.QueryBuilder;
import com.fr.swift.cal.builder.QueryType;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/10/12.
 * 分析服务
 */
public class SwiftAnalyseService extends AbstractSwiftService implements QueryRunner{
    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public boolean start() throws SwiftServiceException {
        boolean start =  super.start();
        QueryRunnerProvider.getInstance().registerRunner(this);
        return start;
    }

    @Override
    public <T extends SwiftResultSet> T getQueryResult(QueryInfo<T> info) throws SQLException {
        Query<T> query = QueryBuilder.buildQuery(info);
        T result = query.getQueryResult();
        // TODO: 2018/4/25 每个节点上面都有service，如何判断是不是请求进来的最外层节点呢？
        if (info.getType() == QueryType.GROUP || info.getType() == QueryType.CROSS_GROUP) {
            GroupNode root = (GroupNode) ((NodeResultSet) result).getNode();
            TargetCalculatorUtils.calculate(root, ((GroupQueryInfo) info).getTargetInfo().getGroupTargets());
        }
        return result;
    }
}
