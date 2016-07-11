package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.sql.criteria.internal.QueryStructure;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
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
public class QueryStructureRenderFineEngine extends BasicEngineRender<QueryStructure, Criteria> {
    private Criteria criteria;
    private Connection connection;

    public QueryStructureRenderFineEngine(QueryStructure queryStructure) {
        super(queryStructure);
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

    public Criteria getRenderResult() {
        return criteria;
    }

    @Override
    public Criteria render(RenderingContext renderingContext) {
        CriteriaBuilder builder = new CriteriaBuilder();
        Set<Root<?>> roots = getDelegate().getRoots();
        if (roots.size() == 1) {
            builder.setEntry(renderFrom(renderingContext));
            builder.setProjections(renderSelection(renderingContext));
            List<Condition> conditions = new ArrayList<Condition>();
            conditions.add(renderWhere(renderingContext));
            builder.setConditions(conditions);
            builder.setSummaryInfo(renderSummaryInfo(renderingContext));
            return builder.build();
        }
        throw new UnsupportedOperationException();
    }

    private Condition renderWhere(RenderingContext renderingContext) {
        return (Condition) ((Renderable) getDelegate().getRestriction()).render(renderingContext);
    }

    private SummaryInfo renderSummaryInfo(RenderingContext renderingContext) {
        return new SummaryInfo() {
            @Override
            public int getSummarySize() {
                return 0;
            }

            @Override
            public CalculateType[] getCalculateTypes() {
                return new CalculateType[0];
            }
        };
    }

    private Entry renderFrom(RenderingContext renderingContext) {
        Set<Root<?>> roots = getDelegate().getRoots();
        if (roots.size() == 1) {
            Root<?> root = roots.iterator().next();
            return SQLEntryFactory.create(root.getModel().getName(), "Select * from " + root.getModel().getName(), connection);
        } else {
            throw new RuntimeException();
        }
    }

    private List<Projection> renderSelection(RenderingContext renderingContext) {
        List<Projection> projections = new ArrayList<Projection>();
        AbstractPathImpl selection = (AbstractPathImpl) getDelegate().getSelection();

        final Projection projection = (Projection) selection.render(renderingContext);
        projections.add(projection);

        return projections;
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
    public Criteria renderProjection(RenderingContext renderingContext) {
        return criteria;
    }
}
