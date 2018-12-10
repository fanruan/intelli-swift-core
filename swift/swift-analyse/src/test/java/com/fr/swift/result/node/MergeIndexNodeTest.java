package com.fr.swift.result.node;

import junit.framework.TestCase;
import org.junit.Ignore;

/**
 * Created by pony on 2017/12/8.
 */
@Ignore
public class MergeIndexNodeTest extends TestCase {

//    MergeIndexNode node;
//
//    @Override
//    public void setUp() throws Exception {
//        super.setUp();
//        final String[] keys = {"A","B","C"};
//        final int[] index = {0,1,2,1,2,1,0,2,1};
//        DictionaryEncodedColumn column = new TempDictColumn() {
//            @Override
//            public void flush() {
//
//            }
//
//            @Override
//            public int size() {
//                return 3;
//            }
//
//            @Override
//            public Object getValue(int index) {
//                return keys[index];
//            }
//
//            @Override
//            public int getIndexByRow(int row) {
//                return index[row];
//            }
//        };
//        node = new MergeIndexNode(1, 1, 1, new DictionaryEncodedColumn[]{column, column});
//    }
//
//
//
//    public void testInitDataByIndex() {
//        assertEquals(node.getData(), "B");
//    }
//
//    public void testCreateKey() {
//        assertEquals(node.createKey(), ((long)1 << 31) + 1);
//    }
}