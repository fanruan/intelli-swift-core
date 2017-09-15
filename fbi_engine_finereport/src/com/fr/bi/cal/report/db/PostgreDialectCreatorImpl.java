package com.fr.bi.cal.report.db;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.general.ComparatorUtils;
import com.fr.stable.UrlDriver;
import com.fr.stable.fun.impl.AbstractDialectCreator;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Roy on 2017/9/15.
 */
public class PostgreDialectCreatorImpl extends AbstractDialectCreator {
    @Override
    public Class<?> generate(UrlDriver driver) {
        if (ComparatorUtils.equals(driver.getDriver(), "org.postgresql.Driver")) {
            return PostgreDialect.class;
        }
        //return null的话, 在外部还能继续从metadata里处理, 从driver获取和从metadata获取是顺序的关系, 不是同级的.
        //driver里获取不到, 再从metadata里找.
        return null;
    }

    @Override
    public Class<?> generate(Connection connection) {
        try {
            if (ComparatorUtils.equals(connection.getMetaData().getDriverName(), "PostgreSQL Native Driver")) {
                return PostgreDialect.class;
            }
        } catch (SQLException e) {
            BILoggerFactory.getLogger(PostgreDialectCreatorImpl.class).error(e.getMessage(), e);
        }
        return null;
    }
}
