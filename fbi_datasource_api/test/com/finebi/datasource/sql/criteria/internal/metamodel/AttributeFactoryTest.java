package com.finebi.datasource.sql.criteria.internal.metamodel;

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
            AttributeImplementor implementor = factory.buildAttribute(null, new PropertyImpl("abc", false));
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
