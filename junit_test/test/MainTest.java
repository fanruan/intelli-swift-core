package test;


import code.Main;
import junit.framework.TestCase;

/**
 * Created by wuk on 16/6/23.
 */



public class MainTest extends TestCase {
    public void testMain(){
        Main m = new Main("a");
        assertEquals("a",m.info());
        assertTrue(false);
    }
}  

