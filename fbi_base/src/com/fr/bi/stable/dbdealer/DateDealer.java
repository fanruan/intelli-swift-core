package com.fr.bi.stable.dbdealer;

import com.fr.bi.stable.utils.code.BILogger;

import java.sql.ResultSet;
import java.util.Date;

public class DateDealer extends AbstractDealer<Long> {

    public DateDealer(int rsColumn) {
        super(rsColumn);
    }

    @Override
    public Long dealWithResultSet(ResultSet rs) {
        Date date = null;
        try {
            date = rs.getTimestamp(rsColumn);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

}