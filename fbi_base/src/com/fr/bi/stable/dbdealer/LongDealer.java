package com.fr.bi.stable.dbdealer;

import com.fr.bi.stable.utils.code.BILogger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LongDealer extends AbstractDealer<Long> {

    public LongDealer(int rsColumn) {
        super(rsColumn);
    }

    @Override
    public Long dealWithResultSet(ResultSet rs) {
        try {
            return rs.getLong(rsColumn);
        } catch (SQLException e1) {
            BILogger.getLogger().error(e1.getMessage(), e1);
        }
        return null;
    }

}