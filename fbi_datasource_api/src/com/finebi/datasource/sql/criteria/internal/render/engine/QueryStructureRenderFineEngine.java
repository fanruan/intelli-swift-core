package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.api.metamodel.Attribute;
import com.finebi.datasource.sql.criteria.internal.QueryStructure;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;
import com.fr.engine.model.DataModel;
import com.fr.engine.model.calculate.CalculateDataModelManager;
import com.fr.fineengine.criterion.*;
import com.fr.fineengine.criterion.calculatetype.CalculateType;
import com.fr.fineengine.criterion.valuetype.ValueTypes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class QueryStructureRenderFineEngine implements RenderExtended<DataModel> {
    private DataModel dataModel;
    private QueryStructure queryStructure;
    private Connection connection;

    public QueryStructureRenderFineEngine(QueryStructure queryStructure) {
        this.queryStructure = queryStructure;
        initialConnection();
    }

    private void initialConnection() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/demo?user=root&password=123456";
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DataModel getRenderResult() {
        return dataModel;
    }

    @Override
    public DataModel render(RenderingContext renderingContext) {
        CriteriaBuilder builder = new CriteriaBuilder();
        Set<Root<?>> roots = queryStructure.getRoots();
        if (roots.size() == 1) {
            Root<?> root = roots.iterator().next();
            Entry entry = SQLEntryFactory.create(root.getModel().getName(), "Select * from " + root.getModel().getName(), connection);
            builder.setEntry(entry);
            AbstractPathImpl selection = (AbstractPathImpl) queryStructure.getSelection();
            List<Projection> projections = new ArrayList<Projection>();

            if (selection.getPathSource() != null) {
                Attribute<?, ?> attribute = selection.getAttribute();
                final Projection idA = Projections.field(attribute.getName(), covert(attribute.getJavaType()));
                projections.add(idA);
            }
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
            dataModel = CalculateDataModelManager.getInstance().execute(criteria, connection);
        }
        return null;
    }

    private ValueTypes covert(Class javaType) {
        if (Integer.class.isAssignableFrom(javaType)) {
            return ValueTypes.Integer;
        }

        if (String.class.isAssignableFrom(javaType)) {
            return ValueTypes.String;
        }
        return null;
    }

    @Override
    public DataModel renderProjection(RenderingContext renderingContext) {
        return null;
    }
}
