package com.fr.swift.source.db.dbdealer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


public class TimestampDealer extends AbstractDealer<Long> {

    public TimestampDealer(int rsColumn) {
        super(rsColumn);
    }

    @Override
    public Long dealWithResultSet(ResultSet rs) throws SQLException {
        Date date = rs.getTimestamp(rsColumn);
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

}