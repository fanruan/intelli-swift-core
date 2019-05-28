package com.fr.swift.cloud.analysis.downtime;

import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.source.Row;

/**
 * This class created on 2019/5/24
 *
 * @author Lucifer
 * @description
 */
public class ExecutionSqlData {

    private String executionId;
    private long rows;
    private long columns;

    public ExecutionSqlData(Row row) {
        this.executionId = row.getValue(0);
        this.rows = row.getValue(1);
        this.columns = row.getValue(2);
    }

    public long getDatas() {
        return rows * columns;
    }

    public static DimensionBean[] getDimensions() {
        DimensionBean dimensionBean1 = new DimensionBean(DimensionType.DETAIL, "executionId");
        DimensionBean dimensionBean2 = new DimensionBean(DimensionType.DETAIL, "rows");
        DimensionBean dimensionBean3 = new DimensionBean(DimensionType.DETAIL, "columns");
        return new DimensionBean[]{dimensionBean1, dimensionBean2, dimensionBean3};
    }
}
