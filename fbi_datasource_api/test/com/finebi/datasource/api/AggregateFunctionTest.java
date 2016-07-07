package com.finebi.datasource.api;

import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.criteria.CriteriaQuery;
import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.api.metamodel.EntityTypeManager;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.PlainTable;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContextImpl;
import com.finebi.datasource.sql.criteria.internal.metamodel.EntityTypeManagerImpl;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactoryDebug;
import junit.framework.TestCase;

/**
 * Created by Osborn on 2016/7/6.
 */
public class AggregateFunctionTest extends TestCase {

    /**
     * global variables
     */
    private AspireContext context = new AspireContextImpl(new RenderFactoryDebug());
    private EntityTypeManager manager = new EntityTypeManagerImpl(context);

    /**
     * static methods
     * @return
     */
    public static EntityType getEntity() {
        return SelectionTest.getEntity();
    }
    public static RenderingContext getContext(){
        return WherePredicateTest.getContext();
    }


    public void testFunctionCount() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.count(root));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select count(*) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testFunctionAVG(){
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.avg(root.get("id")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select avg(generatedAlias0.id) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFunctionSUM() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.sum(root.get("id")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select sum(generatedAlias0.id) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testFunctionMAX() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.max(root.get("id")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select max(generatedAlias0.id) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFunctionMIN() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.min(root.get("id")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select min(generatedAlias0.id) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFunctionGreatest() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.greatest(root.get("A_name")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select max(generatedAlias0.A_name) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFunctionLeast() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.least(root.get("A_name")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select min(generatedAlias0.A_name) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testFunctionSqrt() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(cb.sqrt(root.get("id")));
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select sqrt(generatedAlias0.id) from jpa as generatedAlias0", result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testFunctionAbs() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(cb.abs(root.get("id")));
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select abs(generatedAlias0.id) from jpa as generatedAlias0", result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * toString 没有被render
     */

    public void testFunctionToString() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(cb.toString(root.get("id")));
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
//            assertEquals("select to_char(generatedAlias0.id) from jpa as generatedAlias0", result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
