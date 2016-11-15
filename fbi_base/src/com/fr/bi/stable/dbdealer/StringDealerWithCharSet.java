package com.fr.bi.stable.dbdealer;

import com.finebi.cube.common.log.BILoggerFactory;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;

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
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
        return value;
    }

    @Override
    public String dealWithResultSet(ResultSet rs) {
        return dealWithValueCharSet(super.dealWithResultSet(rs), originalCharSetName, newCharSetName);
    }

}