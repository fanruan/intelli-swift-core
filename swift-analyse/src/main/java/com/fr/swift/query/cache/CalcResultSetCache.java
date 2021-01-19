package com.fr.swift.query.cache;

import com.fr.swift.analyse.CalcPage;
import com.fr.swift.analyse.CalcSegment;
import com.fr.swift.analyse.merged.BaseDetailResultSet;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.builder.CalcDetailQueryBuilder;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.post.meta.SwiftMetaDataUtils;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.util.Clearable;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.function.Function;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/16
 */
public class CalcResultSetCache implements QueryCache, Clearable {

    private QueryBean queryBean;
    private CalcSegment calcSegment;
    private SwiftResultSet swiftResultSet;
    private long createTime;
    private Function<QueryBean, SwiftResultSet> function;

    public CalcResultSetCache(QueryBean queryBean, Function<QueryBean, SwiftResultSet> function) {
        this.queryBean = queryBean;
        this.function = function;
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
        if (swiftResultSet == null) {
            BaseDetailResultSet baseDetailResultSet = (BaseDetailResultSet) function.apply(queryBean);
            try {
                baseDetailResultSet.setMetaData(SwiftMetaDataUtils.createMetaData(queryBean));
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("get merged result set failed", e);
            }
            swiftResultSet = baseDetailResultSet;
        }
        return swiftResultSet;
    }

    public SwiftResultSet getQueryCalcPage() {
        if (calcSegment == null) {
            try {
                calcSegment = CalcDetailQueryBuilder.of((DetailQueryInfoBean) queryBean).buildCalcSegment();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error("build real result set failed", e);
            }
        }
        return new CalcPage(calcSegment.getFetchSize(), calcSegment.getPage(), calcSegment.hasNextPage());
    }
}
