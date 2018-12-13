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
package com.fr.swift.jdbc.adaptor.parser;


import com.fr.swift.jdbc.druid.sql.parser.Lexer;
import com.fr.swift.jdbc.druid.sql.parser.SQLStatementParser;

public class SwiftStatementParser extends SQLStatementParser {
    public SwiftStatementParser(String sql) {
        super(new SwiftExprParser(sql));
    }

    public SwiftStatementParser(String sql, String dbType) {
        super(new SwiftExprParser(sql, dbType));
    }

    public SwiftStatementParser(Lexer lexer) {
        super(new SwiftExprParser(lexer));
    }

    public SwiftSelectParser createSQLSelectParser() {
        return new SwiftSelectParser(this.exprParser, selectListCache);
    }

}
