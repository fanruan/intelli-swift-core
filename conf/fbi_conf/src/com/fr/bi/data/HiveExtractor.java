package com.fr.bi.data;

import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.dbdealer.DBDealer;
import com.fr.bi.stable.dbdealer.DateDealer;
import com.fr.bi.stable.dbdealer.TimeDealer;
import com.fr.bi.stable.dbdealer.TimestampDealer;
import com.fr.data.core.db.dialect.Dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class created on 2016/8/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class HiveExtractor extends DBExtractorImpl {

    @Override
    protected DBDealer dealWithDate(ICubeFieldSource field, int rsColumn) {
        DBDealer object;
        switch (field.getClassType()) {
            case DBConstant.CLASS.DATE: {
                object = new DateDealer(rsColumn);
                break;
            }
            case DBConstant.CLASS.TIME: {
                object = new TimeDealer(rsColumn);
                break;
            }
            case DBConstant.CLASS.TIMESTAMP: {
                object = new TimestampDealer(rsColumn);
                break;
            }
            default: {
                object = new TimestampDealer(rsColumn);
            }
        }
        return object;
    }

    @Override
    public Statement createStatement(Connection conn, Dialect dialect) throws SQLException {
        Statement stmt = conn.createStatement();
//        try {
//        //    stmt.setFetchSize(dialect.getFetchSize());
//        } catch (Exception e) {
//        }
        return stmt;
    }
}
