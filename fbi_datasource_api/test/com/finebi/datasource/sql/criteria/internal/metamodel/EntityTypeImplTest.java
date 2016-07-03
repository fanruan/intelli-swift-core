package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.sql.criteria.AttributeType;
import com.finebi.datasource.sql.criteria.AttributeTypeImpl;
import junit.framework.TestCase;

/**
 * This class created on 2016/7/2.
 *
 * @author Connery
 * @since 4.0
 */
public class EntityTypeImplTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/2
     */
    public void testBuildEntity() {
        try {
            AttributeFactory factory = new AttributeFactory(null);
            AttributeImplementor implementor = factory.buildAttribute(null, new AttributePropertyImpl("id", false, new AttributeTypeImpl(AttributeType.InnerType.Integer)));
            EntityTypeImpl entityType = new EntityTypeImpl(TestCase.class, null, new PerisitentClassImpl());
            entityType.getBuilder().addAttribute(implementor);
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
