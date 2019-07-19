package com.fr.swift.jdbc.visitor;

import com.fr.swift.jdbc.antlr4.SwiftSqlParser;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.AggregationBean;
import com.fr.swift.query.info.bean.element.MetricBean;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;

/**
 * @author yee
 * @date 2019-07-19
 */
public class FunctionVisitor extends AbstractParseTreeVisitor<AggregationBean> {

    @Override
    public AggregationBean visitChildren(RuleNode node) {
        SwiftSqlParser.FuncExprContext funcExprContext = (SwiftSqlParser.FuncExprContext) node;
        int type = funcExprContext.funcName().start.getType();
        AggregationBean bean = null;
        switch (type) {
            case SwiftSqlParser.MAX:
                bean = MetricBean.builder(funcExprContext.simpleExpr(0).getText(), AggregatorType.MAX).build();
                break;
            case SwiftSqlParser.MIN:
                bean = MetricBean.builder(funcExprContext.simpleExpr(0).getText(), AggregatorType.MIN).build();
                break;
            case SwiftSqlParser.SUM:
                bean = MetricBean.builder(funcExprContext.simpleExpr(0).getText(), AggregatorType.SUM).build();
                break;
            case SwiftSqlParser.AVG:
                bean = MetricBean.builder(funcExprContext.simpleExpr(0).getText(), AggregatorType.AVERAGE).build();
                break;
            case SwiftSqlParser.TODATE:
                // TODO 2019/07/19 todate实现
            default:
                bean = MetricBean.builder(funcExprContext.simpleExpr(0).getText(), AggregatorType.COUNT).build();
                break;

        }
        bean.setAlias(funcExprContext.getText());
        return bean;
    }
}
