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
package com.fr.swift.jdbc.druid.sql.ast.expr;

import com.fr.swift.jdbc.druid.sql.ast.SQLDataType;
import com.fr.swift.jdbc.druid.sql.ast.SQLExpr;
import com.fr.swift.jdbc.druid.sql.ast.SQLExprImpl;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTVisitor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public final class SQLNotExpr extends SQLExprImpl implements Serializable {

    private static final long serialVersionUID = -1539376062876265885L;
    public SQLExpr expr;

    public SQLNotExpr() {

    }

    public SQLNotExpr(SQLExpr expr) {

        this.expr = expr;
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    @Override
    public void output(StringBuffer buf) {
        buf.append(" NOT ");
        this.expr.output(buf);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }

        visitor.endVisit(this);
    }

    @Override
    public List getChildren() {
        return Collections.singletonList(this.expr);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLNotExpr other = (SQLNotExpr) obj;
        if (expr == null) {
            return other.expr == null;
        } else return expr.equals(other.expr);
    }

    public SQLNotExpr clone() {
        SQLNotExpr x = new SQLNotExpr();
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        return x;
    }

    public SQLDataType computeDataType() {
        return SQLBooleanExpr.DEFAULT_DATA_TYPE;
    }
}
