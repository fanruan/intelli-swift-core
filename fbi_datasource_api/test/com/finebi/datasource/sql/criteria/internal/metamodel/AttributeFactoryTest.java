package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.AttributeType;
import com.finebi.datasource.sql.criteria.AttributeTypeImpl;
import junit.framework.TestCase;

/**
 * This class created on 2016/7/2.
 *
 * @author Connery
 * @since 4.0
 */
public class AttributeFactoryTest extends TestCase {


    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/2
     */
    public void testBuildAttribute() {
        try {
            AttributeFactory factory = new AttributeFactory(null);
            AttributeImplementor implementor = factory.buildAttribute(null, new AttributePropertyImpl("id", false, new AttributeTypeImpl(AttributeType.InnerType.Integer)));
            assertEquals(implementor.getName(), "id");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
