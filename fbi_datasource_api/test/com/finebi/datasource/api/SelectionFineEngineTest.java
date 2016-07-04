package com.finebi.datasource.api;

import com.finebi.datasource.api.criteria.CriteriaBuilder;
import com.finebi.datasource.api.criteria.CriteriaQuery;
import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.api.metamodel.AttributeType;
import com.finebi.datasource.api.metamodel.EntityManager;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.PlainTable;
import com.finebi.datasource.sql.criteria.AttributeTypeImpl;
import com.finebi.datasource.sql.criteria.internal.CriteriaQueryImpl;
import com.finebi.datasource.sql.criteria.internal.context.AspireContext;
import com.finebi.datasource.sql.criteria.internal.context.AspireContextImpl;
import com.finebi.datasource.sql.criteria.internal.metamodel.*;
import com.fr.engine.model.DataModel;
import junit.framework.TestCase;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class SelectionFineEngineTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/4
     */
    public void testSingleSelection() {
        try {
            AspireContext context = new AspireContextImpl();
            EntityManager manager = new EntityManagerImpl(context);
            CriteriaBuilder cb = manager.getCriteriaBuilder();
            CriteriaQuery<PlainTable> query = cb.createQuery();
            Root root = query.from(getEntity());
            query.select(root.get("idA"));
            DataModel result = ((CriteriaQueryImpl) query).renderData(SelectionTest.getContext());
            System.out.println(result.getRowSize());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
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
}
