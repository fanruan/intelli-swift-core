package com.fr.swift.jdbc.listener;

import com.fr.swift.jdbc.adaptor.SelectionBeanParser;
import com.fr.swift.jdbc.adaptor.bean.SelectionBean;
import com.fr.swift.jdbc.antlr4.SwiftSqlParser;
import com.fr.swift.jdbc.antlr4.SwiftSqlParserBaseListener;
import com.fr.swift.jdbc.visitor.FunctionVisitor;
import com.fr.swift.jdbc.visitor.OrderByVisitor;
import com.fr.swift.jdbc.visitor.WhereVisitor;
import com.fr.swift.query.info.bean.element.AggregationBean;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.util.Strings;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-19
 */
public class SelectListener extends SwiftSqlParserBaseListener implements SelectionBeanParser {
    private QueryInfoBean bean;

    @Override
    public void enterSelect(SwiftSqlParser.SelectContext ctx) {
        if (ctx.subQuery != null) {
            enterSelect(ctx.subQuery);
            // TODO 2019/07/19 子查询解析
        } else {
            SwiftSqlParser.NameContext table = ctx.table;
            String tableName = table.getText();
            SwiftSqlParser.NamesContext groupBy = ctx.groupBy;
            FilterInfoBean filter = null;
            if (ctx.where != null) {
                filter = ctx.where.accept(new WhereVisitor());
            }

            if (null != groupBy) {
                List<DimensionBean> dimensionBeans = new ArrayList<>();
                for (SwiftSqlParser.NameContext nameContext : groupBy.name()) {
                    dimensionBeans.add(new DimensionBean(DimensionType.GROUP, nameContext.getText()));
                }
                ArrayList<AggregationBean> metricBeans = new ArrayList<>();
                visitColumns(ctx.columns(), dimensionBeans, metricBeans);
                bean = GroupQueryInfoBean.builder(tableName)
                        .setDimensions(dimensionBeans)
                        .setAggregations(metricBeans)
                        .setFilter(filter).build();
            } else {
                SwiftSqlParser.ColumnsContext columns = ctx.columns();
                List<DimensionBean> dimensionBeans = new ArrayList<>();
                List<AggregationBean> metricBeans = new ArrayList<>();
                visitColumns(columns, dimensionBeans, metricBeans);

                if (metricBeans.isEmpty()) {
                    if (dimensionBeans.size() > 1 || dimensionBeans.get(0).getType() != DimensionType.DETAIL_ALL_COLUMN) {
                        for (DimensionBean dimensionBean : dimensionBeans) {
                            dimensionBean.setType(DimensionType.DETAIL);
                        }
                    }
                    bean = DetailQueryInfoBean.builder(tableName).setDimensions(dimensionBeans).setFilter(filter).build();
                } else {
                    bean = GroupQueryInfoBean.builder(tableName).setDimensions(dimensionBeans).setAggregations(metricBeans).setFilter(filter).build();
                }
            }
        }

    }

    private void visitColumns(SwiftSqlParser.ColumnsContext columns, List<DimensionBean> dimensionBeans, List<AggregationBean> metricBeans) {
        int childCount = columns.getChildCount();
        boolean dimension = true;
        DimensionBean aliasDimension = null;
        AggregationBean aliasAggregation = null;
        for (int i = 0; i < childCount; i++) {
            ParseTree child = columns.getChild(i);
            if (child instanceof TerminalNode) {
                if (((TerminalNode) child).getSymbol().getType() == SwiftSqlParser.MUL) {
                    dimensionBeans.add(new DimensionBean(DimensionType.DETAIL_ALL_COLUMN));
                    break;
                }
            }
            if (child instanceof SwiftSqlParser.SimpleExprContext) {
                SwiftSqlParser.FuncExprContext funcExprContext = ((SwiftSqlParser.SimpleExprContext) child).funcExpr();
                if (funcExprContext != null) {
                    metricBeans.add(funcExprContext.accept(new FunctionVisitor()));
                    dimension = false;
                } else {
                    int type = ((SwiftSqlParser.SimpleExprContext) child).start.getType();
                    switch (type) {
                        case SwiftSqlParser.NUMERIC_LITERAL:
                        case SwiftSqlParser.STRING_LITERAL:
                            // TODO 2019/07/19 直接传简单值暂时不支持
                            break;
                        default:
                            dimensionBeans.add(new DimensionBean(DimensionType.GROUP, child.getText()));
                    }
                    dimension = true;
                }
            } else if (child instanceof TerminalNode) {
                if (((TerminalNode) child).getSymbol().getType() == SwiftSqlParser.AS) {
                    if (dimension) {
                        aliasDimension = dimensionBeans.get(dimensionBeans.size() - 1);
                    } else {
                        aliasAggregation = metricBeans.get(metricBeans.size() - 1);
                    }
                }
            } else if (child instanceof SwiftSqlParser.NameContext) {
                if (null != aliasAggregation) {
                    aliasAggregation.setAlias(child.getText());
                    aliasAggregation = null;
                }
                if (null != aliasDimension) {
                    aliasDimension.setAlias(child.getText());
                    aliasDimension = null;
                }
            }
        }
    }

    @Override
    public void enterOrderBy(SwiftSqlParser.OrderByContext ctx) {
        bean.setSorts(ctx.accept(new OrderByVisitor()));
    }

    @Override
    public SelectionBean getSelectionBean() {
        return new SelectionBean(Strings.EMPTY, bean.getTableName(), bean);
    }
}
