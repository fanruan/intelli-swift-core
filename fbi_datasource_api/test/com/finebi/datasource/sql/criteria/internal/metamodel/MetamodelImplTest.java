package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.EntityType;
import junit.framework.TestCase;

/**
 * This class created on 2016/7/7.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class MetamodelImplTest extends TestCase {


    /**
     * Detail:
     * Author:Connery
     * Date:2016/7/7
     */
    public void testAddEntity() {
        try {
            MetamodelImpl metamodel = new MetamodelImpl();
            EntityType entityType = new EntityTypeImpl(TestCase.class, null, new PersistentClassImpl("a", "b"));
            metamodel.build().addEntityType(entityType);
            assertEquals(metamodel.entity("b"), entityType);

            EntityType entityType_2 = new EntityTypeImpl(TestCase.class, null, new PersistentClassImpl("e", "c"));
            metamodel.build().addEntityType(entityType_2);
            assertEquals(metamodel.entity("c"), entityType_2);
            assertEquals(metamodel.entity("b"), entityType);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail: 测试Metamodel的不可变
     * Author:Connery
     * Date:2016/7/7
     */
    public void testClose() {
        try {
            MetamodelImpl metamodel = new MetamodelImpl();
            EntityType entityType = new EntityTypeImpl(TestCase.class, null, new PersistentClassImpl("a", "b"));
            metamodel.build().addEntityType(entityType);
            assertEquals(metamodel.entity("b"), entityType);
            assertFalse(metamodel.isImmutable());
            metamodel.close();
            assertTrue(metamodel.isImmutable());
            try {
                EntityType entityType_2 = new EntityTypeImpl(TestCase.class, null, new PersistentClassImpl("e", "c"));
                metamodel.build().addEntityType(entityType_2);
            } catch (RuntimeException e) {
                return;
            }
            assertTrue(false);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

}
