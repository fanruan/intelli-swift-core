package com.fr.bi.tool;

import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Connery on 2016/1/26.
 */
public class BIFileScannerTest extends TestCase {
    public void testPattern() {
        String target_1 = "abc.java";
        String target_2 = "abc.javaabc";
        String target_3 = "abc.java.abc";
        String regex = ".*\\.java$";
        Pattern pattern = Pattern.compile(regex);
        assertTrue(pattern.matcher(target_1).find());
        assertFalse(pattern.matcher(target_2).find());
        assertFalse(pattern.matcher(target_3).find());


    }

    public void testFindJavaFile() {
        String root = "D:\\FineBI\\Git\\workHouse\\project\\fbi_base\\test\\com\\fr\\bi\\tool";
        BIFileScanner scanner = new BIFileScanner(root);
        List<String> paths = scanner.findAllJavaFile();

        BILogger.getLogger().info("");
    }
}