package com.finebi.datasource.api;

import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.criteria.CriteriaQuery;
import com.finebi.datasource.api.criteria.Predicate;
import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.api.metamodel.EntityManager;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContextImpl;
import com.finebi.datasource.sql.criteria.internal.metamodel.EntityManagerImpl;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactoryDebug;
import junit.framework.TestCase;

/**
 * Created by Osborn on 2016/7/6.
 */
public class GroupAndOrderByTest extends TestCase {
    /**
     * global variables
     */
    private AspireContext context = new AspireContextImpl(new RenderFactoryDebug());
    private EntityManager manager = new EntityManagerImpl(context);

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
    public void testGroupBy() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            query.groupBy(root.get("A_name"));
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 group by generatedAlias0.A_name", result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testHaving() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            query.groupBy(root.get("A_name"));
            Predicate condition = cb.like(root.get("A_name"), "_a%");
            query.having(condition);
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0 from jpa as generatedAlias0 group by generatedAlias0.A_name having generatedAlias0.A_name like :_a%", result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Author:Osborn
     * Date:2016/7/5
     */
    public void testOrderBy() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root);
            query.orderBy(cb.asc(root.get("id")));
            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

}
