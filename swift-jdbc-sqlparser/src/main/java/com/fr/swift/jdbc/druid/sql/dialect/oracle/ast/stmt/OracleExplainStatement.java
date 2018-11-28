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
package com.fr.swift.jdbc.druid.sql.dialect.oracle.ast.stmt;

import com.fr.swift.jdbc.druid.sql.SQLUtils;
import com.fr.swift.jdbc.druid.sql.ast.SQLExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLCharExpr;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLExplainStatement;
import com.fr.swift.jdbc.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTVisitor;
import com.fr.swift.jdbc.druid.util.JdbcConstants;

public class OracleExplainStatement extends SQLExplainStatement implements OracleStatement {

    private SQLCharExpr statementId;
    private SQLExpr     into;
    
    public OracleExplainStatement() {
        super (JdbcConstants.ORACLE);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, statementId);
            acceptChild(visitor, into);
            acceptChild(visitor, statement);
        }
        visitor.endVisit(this);
    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    public String toString() {
        return SQLUtils.toOracleString(this);
    }

    public SQLCharExpr getStatementId() {
        return statementId;
    }

    public void setStatementId(SQLCharExpr statementId) {
        this.statementId = statementId;
    }

    public SQLExpr getInto() {
        return into;
    }

    public void setInto(SQLExpr into) {
        this.into = into;
    }

}
