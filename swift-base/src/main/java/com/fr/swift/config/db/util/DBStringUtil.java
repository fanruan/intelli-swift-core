package com.fr.swift.config.db.util;

import com.fr.swift.util.Strings;

/**
 * @author yee
 * @date 2018-11-27
 */
public class DBStringUtil {
    public static final int FILL_SIZE_FOR_ORACLE_9i = 2008;

    public DBStringUtil() {
    }

    public static String dealWithClobStringLengthForOracle9i(String str) {
        return str != null && str.length() >= 1000 && str.length() <= 2000 ? Strings.rightPad(str, FILL_SIZE_FOR_ORACLE_9i) : str;
    }
}
