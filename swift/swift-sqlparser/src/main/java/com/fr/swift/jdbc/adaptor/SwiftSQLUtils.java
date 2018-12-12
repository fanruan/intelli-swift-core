package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.druid.sql.ast.SQLDataType;
import com.fr.swift.jdbc.druid.sql.ast.SQLExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLIdentifierExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLPropertyExpr;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLExprTableSource;
import com.fr.swift.jdbc.druid.util.FnvHash;

import java.sql.Types;

/**
 * Created by lyon on 2018/12/11.
 */
public class SwiftSQLUtils {

    public static int getDataType(String type) {
        if (FnvHash.fnv1a_64_lower(type) == FnvHash.Constants.BOOLEAN) {
            return Types.BOOLEAN;
        } else if (FnvHash.fnv1a_64_lower(type) == FnvHash.Constants.INT) {
            return Types.INTEGER;
        } else if (FnvHash.fnv1a_64_lower(type) == FnvHash.fnv1a_64_lower(SQLDataType.Constants.BIGINT)) {
            return Types.BIGINT;
        } else if (FnvHash.fnv1a_64_lower(type) == FnvHash.Constants.DOUBLE) {
            return Types.DOUBLE;
        } else if (FnvHash.fnv1a_64_lower(type) == FnvHash.Constants.TIMESTAMP) {
            return Types.TIMESTAMP;
        } else if (FnvHash.fnv1a_64_lower(type) == FnvHash.Constants.DATE) {
            return Types.DATE;
        } else {
            return Types.VARCHAR;
        }
    }

    /**
     * @param tableSource
     * @return [tableName, schemaName]
     */
    public static String[] getTableName(SQLExprTableSource tableSource) {
        String[] tableName = new String[2];
        SQLExpr name = tableSource.getExpr();
        if (name instanceof SQLIdentifierExpr) {
            tableName[0] = ((SQLIdentifierExpr) name).getName();
        } else if (name instanceof SQLPropertyExpr) {
            tableName[0] = ((SQLPropertyExpr) name).getName();
            tableName[1] = ((SQLPropertyExpr) name).getOwnernName();
        }
        return tableName;
    }
}
