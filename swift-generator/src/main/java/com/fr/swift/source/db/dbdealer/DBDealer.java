package com.fr.swift.source.db.dbdealer;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBDealer<T> {
    SwiftLogger LOGGER = SwiftLoggers.getLogger(DBDealer.class);
    T dealWithResultSet(ResultSet rs) throws SQLException;

}