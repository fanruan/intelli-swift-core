package com.fr.bi.tool;

import com.fr.base.ClassUtils;
import junit.framework.TestCase;

/**
 * Created by Connery on 2016/1/26.
 */
public class BIModuleFolderTest extends TestCase {
    public void testGenerateFile() {
        BIModuleFolder folder = new BIModuleFolder("fbi_base", "D:\\FineBI\\Git\\workHouse\\project\\fbi_base", ClassUtils.getClasses("com.fr.bi"));
        folder.generateFactory();
    }
}