package com.fr.bi.tool;

import com.fr.bi.stable.utils.program.BIClassUtils;
import junit.framework.TestCase;

/**
 * Created by Connery on 2016/1/26.
 */
public class BIModuleFolderTest extends TestCase {
    public void testGenerateFile() {
        BIModuleFolder folder = new BIModuleFolder("fbi_base", "D:\\FineBI\\Git\\workHouse\\project\\fbi_base", BIClassUtils.getClasses("com.fr.bi"));
        folder.generateFactory();
    }
}