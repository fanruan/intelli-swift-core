package com.fr.bi.cal.report.db;

import com.fr.general.ComparatorUtils;
import com.fr.stable.UrlDriver;
import com.fr.stable.fun.impl.AbstractDialectCreator;

import java.sql.Connection;

/**
 * Created by Administrator on 2015/12/17 0017.
 */
public class DialectCreatorImpl extends AbstractDialectCreator{

    @Override
    public Class<?> generate(UrlDriver driver) {
        if (ComparatorUtils.equals(driver.getDriver(), "sun.jdbc.odbc.JdbcOdbcDriver")){
            return AdsMysqlOdbcDialect.class;
        }

        //return null的话, 在外部还能继续从metadata里处理, 从driver获取和从metadata获取是顺序的关系, 不是同级的.
        //driver里获取不到, 再从metadata里找.
        return null;
    }

    @Override
    public Class<?> generate(Connection connection) {
        return null;
    }
}