package com.fr.swift.query.cache;

import com.fr.swift.query.query.QueryBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.util.Clearable;
import com.fr.swift.util.IoUtil;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/16
 */
public class CalcResultSetCache implements QueryCache, Clearable {

    private QueryBean queryBean;

    private SwiftResultSet swiftResultSet;
    private long createTime;

    public CalcResultSetCache(QueryBean queryBean, SwiftResultSet swiftResultSet) {
        this.queryBean = queryBean;
        this.swiftResultSet = swiftResultSet;
        this.createTime = System.currentTimeMillis();
    }

    @Override
    public long getIdle() {
        return System.currentTimeMillis() - this.createTime;
    }

    @Override
    public void update() {
        createTime = System.currentTimeMillis();
    }

    @Override
    public void clear() {
        IoUtil.close(swiftResultSet);
    }

    @Override
    public SwiftResultSet getSwiftResultSet() {
        return swiftResultSet;
    }
}
