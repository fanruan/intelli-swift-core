package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.SelectionTest;
import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.criteria.CriteriaQuery;
import com.finebi.datasource.api.criteria.Predicate;
import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.api.metamodel.AttributeType;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.EntityTypeManager;
import com.finebi.datasource.api.metamodel.PlainTable;
import com.finebi.datasource.sql.criteria.AttributeTypeImpl;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContextImpl;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactoryEngineAdapter;
import com.fr.engine.model.DataModel;
import com.fr.engine.model.calculate.CalculateDataModelManager;
import com.fr.fineengine.criterion.Criteria;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class PredicateFineEngineTest extends TestCase {

    private AspireContext context = new AspireContextImpl(new RenderFactoryEngineAdapter());
    private EntityTypeManager manager = new EntityTypeManagerImpl(context);

    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/4
     */
    public void testEqPredicate() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityTypeManager manager = new EntityTypeManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root.get("idA"));
            Predicate condition = cb.equal(root.get("idA"), 2);
            query.where(condition);
            Criteria criteria = (Criteria) ((CriteriaQueryImpl) query).renderData(getRenderContext());
            DataModel result = CalculateDataModelManager.getInstance().execute(criteria, getConnection());


            assertEquals(result.getRowSize(), 1);
            assertEquals(result.getValue(0, 0), Long.valueOf(2));
            assertEquals(result.getColumnSize(), 1);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/4
     */
    public void testBetweenPredicate() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityTypeManager manager = new EntityTypeManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root.get("idA"));
            Predicate condition = cb.between(root.get("idA"), 2, 3);
            query.where(condition);

            Criteria criteria = (Criteria) ((CriteriaQueryImpl) query).renderData(getRenderContext());

            DataModel result = CalculateDataModelManager.getInstance().execute(criteria, getConnection());
            DataModel expectResult = PredicateFineEngineDemo.getBetween(2, 3);
            checkBasic(result, expectResult);
            assertEquals(result.getValue(0, 0), expectResult.getValue(0, 0));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/4
     */
    public void testInPredicate() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityTypeManager manager = new EntityTypeManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root.get("idA"));
            Predicate condition = cb.in(root.get("idA")).value(2).value(3);
            query.where(condition);

            Criteria criteria = (Criteria) ((CriteriaQueryImpl) query).renderData(getRenderContext());

            DataModel result = CalculateDataModelManager.getInstance().execute(criteria, getConnection());
            DataModel expectResult = PredicateFineEngineDemo.getIn(2, 3);
            checkBasic(result, expectResult);
            assertEquals(result.getValue(0, 0), expectResult.getValue(0, 0));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private void checkBasic(DataModel expectResult, DataModel result) {
        assertEquals(expectResult.getRowSize(), result.getRowSize());
        assertEquals(expectResult.getColumnSize(), result.getColumnSize());

    }

    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/demo?user=root&password=123456";
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    public static RenderingContext getRenderContext() {
        return SelectionTest.getContext(new RenderFactoryEngineAdapter());
    }

    public static EntityType getEntity() {
        AttributeFactory factory = new AttributeFactory(null);
        AttributeImplementor implementor = factory.buildAttribute(null, new AttributePropertyImpl("idA", false, new AttributeTypeImpl(AttributeType.InnerType.Integer)));
        AttributeImplementor implementor_name = factory.buildAttribute(null, new AttributePropertyImpl("A_name", false, new AttributeTypeImpl(AttributeType.InnerType.String)));
        EntityTypeImpl entityType = new EntityTypeImpl(TestCase.class, null, new PersistentClassImpl("whatever", "a"));
        entityType.getBuilder().addAttribute(implementor);
        entityType.getBuilder().addAttribute(implementor_name);
        return entityType;
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/4
     */
    public void testLikePredicate() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityTypeManager manager = new EntityTypeManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root.get("A_name"));
            Predicate condition = cb.like(root.get("A_name"), "_ack");
            query.where(condition);

            Criteria criteria = (Criteria) ((CriteriaQueryImpl) query).renderData(getRenderContext());

            DataModel result = CalculateDataModelManager.getInstance().execute(criteria, getConnection());
            DataModel expectResult = PredicateFineEngineDemo.getBetween(2, 3);
            checkBasic(result, expectResult);
            assertEquals(result.getValue(0, 0), expectResult.getValue(0, 0));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
