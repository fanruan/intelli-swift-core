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
package com.fr.swift.jdbc.druid.sql.ast.statement;

import com.fr.swift.jdbc.druid.sql.SQLUtils;
import com.fr.swift.jdbc.druid.sql.ast.SQLExpr;
import com.fr.swift.jdbc.druid.sql.ast.SQLHint;
import com.fr.swift.jdbc.druid.sql.ast.SQLObject;
import com.fr.swift.jdbc.druid.sql.ast.SQLObjectImpl;
import com.fr.swift.jdbc.druid.sql.ast.SQLOrderBy;
import com.fr.swift.jdbc.druid.sql.ast.SQLStatement;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLAllColumnExpr;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTOutputVisitor;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLSelect extends SQLObjectImpl {

    protected SQLWithSubqueryClause withSubQuery;
    protected SQLSelectQuery query;
    protected SQLOrderBy orderBy;

    protected List<SQLHint> hints;

    protected SQLObject restriction;

    protected boolean forBrowse;
    protected List<String> forXmlOptions = null;
    protected SQLExpr xmlPath;

    protected SQLExpr rowCount;
    protected SQLExpr offset;

    public SQLSelect() {

    }

    public List<SQLHint> getHints() {
        if (hints == null) {
            hints = new ArrayList<SQLHint>(2);
        }
        return hints;
    }

    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }
        return hints.size();
    }

    public SQLSelect(SQLSelectQuery query) {
        this.setQuery(query);
    }

    public SQLWithSubqueryClause getWithSubQuery() {
        return withSubQuery;
    }

    public void setWithSubQuery(SQLWithSubqueryClause withSubQuery) {
        this.withSubQuery = withSubQuery;
    }

    public SQLSelectQuery getQuery() {
        return this.query;
    }

    public void setQuery(SQLSelectQuery query) {
        if (query != null) {
            query.setParent(this);
        }
        this.query = query;
    }

    public SQLSelectQueryBlock getQueryBlock() {
        if (query instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) query;
        }

        return null;
    }

    public SQLOrderBy getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        if (orderBy != null) {
            orderBy.setParent(this);
        }
        this.orderBy = orderBy;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.withSubQuery);
            acceptChild(visitor, this.query);
            acceptChild(visitor, this.restriction);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.hints);
            acceptChild(visitor, this.offset);
            acceptChild(visitor, this.rowCount);
        }

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderBy == null) ? 0 : orderBy.hashCode());
        result = prime * result + ((query == null) ? 0 : query.hashCode());
        result = prime * result + ((withSubQuery == null) ? 0 : withSubQuery.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SQLSelect other = (SQLSelect) obj;
        if (orderBy == null) {
            if (other.orderBy != null) return false;
        } else if (!orderBy.equals(other.orderBy)) return false;
        if (query == null) {
            if (other.query != null) return false;
        } else if (!query.equals(other.query)) return false;
        if (withSubQuery == null) {
            return other.withSubQuery == null;
        } else return withSubQuery.equals(other.withSubQuery);
    }

    public void output(StringBuffer buf) {
        String dbType = null;

        SQLObject parent = this.getParent();
        if (parent instanceof SQLStatement) {
            dbType = ((SQLStatement) parent).getDbType();
        }

        if (dbType == null && query instanceof SQLSelectQueryBlock) {
            dbType = ((SQLSelectQueryBlock) query).dbType;
        }

        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(buf, dbType);
        this.accept(visitor);
    }

    public String toString() {
        SQLObject parent = this.getParent();
        if (parent instanceof SQLStatement) {
            String dbType = ((SQLStatement) parent).getDbType();

            if (dbType != null) {
                return SQLUtils.toSQLString(this, dbType);
            }
        }

        if (query instanceof SQLSelectQueryBlock) {
            String dbType = ((SQLSelectQueryBlock) query).dbType;

            if (dbType != null) {
                return SQLUtils.toSQLString(this, dbType);
            }
        }

        return super.toString();
    }

    public SQLSelect clone() {
        SQLSelect x = new SQLSelect();

        x.withSubQuery = this.withSubQuery;
        if (query != null) {
            x.setQuery(query.clone());
        }

        if (orderBy != null) {
            x.setOrderBy(this.orderBy.clone());
        }
        if (restriction != null) {
            x.setRestriction(restriction.clone());
        }

        if (this.hints != null) {
            for (SQLHint hint : this.hints) {
                x.hints.add(hint);
            }
        }

        x.forBrowse = forBrowse;

        if (forXmlOptions != null) {
            x.forXmlOptions = new ArrayList<String>(forXmlOptions);
        }

        if (xmlPath != null) {
            x.setXmlPath(xmlPath.clone());
        }

        if (rowCount != null) {
            x.setRowCount(rowCount.clone());
        }

        if (offset != null) {
            x.setOffset(offset.clone());
        }

        return x;
    }

    public boolean isSimple() {
        return withSubQuery == null
                && (hints == null || hints.size() == 0)
                && restriction == null
                && (!forBrowse)
                && (forXmlOptions == null || forXmlOptions.size() == 0)
                && xmlPath == null
                && rowCount == null
                && offset == null;
    }

    public SQLObject getRestriction() {
        return this.restriction;
    }

    public void setRestriction(SQLObject restriction) {
        if (restriction != null) {
            restriction.setParent(this);
        }
        this.restriction = restriction;
    }

    public boolean isForBrowse() {
        return forBrowse;
    }

    public void setForBrowse(boolean forBrowse) {
        this.forBrowse = forBrowse;
    }

    public List<String> getForXmlOptions() {
        if (forXmlOptions == null) {
            forXmlOptions = new ArrayList<String>(4);
        }

        return forXmlOptions;
    }

    public int getForXmlOptionsSize() {
        if (forXmlOptions == null) {
            return 0;
        }
        return forXmlOptions.size();
    }

    public SQLExpr getRowCount() {
        return rowCount;
    }

    public void setRowCount(SQLExpr rowCount) {
        if (rowCount != null) {
            rowCount.setParent(this);
        }

        this.rowCount = rowCount;
    }

    public SQLExpr getOffset() {
        return offset;
    }

    public void setOffset(SQLExpr offset) {
        if (offset != null) {
            offset.setParent(this);
        }
        this.offset = offset;
    }

    public SQLExpr getXmlPath() {
        return xmlPath;
    }

    public void setXmlPath(SQLExpr xmlPath) {
        if (xmlPath != null) {
            xmlPath.setParent(this);
        }
        this.xmlPath = xmlPath;
    }

    public SQLSelectQueryBlock getFirstQueryBlock() {
        if (query instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) query;
        }

        if (query instanceof SQLUnionQuery) {
            return ((SQLUnionQuery) query).getFirstQueryBlock();
        }

        return null;
    }

    public boolean addWhere(SQLExpr where) {
        if (where == null) {
            return false;
        }

        if (query instanceof SQLSelectQueryBlock) {
            ((SQLSelectQueryBlock) query).addWhere(where);
            return true;
        }

        if (query instanceof SQLUnionQuery) {
            SQLSelectQueryBlock queryBlock = new SQLSelectQueryBlock();
            queryBlock.setFrom(new SQLSelect(query), "u");
            queryBlock.addSelectItem(new SQLAllColumnExpr());
            queryBlock.setParent(queryBlock);
            query = queryBlock;
            return true;
        }

        return false;
    }
}
