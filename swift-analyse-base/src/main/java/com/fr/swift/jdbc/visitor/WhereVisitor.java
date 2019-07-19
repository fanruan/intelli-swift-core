package com.fr.swift.jdbc.visitor;

import com.fr.swift.jdbc.antlr4.SwiftSqlParser.BoolExprContext;
import com.fr.swift.jdbc.antlr4.SwiftSqlParser.KeywordBoolExprContext;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-19
 */
public class WhereVisitor extends AbstractParseTreeVisitor<FilterInfoBean> {
    private BoolExprVisitor visitor = new BoolExprVisitor();

    @Override
    public FilterInfoBean visitChildren(RuleNode node) {
        if (node instanceof BoolExprContext || node instanceof KeywordBoolExprContext) {
            return node.accept(visitor);
        } else {
            List<FilterInfoBean> list = new ArrayList<>();
            for (int i = 0; i < node.getChildCount(); i++) {
                ParseTree child = node.getChild(i);
                FilterInfoBean accept = child.accept(this);
                if (null != accept) {
                    list.add(accept);
                }
            }
            if (list.size() == 1) {
                return list.get(0);
            }
            return new AndFilterBean(list);
        }
    }

    @Override
    public FilterInfoBean visitTerminal(TerminalNode node) {
        return super.visitTerminal(node);
    }
}
