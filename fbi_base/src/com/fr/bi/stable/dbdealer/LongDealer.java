package com.fr.bi.stable.dbdealer;

import com.finebi.cube.common.log.BILoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LongDealer extends AbstractDealer<Long> {

    public LongDealer(int rsColumn) {
        super(rsColumn);
    }

    @Override
    public Long dealWithResultSet(ResultSet rs) {
        long v = 0L;
        try {
            v = rs.getLong(rsColumn);
        } catch (SQLException e1) {
            BILoggerFactory.getLogger().error(e1.getMessage(), e1);
        }
        if (v == 0) {
            try {
                return rs.getObject(rsColumn) == null ? null : new Long(0);
            } catch (Exception e) {
                return 0L;
            }
        } else {
            return v;
        }
    }

}