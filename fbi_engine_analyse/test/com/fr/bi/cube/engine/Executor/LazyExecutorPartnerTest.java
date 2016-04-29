package com.fr.bi.cube.engine.Executor;

import com.fr.bi.utility.TestTimeAssert;
import junit.framework.TestCase;

/**
 * Created by Connery on 2014/12/14.
 */
public class LazyExecutorPartnerTest extends TestCase {
    public static final int NODE_AMOUNT = 1000;

    /**
     * 测试说明：先取出1000
     */
//    public void testGetCorrespondingContent_01() {
//        final LazyExecutorPartner4Test lazyExecutorPartner4Test = new LazyExecutorPartner4Test((long) 0, NODE_AMOUNT);
////        startGet(lazyExecutorPartner4Test, 1000, 1000 * LazyExecutorPartner4Test.TIME);
////        startGet(lazyExecutorPartner4Test, 100, 0);
//        TestTimeAssert testTimeAssert = new TestTimeAssert();
//        testTimeAssert.beginRecord();
//        int count = 4;
//        int result = lazyExecutorPartner4Test.getCorrespondingContent(count);
//        assertEquals(result, count);
//        testTimeAssert.addAssertTime((count+1) * LazyExecutorPartner4Test.TIME, 50);
//        testTimeAssert.beginRecord();
//         result = lazyExecutorPartner4Test.getCorrespondingContent(count);
//        assertEquals(result, count);
//        testTimeAssert.addAssertTime(0 * LazyExecutorPartner4Test.TIME, 50);
//
//    }
    public void testForward() {
        final LazyExecutorPartner4Test lazyExecutorPartner4Test = new LazyExecutorPartner4Test((long) 0, NODE_AMOUNT);
        LazyExecutorPartnerTest lazyExecutorPartnerTest = new LazyExecutorPartnerTest();
        lazyExecutorPartnerTest.startGet(lazyExecutorPartner4Test, 100, 100 * LazyExecutorPartner4Test.TIME);
        lazyExecutorPartnerTest.startGet(lazyExecutorPartner4Test, 500, 500 * LazyExecutorPartner4Test.TIME);
        try {
            Thread.sleep(10000);
        } catch (Exception ex) {
        }
    }

    public void testBackward() {
        final LazyExecutorPartner4Test lazyExecutorPartner4Test = new LazyExecutorPartner4Test((long) 0, NODE_AMOUNT);
        LazyExecutorPartnerTest lazyExecutorPartnerTest = new LazyExecutorPartnerTest();
        lazyExecutorPartnerTest.startGet(lazyExecutorPartner4Test, 500, 500 * LazyExecutorPartner4Test.TIME);
        try {
            Thread.sleep(500 * LazyExecutorPartner4Test.TIME);
        } catch (Exception ex) {
        }
        lazyExecutorPartnerTest.startGet(lazyExecutorPartner4Test, 100, 0);
        try {
            Thread.sleep(100);
        } catch (Exception ex) {
        }
    }

    public void testTwoSame() {
        final LazyExecutorPartner4Test lazyExecutorPartner4Test = new LazyExecutorPartner4Test((long) 0, NODE_AMOUNT);
        LazyExecutorPartnerTest lazyExecutorPartnerTest = new LazyExecutorPartnerTest();
        lazyExecutorPartnerTest.startGet(lazyExecutorPartner4Test, 100, 100 * LazyExecutorPartner4Test.TIME);
        lazyExecutorPartnerTest.startGet(lazyExecutorPartner4Test, 100, 100 * LazyExecutorPartner4Test.TIME);

        try {
            Thread.sleep(10000);
        } catch (Exception ex) {
        }
    }

    public void testOverAmount() {
        final LazyExecutorPartner4Test lazyExecutorPartner4Test = new LazyExecutorPartner4Test((long) 0, 50);
        LazyExecutorPartnerTest lazyExecutorPartnerTest = new LazyExecutorPartnerTest();
        lazyExecutorPartnerTest.startGet(lazyExecutorPartner4Test, 50, 50 * LazyExecutorPartner4Test.TIME);
        lazyExecutorPartnerTest.startGet(lazyExecutorPartner4Test, 51, 51 * LazyExecutorPartner4Test.TIME);

        try {
            Thread.sleep(10000);
        } catch (Exception ex) {
        }
    }

//    public void testGetCorrespondingContent_02() {
//        final LazyExecutorPartner4Test lazyExecutorPartner4Test = new LazyExecutorPartner4Test((long) 0, NODE_AMOUNT);
//        startGet(lazyExecutorPartner4Test, 100, 100 * LazyExecutorPartner4Test.TIME);
//        startGet(lazyExecutorPartner4Test, 1000, 1000 * LazyExecutorPartner4Test.TIME);
//
//    }


    private void startGet(final LazyExecutorPartner4Test lazyExecutorPartner4Test, final int count, final long time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TestTimeAssert testTimeAssert = new TestTimeAssert();
                testTimeAssert.beginRecord();
                int result = lazyExecutorPartner4Test.getCorrespondingContent(count);
                if (result == -1) {
                    assertTrue(lazyExecutorPartner4Test.outRange(count));
                } else {
                    assertEquals(result, count);
                }
                testTimeAssert.addAssertTime(time, 100);
            }
        }).start();
    }
}