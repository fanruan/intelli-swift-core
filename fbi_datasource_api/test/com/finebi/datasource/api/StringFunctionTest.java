package com.finebi.datasource.api;

import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.criteria.CriteriaQuery;
import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.EntityTypeManager;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContextImpl;
import com.finebi.datasource.sql.criteria.internal.metamodel.EntityTypeManagerImpl;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactoryDebug;
import junit.framework.TestCase;

/**
 * This class created on 2016/7/6.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class StringFunctionTest extends TestCase{
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

    public void testStringUpper() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.upper(root.get("A_name")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select upper(generatedAlias0.A_name) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testStringLower() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.lower(root.get("A_name")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select lower(generatedAlias0.A_name) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testStringConcat() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.concat(root.get("id"),root.get("A_name")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select generatedAlias0.id || generatedAlias0.A_name from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testStringSubString() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.substring(root.get("A_name"),1,2));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select substring(generatedAlias0.A_name,1,2) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testStringTrim() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.trim('a',root.get("A_name")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select trim(BOTH a from generatedAlias0.A_name) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testStringLength() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.length(root.get("A_name")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select length(generatedAlias0.A_name) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testStringCoalesce() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.coalesce(root.get("id"),root.get("A_name")));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select coalesce(generatedAlias0.id, generatedAlias0.A_name) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testStringLocate() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.locate(root.get("id"),"1"));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select locate(:1,generatedAlias0.id) from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testStringLiteral() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.literal(1D));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select 1.0D from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    public void testStringNullLiteral() {
        try {
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();

            Root root = query.from(getEntity());
            query.select(cb.nullLiteral("1".getClass()));

            String result = ((CriteriaQueryImpl) query).render(getContext()).toString();
            assertEquals("select null from jpa as generatedAlias0",result);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
