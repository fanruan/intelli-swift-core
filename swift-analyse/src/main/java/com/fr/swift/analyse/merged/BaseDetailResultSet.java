package com.fr.swift.analyse.merged;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * @author xiqiu
 * @date 2021/1/18
 * @description
 * @since swift-1.2.0
 */
public abstract class BaseDetailResultSet implements SwiftResultSet {
    protected SwiftMetaData swiftMetaData;

    public abstract void setMetaData(SwiftMetaData swiftMetaData);

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return swiftMetaData;
    }
}
