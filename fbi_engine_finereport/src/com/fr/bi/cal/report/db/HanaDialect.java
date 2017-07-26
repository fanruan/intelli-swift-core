package com.fr.bi.cal.report.db;

import com.fr.data.core.db.dialect.AbstractDialect;

import java.sql.Connection;

/**
 * Created by Roy on 2017/7/26.
 */
public class HanaDialect extends AbstractDialect {
    @Override
    public String defaultValidationQuery(Connection connection) {
        return "select top 5000 distinct \"YEAR_OF_SALE\",SUM(\"GROSSAMOUNT\") AS \"GROSSAMOUNT_SUM\" FROM \"_SYS_BIC\".\"sap.hana.democontent.epm.spatial.models/REGION_SALES_BP\" group by \"YEAR_OF_SALE\" order by \"YEAR_OF_SALE\" asc  ";
    }
}
