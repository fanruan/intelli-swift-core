package com.fr.swift.jdbc.listener;

import com.fr.swift.jdbc.adaptor.InsertionBeanParser;
import com.fr.swift.jdbc.adaptor.bean.InsertionBean;
import com.fr.swift.jdbc.antlr4.SwiftSqlParseUtil;
import com.fr.swift.jdbc.antlr4.SwiftSqlParser;
import com.fr.swift.jdbc.antlr4.SwiftSqlParserBaseListener;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-19
 */
public class InsertListener extends SwiftSqlParserBaseListener implements InsertionBeanParser {
    private InsertionBean insertionBean;

    @Override
    public void enterInsert(SwiftSqlParser.InsertContext ctx) {
        List<Row> rows = new ArrayList<>();
        InsertVisitor visitor = new InsertVisitor();
        for (SwiftSqlParser.ValuesContext value : ctx.values()) {
            rows.add(value.accept(visitor));
        }
        String tableName = ctx.name().getText();
        List<String> fields = new ArrayList<>();
        if (ctx.columnNames != null) {
            for (SwiftSqlParser.NameContext nameContext : ctx.columnNames.name()) {
                fields.add(nameContext.getText());
            }
        }
        insertionBean = new InsertionBean();
        insertionBean.setFields(fields);
        insertionBean.setRows(rows);
        insertionBean.setTableName(tableName);
    }

    @Override
    public InsertionBean getInsertionBean() {
        return insertionBean;
    }

    private class InsertVisitor extends AbstractParseTreeVisitor<Row> {
        @Override
        public Row visitChildren(RuleNode node) {
            SwiftSqlParser.ValuesContext values = (SwiftSqlParser.ValuesContext) node;
            List<Object> data = new ArrayList<>();
            for (SwiftSqlParser.ValueContext valueContext : values.value()) {
                String text = valueContext.getText();
                if (valueContext.start.getType() == SwiftSqlParser.NUMERIC_LITERAL) {
                    data.add(Double.parseDouble(text));
                } else {
                    data.add(SwiftSqlParseUtil.trimQuote(text, "'"));
                }
            }
            return new ListBasedRow(data);
        }
    }
}
