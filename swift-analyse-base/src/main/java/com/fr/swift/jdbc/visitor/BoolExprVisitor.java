package com.fr.swift.jdbc.visitor;

import com.fr.swift.jdbc.antlr4.SwiftSqlParseUtil;
import com.fr.swift.jdbc.antlr4.SwiftSqlParser;
import com.fr.swift.jdbc.creator.FilterBeanCreator;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.OrFilterBean;
import com.fr.swift.util.Strings;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ErrorNodeImpl;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author yee
 * @date 2019-07-19
 */
public class BoolExprVisitor extends AbstractParseTreeVisitor<FilterInfoBean> {
    @Override
    public FilterInfoBean visitChildren(RuleNode node) {

        if (node instanceof SwiftSqlParser.BoolExprContext) {
            return visitBoolExprContext((SwiftSqlParser.BoolExprContext) node);
        } else if (node instanceof SwiftSqlParser.KeywordBoolExprContext) {
            return visitKeywordBoolExprContext((SwiftSqlParser.KeywordBoolExprContext) node);
        } else {
            return null;
        }
    }

    private FilterInfoBean visitKeywordBoolExprContext(SwiftSqlParser.KeywordBoolExprContext boolExpr) {
        String columnName = null;
        Queue<FilterBeanCreator<FilterInfoBean>> queue = new LinkedBlockingQueue<>();
        Queue values = new LinkedBlockingQueue<>();
        for (ParseTree child : boolExpr.children) {
            if (child instanceof SwiftSqlParser.SimpleExprContext) {
                columnName = child.getText();
            } else if (child instanceof TerminalNode) {
                int type = ((TerminalNode) child).getSymbol().getType();
                if (type == SwiftSqlParser.NUMERIC_LITERAL || type == SwiftSqlParser.STRING_LITERAL) {
                    values.offer(child.getText());
                } else {
                    FilterBeanCreator<FilterInfoBean> accept = child.accept(new com.fr.swift.jdbc.visitor.BoolOpVisitor());
                    if (null != accept) {
                        queue.offer(accept);
                    }
                }
            } else if (child instanceof SwiftSqlParser.ValuesContext || child instanceof SwiftSqlParser.ValueContext) {
                values.offer(child.accept(new ValueExprVisitor()));
            } else {
                // do nothing
            }
        }
        FilterInfoBean result = null;
        while (queue.peek() != null) {
            FilterBeanCreator<FilterInfoBean> poll = queue.poll();
            // between and
            if (poll.type() == SwiftSqlParser.BETWEEN) {
                List filterValue = new ArrayList();
                filterValue.add(values.poll());
                filterValue.add(values.poll());
                return poll.create(columnName, filterValue);
            }
            // is not null   is null  in values
            if (null == result) {
                result = poll.create(columnName, values.peek() != null ? values.poll() : null);
            } else {
                result = poll.create(columnName, result);
            }
        }
        return result;
    }

    private FilterInfoBean visitBoolExprContext(SwiftSqlParser.BoolExprContext boolExpr) {
        SwiftSqlParser.KeywordBoolExprContext keywordBoolExprContext = boolExpr.keywordBoolExpr();
        if (null != keywordBoolExprContext) {
            return keywordBoolExprContext.accept(this);
        }
        List<SwiftSqlParser.BoolExprContext> boolExprs = boolExpr.boolExpr();
        if (boolExprs.isEmpty()) {
            String column = null;
            Object value = null;
            FilterBeanCreator<FilterInfoBean> creator = null;
            for (int i = 0; i < boolExpr.getChildCount(); i++) {
                ParseTree child = boolExpr.getChild(i);
                if (child instanceof SwiftSqlParser.SimpleExprContext) {
                    if (Strings.isEmpty(column)) {
                        column = child.getText();
                    } else {
                        value = SwiftSqlParseUtil.trimQuote(child.getText(), "'");
                    }
                } else if (child instanceof SwiftSqlParser.BoolOpContext) {
                    creator = child.accept(new com.fr.swift.jdbc.visitor.BoolOpVisitor());
                } else if (child instanceof SwiftSqlParser.ValuesContext || child instanceof SwiftSqlParser.ValueContext) {
                    value = child.accept(new ValueExprVisitor());
                } else {
                    // do nothing
                }
            }
            if (null == creator) {
                return visitErrorNode(new ErrorNodeImpl(boolExpr.getStart()));
            }
            return creator.create(column, value);
        } else {
            List<FilterInfoBean> list = new ArrayList<>();
            for (SwiftSqlParser.BoolExprContext boolExp : boolExprs) {
                list.add(boolExp.accept(this));
            }
            return boolExpr.logicOp().OR() != null ? new OrFilterBean(list) : new AndFilterBean(list);
        }
    }

    @Override
    public FilterInfoBean visitErrorNode(ErrorNode node) {
        throw new RuntimeException(node.getText());
    }

    /**
     * @author yee
     * @date 2019-07-19
     */
    public static class ValueExprVisitor extends AbstractParseTreeVisitor<Set<String>> {
        @Override
        public Set<String> visitChildren(RuleNode node) {
            Set<String> set = new HashSet<>();
            if (node instanceof SwiftSqlParser.ValuesContext) {
                for (SwiftSqlParser.ValueContext valueContext : ((SwiftSqlParser.ValuesContext) node).value()) {
                    set.addAll(valueContext.accept(this));
                }
            } else if (node instanceof SwiftSqlParser.ValueContext) {
                if (((SwiftSqlParser.ValueContext) node).getStart().getType() == SwiftSqlParser.NUMERIC_LITERAL) {
                    return Collections.singleton(((SwiftSqlParser.ValueContext) node).NUMERIC_LITERAL().getText());
                }
                TerminalNode terminalNode = ((SwiftSqlParser.ValueContext) node).STRING_LITERAL();
                String value = SwiftSqlParseUtil.trimQuote(terminalNode.getText(), "'");
                return Collections.singleton(value);
            }
            return set;
        }
    }
}
