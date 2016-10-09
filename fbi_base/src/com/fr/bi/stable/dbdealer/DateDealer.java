package com.fr.bi.stable.dbdealer;

import com.finebi.cube.common.log.BILoggerFactory;
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
            date = rs.getDate(rsColumn);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

}