package com.finebi.datasource.sql.criteria;

import com.finebi.datasource.api.criteria.Selection;
import com.finebi.datasource.api.metamodel.PlainColumn;
import com.finebi.datasource.api.metamodel.PlainTable;
import com.fr.engine.model.DataModel;
import com.fr.engine.model.calculate.CalculateDataModelManager;
import com.fr.fineengine.criterion.SQLEntryFactory;
import com.fr.fineengine.criterion.*;
import com.fr.fineengine.criterion.calculatetype.CalculateType;
import com.fr.fineengine.criterion.valuetype.ValueTypes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2016/6/29.
 *
 * @author Connery
 * @since 4.0
 */
public class SQLAdapter {
    public DataModel listResult(CriteriaQueryImpl query) {
        Selection selection = query.getSelection();
        PlainTable plainTable = selection.getPlainTable();
        Connection connection = getConnection();
        List<PlainColumn> columns = plainTable.getColumns();
        CriteriaBuilder builder = new CriteriaBuilder();
        Entry entry = SQLEntryFactory.create(plainTable.getTableName(), "Select * from " + plainTable.getTableName(), connection);
        builder.setEntry(entry);
        for (PlainColumn column : columns) {
            final Projection projection = Projections.field(column.getColumnName(), convert(column.getJavaType()));
            builder.addProjection(projection);
        }
        builder.setSummaryInfo(new SummaryInfo() {
            @Override
            public int getSummarySize() {
                return 0;
            }

            @Override
            public CalculateType[] getCalculateTypes() {
                return new CalculateType[0];
            }
        });
        Criteria criteria = builder.build();
        return CalculateDataModelManager.getInstance().execute(criteria, connection);
    }

    private ValueTypes convert(JavaPrimitiveType primitiveType) {
        switch (primitiveType) {
            case Integer:
                return ValueTypes.Integer;
            case String:
                return ValueTypes.String;
            default:
                return ValueTypes.String;
        }
    }

    private Connection getConnection() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/demo?user=root&password=123456";
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url);
            return connection;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
