package com.finebi.common.resource;

import junit.framework.TestCase;

/**
 * This class created on 2017/4/12.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class ResourceNameTest extends TestCase {

    /**
     * Detail:
     * Author:Connery
     * Date:2017/4/12
     */
    public void testEquals(){
        try{
            ResourceNameImpl name1 = new ResourceNameImpl("A");
            ResourceNameImpl name2 = new ResourceNameImpl("A");
            ResourceNameImpl name3 = new ResourceNameImpl("B");
            assertTrue(name1.equals(name2));
            assertFalse(name1.equals(name3));
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
}
