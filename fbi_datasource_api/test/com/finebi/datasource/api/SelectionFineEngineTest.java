package com.finebi.datasource.api;

import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.criteria.CriteriaQuery;
import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.api.metamodel.EntityTypeManager;
import com.finebi.datasource.api.metamodel.PlainTable;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContextImpl;
import com.finebi.datasource.sql.criteria.internal.metamodel.EntityTypeManagerImpl;
import com.finebi.datasource.sql.criteria.internal.metamodel.PredicateFineEngineTest;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactoryEngineAdapter;
import com.fr.engine.model.DataModel;
import com.fr.engine.model.calculate.CalculateDataModelManager;
import com.fr.fineengine.criterion.Criteria;
import junit.framework.TestCase;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class SelectionFineEngineTest extends TestCase {

    private AspireContext context = new AspireContextImpl(new RenderFactoryEngineAdapter());
    private EntityTypeManager manager = new EntityTypeManagerImpl(context);

    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/4
     */
    public void testSingleSelection() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityTypeManager manager = new EntityTypeManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(PredicateFineEngineTest.getEntity());
            query.select(root.get("A_name"));
            Criteria criteria = (Criteria) ((CriteriaQueryImpl) query).renderData(getRenderContext());
            DataModel result = CalculateDataModelManager.getInstance().execute(criteria, PredicateFineEngineTest.getConnection());
            System.out.println(result.getRowSize());
            System.out.println(result.getValue(0, 0));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }


    public static RenderingContext getRenderContext() {
        return SelectionTest.getContext(new RenderFactoryEngineAdapter());
    }


}
