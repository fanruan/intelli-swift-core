package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.JSQLParserException;
import com.fr.general.jsqlparser.parser.CCJSqlParserUtil;
import com.fr.general.jsqlparser.statement.Statement;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.util.Crasher;

import java.io.StringReader;

/**
 * Created by pony on 2018/8/17.
 */
public class SqlParserFactory {
    public static QueryBean parsQuery(String sql) throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse(new StringReader(sql));
        QueryBeanVisitor visitor = new QueryBeanVisitor();
        stmt.accept(visitor);
        if (visitor.getQueryBean() != null){
            return visitor.getQueryBean();
        }
        return Crasher.crash(new SwiftJDBCNotSupportedException(sql));
    }


}
