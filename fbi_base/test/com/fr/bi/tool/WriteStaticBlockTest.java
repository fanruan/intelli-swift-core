package com.fr.bi.tool;

import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * Created by Connery on 2016/1/23.
 */
public class WriteStaticBlockTest extends TestCase {
    public void testBasicGenerate() {
        try {
            WriterStaticCodeBlock4Test codeBlock4Test = new WriterStaticCodeBlock4Test("", new FileOperation4Test(), "");
            String result = codeBlock4Test.generateCode();
            assertEquals("{\r\n\tstatic{}}", result);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testFactoryGenerate() {
//        try {
//            BIFactoryAutoRegisterCode codeBlock4Test = new BIFactoryAutoRegisterCode("D:\\FineBI\\Git\\workHouse\\project\\fbi_base\\" +
//                    "test\\com\\fr\\bi\\tool\\BIConfFactory.java", new FileOperation(), "D:\\FineBI\\Git\\workHouse\\project\\fbi_base\\" +
//                    "test\\com\\fr\\bi\\tool\\BIConfFactory.java");
//            String result = codeBlock4Test.generateCode();
//            codeBlock4Test.writeFile();
//            BILogger.getLogger().info(result);
//        } catch (Exception e) {
//            BILogger.getLogger().error(e.getMessage(), e);
//        }
    }
}