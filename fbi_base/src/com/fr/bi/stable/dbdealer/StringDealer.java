package com.fr.bi.stable.dbdealer;

import com.finebi.cube.common.log.BILoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringDealer extends AbstractDealer<String> {

    public StringDealer(int rsColumn) {
        super(rsColumn);
    }

    @Override
    public String dealWithResultSet(ResultSet rs) {
        try {
            return rs.getString(rsColumn);
        } catch (SQLException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

}