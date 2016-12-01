package com.fr.bi.data;

import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.dbdealer.DBDealer;
import com.fr.bi.stable.dbdealer.TimestampDealer;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.data.core.db.dialect.Dialect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class created on 2016/8/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class NormalExtractor extends DBExtractorImpl {


    @Override
    protected DBDealer dealWithDate(ICubeFieldSource field, int rsColumn) {
        return new TimestampDealer(rsColumn);
    }

    public Statement createStatement(Connection conn, Dialect dialect) throws SQLException {
        Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        try {
            stmt.setFetchSize(dialect.getFetchSize());
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
        return stmt;
    }
}
