package com.finebi.cube.gen.arrange;

import com.finebi.cube.conf.CubePreConditionsCheck;
import com.finebi.cube.impl.conf.CubePreConditionsCheckManager;
import junit.framework.TestCase;

import java.io.File;

public class CubePreConditionsCheckTest extends TestCase {
    protected CubePreConditionsCheck check;

    @Override
    protected void setUp() throws Exception {
        check = new CubePreConditionsCheckManager();
        super.setUp();
    }

    public void testSpace() {
        String os = System.getProperty("os.name");
        File file = null;
        if (os.toUpperCase().contains("WINDOWS")) {
            file = new File("c:\\Windows");
        } else {
            file = new File("/home");
        }
        boolean spaceCheck = check.HDSpaceCheck(file);
        assertTrue(spaceCheck);
    }

    public void testConnection() {

    }

}
