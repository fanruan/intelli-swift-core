package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.fr.engine.model.DataModel;
import com.fr.engine.model.calculate.CalculateDataModelManager;
import com.fr.fineengine.criterion.*;
import com.fr.fineengine.criterion.calculatetype.CalculateType;
import com.fr.fineengine.criterion.valuetype.ValueTypes;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/7/11.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class PredicateFineEngineDemo extends TestCase {

    public static DataModel getBetween(Object value1, Object value2) {
        try {

            CriteriaBuilder builder = new CriteriaBuilder();
            Entry entry = SQLEntryFactory.create("a", "Select * from a", PredicateFineEngineTest.getConnection());
            builder.setEntry(entry);
            final Projection idA = Projections.field("idA", ValueTypes.Integer);
            List<Projection> projections = new ArrayList<Projection>();
            projections.add(idA);
            builder.setProjections(projections);
            builder.addCondition(Restrictions.between(idA, value1, value2));
            builder.setSummaryInfo(getSummaryInfo());

            Criteria criteria = builder.build();
            DataModel dataModel = CalculateDataModelManager.getInstance().execute(criteria, PredicateFineEngineTest.getConnection());
            return dataModel;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static DataModel getIn(Object... value) {
        try {

            CriteriaBuilder builder = new CriteriaBuilder();
            Entry entry = SQLEntryFactory.create("a", "Select * from a", PredicateFineEngineTest.getConnection());
            builder.setEntry(entry);
            final Projection idA = Projections.field("idA", ValueTypes.Integer);
            List<Projection> projections = new ArrayList<Projection>();
            projections.add(idA);
            builder.setProjections(projections);
            builder.addCondition(Restrictions.in(idA, value));
            builder.setSummaryInfo(getSummaryInfo());

            Criteria criteria = builder.build();
            DataModel dataModel = CalculateDataModelManager.getInstance().execute(criteria, PredicateFineEngineTest.getConnection());
            return dataModel;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static SummaryInfo getSummaryInfo() {
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

    public static DataModel getLike(String pattern) {
        try {

            CriteriaBuilder builder = new CriteriaBuilder();
            Entry entry = SQLEntryFactory.create("a", "Select * from a", PredicateFineEngineTest.getConnection());
            builder.setEntry(entry);
            final Projection idA = Projections.field("idA", ValueTypes.Integer);
            List<Projection> projections = new ArrayList<Projection>();
            projections.add(idA);
            builder.setProjections(projections);
            builder.addCondition(Restrictions.like(idA, pattern, MatchMode.ANYWHERE));
            builder.setSummaryInfo(getSummaryInfo());

            Criteria criteria = builder.build();
            DataModel dataModel = CalculateDataModelManager.getInstance().execute(criteria, PredicateFineEngineTest.getConnection());
            return dataModel;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public void test4Run() {
        DataModel dataModel = getLike("Jack");
        System.out.println(dataModel.getColumnSize());
    }


}
