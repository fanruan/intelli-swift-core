package com.fr.swift.source.db.dbdealer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleDealer extends AbstractDealer<Double> {

    public DoubleDealer(int rsColumn) {
        super(rsColumn);
    }

    @Override
    public Double dealWithResultSet(ResultSet rs) throws SQLException{
        double v = rs.getDouble(rsColumn);
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