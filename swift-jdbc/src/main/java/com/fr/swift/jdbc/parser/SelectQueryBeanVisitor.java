package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.expression.Expression;
import com.fr.general.jsqlparser.schema.Table;
import com.fr.general.jsqlparser.statement.select.FromItem;
import com.fr.general.jsqlparser.statement.select.FromItemVisitor;
import com.fr.general.jsqlparser.statement.select.LateralSubSelect;
import com.fr.general.jsqlparser.statement.select.PlainSelect;
import com.fr.general.jsqlparser.statement.select.SelectItem;
import com.fr.general.jsqlparser.statement.select.SelectVisitor;
import com.fr.general.jsqlparser.statement.select.SetOperationList;
import com.fr.general.jsqlparser.statement.select.SubJoin;
import com.fr.general.jsqlparser.statement.select.SubSelect;
import com.fr.general.jsqlparser.statement.select.TableFunction;
import com.fr.general.jsqlparser.statement.select.ValuesList;
import com.fr.general.jsqlparser.statement.select.WithItem;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.query.info.bean.query.AbstractSingleTableQueryInfoBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.util.Crasher;

import java.util.List;
import java.util.UUID;

/**
 * Created by pony on 2018/8/17.
 */
public class SelectQueryBeanVisitor implements SelectVisitor,FromItemVisitor,QueryBeanParser {
    private AbstractSingleTableQueryInfoBean queryBean;
    @Override
    public void visit(PlainSelect plainSelect) {
        FromItem item = plainSelect.getFromItem();
        List<Expression> groupbyColumns = plainSelect.getGroupByColumnReferences();
        DimensionMetricVisitor subVisitor;
        if (groupbyColumns == null || groupbyColumns.isEmpty()){
            queryBean = new DetailQueryInfoBean();
            subVisitor = new DetailQueryBeanVisitor((DetailQueryInfoBean) queryBean);
        } else {
            queryBean = new GroupQueryInfoBean();
            subVisitor = new GroupQueryBeanVisitor((GroupQueryInfoBean) queryBean);
        }
        queryBean.setQueryId(UUID.randomUUID().toString());
        item.accept(this);
        if (groupbyColumns != null){
            GroupByDimensionVisitor visitor = new GroupByDimensionVisitor((GroupQueryInfoBean) queryBean);
            for (Expression expression : groupbyColumns){
                expression.accept(visitor);
            }
        }
        for (SelectItem selectItem : plainSelect.getSelectItems()){
            selectItem.accept(subVisitor);
        }
        if (plainSelect.getWhere() != null){
            plainSelect.getWhere().accept(subVisitor);
        }
    }

    @Override
    public void visit(SetOperationList setOperationList) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(WithItem withItem) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public QueryBean getQueryBean() {
        return queryBean;
    }

    @Override
    public void visit(Table table) {
        queryBean.setTableName(table.getName());
    }

    @Override
    public void visit(SubSelect subSelect) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(SubJoin subJoin) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(ValuesList valuesList) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(TableFunction tableFunction) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }
}
