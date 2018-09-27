package com.fr.swift.source.db.dbdealer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DateDealer extends AbstractDealer<Long> {

    public DateDealer(int rsColumn) {
        super(rsColumn);
    }

    @Override
    public Long dealWithResultSet(ResultSet rs) throws SQLException{
        Date date = rs.getDate(rsColumn);
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

}