package com.finebi.datasource.api;

import com.fr.engine.model.DataModel;
import com.fr.engine.model.calculate.CalculateDataModelManager;
import com.fr.fineengine.criterion.*;
import com.fr.fineengine.criterion.calculatetype.CalculateType;
import com.fr.fineengine.criterion.valuetype.ValueTypes;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/6/29.
 *
 * @author Connery
 * @since 4.0
 */
public class SQLAdapterDemo extends TestCase {

    public void testSQLAdapter() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/osborn_db?user=root&password=";
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url);

            assertFalse(connection.isClosed());
            CriteriaBuilder builder = new CriteriaBuilder();
            Entry entry = SQLEntryFactory.create("a", "Select * from a", connection);
            builder.setEntry(entry);
            final Projection idA = Projections.field("idA", ValueTypes.Integer);
            final Projection name = Projections.field("A_name", ValueTypes.String);
            List<Projection> projections = new ArrayList<Projection>();
            projections.add(idA);
            projections.add(name);
            builder.setProjections(projections);
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
            DataModel dataModel = CalculateDataModelManager.getInstance().execute(criteria, connection);
            System.out.println("");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
