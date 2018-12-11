package com.fr.swift.jdbc.adaptor;

import com.fr.swift.jdbc.druid.sql.ast.SQLExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLBetweenExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLIdentifierExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLInListExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLNotExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLTextLiteralExpr;
import com.fr.swift.jdbc.druid.sql.visitor.SQLASTVisitorAdapter;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.OrFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.value.RangeFilterValueBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lyon on 2018/12/7.
 */
class WhereASTVisitorAdapter extends SQLASTVisitorAdapter implements FilterInfoBeanParser {

    private FilterInfoBean bean;

    // where
    @Override
    public boolean visit(SQLInListExpr x) {
        InSQLASTVisitorAdapter visitor = new InSQLASTVisitorAdapter();
        visitor.visit(x);
        bean = visitor.getFilterInfoBean();
        return false;
    }

    @Override
    public boolean visit(SQLBetweenExpr x) {
        BetweenSQLASTVisitorAdapter visitor = new BetweenSQLASTVisitorAdapter();
        visitor.visit(x);
        bean = visitor.getFilterInfoBean();
        return false;
    }

    @Override
    public boolean visit(SQLNotExpr x) {
        if (x == null) {
            return false;
        }
        bean = new NotFilterBean();
        WhereASTVisitorAdapter visitor = new WhereASTVisitorAdapter();
        if (x.getExpr() != null) {
            x.getExpr().accept(visitor);
            bean.setFilterValue(visitor.getFilterInfoBean());
        }
        return false;
    }

    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        if (x == null) {
            return false;
        }
        switch (x.getOperator()) {
            case BooleanAnd: {
                bean = new AndFilterBean();
                bean.setFilterValue(parse(Arrays.asList(x.getLeft(), x.getRight())));
                break;
            }
            case BooleanOr: {
                bean = new OrFilterBean();
                bean.setFilterValue(parse(Arrays.asList(x.getLeft(), x.getRight())));
                break;
            }
            case Equality: {
                bean = new InFilterBean();
                if (x.getLeft() instanceof SQLIdentifierExpr) {
                    ((InFilterBean) bean).setColumn(((SQLIdentifierExpr) x.getLeft()).getName());
                }
                if (x.getRight() != null) {
                    ((InFilterBean) bean).setFilterValue(Collections.singleton(getLiteralValue(x.getRight())));
                }
                break;
            }
            case GreaterThan:
                bean = new NumberInRangeFilterBean();
                ((NumberInRangeFilterBean) bean).setColumn(getColumnName(x.getLeft()));
                RangeFilterValueBean gtValue = new RangeFilterValueBean();
                gtValue.setStart(getLiteralValue(x.getRight()));
                bean.setFilterValue(gtValue);
                break;
            case GreaterThanOrEqual:
                bean = new NumberInRangeFilterBean();
                ((NumberInRangeFilterBean) bean).setColumn(getColumnName(x.getLeft()));
                RangeFilterValueBean gteValue = new RangeFilterValueBean();
                gteValue.setStart(getLiteralValue(x.getRight()));
                gteValue.setStartIncluded(true);
                bean.setFilterValue(gteValue);
                break;
            case LessThan:
                bean = new NumberInRangeFilterBean();
                ((NumberInRangeFilterBean) bean).setColumn(getColumnName(x.getLeft()));
                RangeFilterValueBean ltValue = new RangeFilterValueBean();
                ltValue.setEnd(getLiteralValue(x.getRight()));
                bean.setFilterValue(ltValue);
                break;
            case LessThanOrEqual:
                bean = new NumberInRangeFilterBean();
                ((NumberInRangeFilterBean) bean).setColumn(getColumnName(x.getLeft()));
                RangeFilterValueBean lteValue = new RangeFilterValueBean();
                lteValue.setEnd(getLiteralValue(x.getRight()));
                lteValue.setEndIncluded(true);
                bean.setFilterValue(lteValue);
                break;
        }
        return false;
    }

    private static String getColumnName(SQLExpr expr) {
        if (expr instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) expr).getName();
        }
        return null;
    }

    private static String getLiteralValue(SQLExpr expr) {
        if (expr instanceof SQLNumericLiteralExpr) {
            return ((SQLNumericLiteralExpr) expr).getNumber().toString();
        } else if (expr instanceof SQLTextLiteralExpr) {
            return ((SQLTextLiteralExpr) expr).getText();
        }
        return null;
    }

    private static List<FilterInfoBean> parse(List<SQLExpr> exprList) {
        List<FilterInfoBean> filterInfoBeanList = new ArrayList<FilterInfoBean>();
        for (SQLExpr expr : exprList) {
            if (expr == null) {
                continue;
            }
            WhereASTVisitorAdapter visitor = new WhereASTVisitorAdapter();
            expr.accept(visitor);
            if (visitor.getFilterInfoBean() != null) {
                filterInfoBeanList.add(visitor.getFilterInfoBean());
            }
        }
        return filterInfoBeanList;
    }

    @Override
    public FilterInfoBean getFilterInfoBean() {
        return bean;
    }

    // 不包括边界
    private static class BetweenSQLASTVisitorAdapter extends SQLASTVisitorAdapter implements FilterInfoBeanParser {

        private NumberInRangeFilterBean numberInRangeFilterBean;

        @Override
        public boolean visit(SQLBetweenExpr x) {
            if (x == null) {
                return false;
            }
            SQLExpr col = x.getTestExpr();
            if (col instanceof SQLIdentifierExpr) {
                numberInRangeFilterBean = new NumberInRangeFilterBean();
                numberInRangeFilterBean.setColumn(((SQLIdentifierExpr) col).getName());
                RangeFilterValueBean bean = new RangeFilterValueBean();
                SQLExpr start = x.getBeginExpr();
                if (start instanceof SQLNumericLiteralExpr) {
                    bean.setStart(((SQLNumericLiteralExpr) start).getNumber().toString());
                } else if (start instanceof SQLTextLiteralExpr) {
                    bean.setStart(((SQLTextLiteralExpr) start).getText());
                }
                SQLExpr end = x.getEndExpr();
                if (end instanceof SQLNumericLiteralExpr) {
                    bean.setEnd(((SQLNumericLiteralExpr) end).getNumber().toString());
                } else if (end instanceof SQLTextLiteralExpr) {
                    bean.setStart(((SQLTextLiteralExpr) end).getText());
                }
                numberInRangeFilterBean.setFilterValue(bean);
            }
            return false;
        }

        @Override
        public FilterInfoBean getFilterInfoBean() {
            return numberInRangeFilterBean;
        }
    }

    private static class InSQLASTVisitorAdapter extends SQLASTVisitorAdapter implements FilterInfoBeanParser {

        private InFilterBean in;

        @Override
        public boolean visit(SQLInListExpr x) {
            if (x == null) {
                return false;
            }
            SQLExpr col = x.getExpr();
            if (col instanceof SQLIdentifierExpr) {
                in = new InFilterBean();
                in.setColumn(((SQLIdentifierExpr) col).getName());
                List<SQLExpr> list = x.getTargetList();
                Set<String> items = new HashSet<String>();
                for (SQLExpr sqlExpr : list) {
                    if (sqlExpr instanceof SQLNumericLiteralExpr) {
                        items.add(((SQLNumericLiteralExpr) sqlExpr).getNumber().toString());
                    } else if (sqlExpr instanceof SQLTextLiteralExpr) {
                        items.add(((SQLTextLiteralExpr) sqlExpr).getText());
                    }
                }
                in.setFilterValue(items);
            }
            return false;
        }

        @Override
        public FilterInfoBean getFilterInfoBean() {
            return in;
        }
    }
}
