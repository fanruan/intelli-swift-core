package com.finebi.cube.location;

import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public class BIURILocationTest extends TestCase {
    public void testSlash() {
        try {
            BICubeLocation location = new BICubeLocation("/test", "child");
            assertEquals("/test/child", location.getAbsolutePath());
        } catch (Exception ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
        }
    }

    public void testChildSlash() {
        try {
            BICubeLocation location = new BICubeLocation("/test", "////child");
            assertEquals("/test/child", location.getAbsolutePath());
        } catch (Exception ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
        }
    }
    public void testNoStartSlash() {
        try {
            BICubeLocation location = new BICubeLocation("test/test2", "////child");
            assertEquals("/test/test2/child", location.getAbsolutePath());
        } catch (Exception ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
        }
    }
}
