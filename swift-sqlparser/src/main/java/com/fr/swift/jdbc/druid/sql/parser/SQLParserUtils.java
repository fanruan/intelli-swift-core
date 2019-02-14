/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fr.swift.jdbc.druid.sql.parser;

import com.fr.swift.jdbc.adaptor.parser.SwiftExprParser;
import com.fr.swift.jdbc.adaptor.parser.SwiftLexer;
import com.fr.swift.jdbc.adaptor.parser.SwiftStatementParser;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.fr.swift.jdbc.druid.util.JdbcConstants;

public class SQLParserUtils {

    public static SQLStatementParser createSQLStatementParser(String sql, String dbType) {
        SQLParserFeature[] features;
        if (JdbcConstants.ODPS.equals(dbType) || JdbcConstants.MYSQL.equals(dbType)) {
            features = new SQLParserFeature[]{SQLParserFeature.KeepComments};
        } else {
            features = new SQLParserFeature[]{};
        }
        return createSQLStatementParser(sql, dbType, features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, String dbType, boolean keepComments) {
        SQLParserFeature[] features;
        if (keepComments) {
            features = new SQLParserFeature[]{SQLParserFeature.KeepComments};
        } else {
            features = new SQLParserFeature[]{};
        }

        return createSQLStatementParser(sql, dbType, features);
    }

    public static SQLStatementParser createSQLStatementParser(String sql, String dbType, SQLParserFeature... features) {
        if (JdbcConstants.SWIFT.equals(dbType)) {
            return new SwiftStatementParser(sql, dbType);
        }
        return new SQLStatementParser(sql, dbType);
    }

    public static SQLExprParser createExprParser(String sql, String dbType) {
        if (JdbcConstants.SWIFT.equals(dbType)) {
            return new SwiftExprParser(sql);
        }
        return new SQLExprParser(sql);
    }

    public static Lexer createLexer(String sql, String dbType) {
        if (JdbcConstants.SWIFT.equals(dbType)) {
            return new SwiftLexer(sql);
        }
        return new Lexer(sql);
    }

    public static SQLSelectQueryBlock createSelectQueryBlock(String dbType) {
        return new SQLSelectQueryBlock();
    }
}
