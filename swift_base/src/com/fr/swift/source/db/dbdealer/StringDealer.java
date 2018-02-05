package com.fr.swift.source.db.dbdealer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringDealer extends AbstractDealer<String> {

    public StringDealer(int rsColumn) {
        super(rsColumn);
    }

    @Override
    public String dealWithResultSet(ResultSet rs) throws SQLException{
        return rs.getString(rsColumn);
    }

}