package com.finebi.datasource.api;

import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.criteria.CriteriaQuery;
import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.api.metamodel.EntityTypeManager;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContextImpl;
import com.finebi.datasource.sql.criteria.internal.metamodel.EntityTypeManagerImpl;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactoryDebug;
import junit.framework.TestCase;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class SQLWhereTest extends TestCase {


    private AspireContext context = new AspireContextImpl(new RenderFactoryDebug());
    private EntityTypeManager manager = new EntityTypeManagerImpl(context);

    /**
     * Author:Osborn
     * Date:2016/7/5
     */
    public void testOrderBy() {
        try {

            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root root = query.from(WherePredicateTest.getEntity());
            query.select(root);
            query.where(cb.isTrue(cb.equal(root.get("id"), 1)));
            String result = ((CriteriaQueryImpl) query).render(WherePredicateTest.getContext()).toString();
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
