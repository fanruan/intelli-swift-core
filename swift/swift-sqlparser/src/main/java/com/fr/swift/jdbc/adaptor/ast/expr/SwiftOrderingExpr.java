package com.fr.swift.jdbc.adaptor.ast.expr;

import com.fr.swift.jdbc.druid.sql.ast.SQLExpr;
import com.fr.swift.jdbc.druid.sql.ast.SQLExprImpl;
import com.fr.swift.jdbc.druid.sql.ast.SQLOrderingSpecification;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

/**
 * Created by lyon on 2018/12/7.
 */
public class SwiftOrderingExpr extends SQLExprImpl {

    protected SQLExpr expr;
    protected SQLOrderingSpecification type;

    public SwiftOrderingExpr() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {

    }

    public SwiftOrderingExpr(SQLExpr expr, SQLOrderingSpecification type) {
        super();
        setExpr(expr);
        this.type = type;
    }

    public SwiftOrderingExpr clone() {
        SwiftOrderingExpr x = new SwiftOrderingExpr();
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        x.type = type;
        return x;
    }

    @Override
    public List getChildren() {
        return Collections.singletonList(this.expr);
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    public SQLOrderingSpecification getType() {
        return type;
    }

    public void setType(SQLOrderingSpecification type) {
        this.type = type;
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
        SwiftOrderingExpr other = (SwiftOrderingExpr) obj;
        if (expr != other.expr) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

}
