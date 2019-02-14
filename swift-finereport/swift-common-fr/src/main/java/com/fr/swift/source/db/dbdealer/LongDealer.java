package com.fr.swift.source.db.dbdealer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LongDealer extends AbstractDealer<Long> {

    public LongDealer(int rsColumn) {
        super(rsColumn);
    }

    @Override
    public Long dealWithResultSet(ResultSet rs) throws SQLException{
        long v = rs.getLong(rsColumn);
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