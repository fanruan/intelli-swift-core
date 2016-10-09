package com.fr.bi.stable.dbdealer;

import com.finebi.cube.common.log.BILoggerFactory;
import java.sql.ResultSet;
import java.util.Date;


public class TimeDealer extends AbstractDealer<Long> {

    public TimeDealer(int rsColumn) {
        super(rsColumn);
    }

    @Override
    public Long dealWithResultSet(ResultSet rs) {
        Date date = null;
        try {
            date = rs.getTime(rsColumn);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

}