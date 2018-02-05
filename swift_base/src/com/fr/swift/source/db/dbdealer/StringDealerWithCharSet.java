package com.fr.swift.source.db.dbdealer;

import com.fr.swift.util.Crasher;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringDealerWithCharSet extends StringDealer {

    private String originalCharSetName;
    private String newCharSetName;

    public StringDealerWithCharSet(int rsColumn, String originalCharSetName, String newCharSetName) {
        super(rsColumn);
        this.originalCharSetName = originalCharSetName;
        this.newCharSetName = newCharSetName;
    }

    private static String dealWithValueCharSet(String value, String originalCharSetName, String newCharSetName) {
        if (value != null) {
            try {
                return new String(value.getBytes(originalCharSetName), newCharSetName);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e.getMessage(), e);
                Crasher.crash(e);
            }
        }
        return value;
    }

    @Override
    public String dealWithResultSet(ResultSet rs) throws SQLException {
        return dealWithValueCharSet(super.dealWithResultSet(rs), originalCharSetName, newCharSetName);
    }

}