package com.fr.bi.stable.dbdealer;

import com.fr.bi.stable.utils.code.BILogger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleDealer extends AbstractDealer<Double> {

    public DoubleDealer(int rsColumn) {
        super(rsColumn);
    }

    @Override
    public Double dealWithResultSet(ResultSet rs) {
        double v = 0;
        try {
            v = rs.getDouble(rsColumn);
        } catch (SQLException e1) {
            BILogger.getLogger().error(e1.getMessage(), e1);
        }
        if (v == 0) {
            try {
                return rs.getObject(rsColumn) == null ? null : new Double(0);
            } catch (Exception e) {
                return 0d;
            }
        } else {
            return v;
        }
    }

}