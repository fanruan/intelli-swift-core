package com.finebi.common.name;
/**
 * This class created on 2017/4/27.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import junit.framework.TestCase;

public class NameImpTest extends TestCase {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(NameImpTest.class);
    /**
     * Detail:
     * Author:Connery
     * Date:2017/4/27
     */
    public void testParentNameEqual(){
        try{
            Name parent = new NameImp("A", new NameProvider() {
                @Override
                public Name getName() {
                    return new NameImp("P");
                }
            });
            Name self = new NameImp("P"+NameImp.SEPARATOR+"A");
            assertEquals(self,parent);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            fail();
        }
    }
}
