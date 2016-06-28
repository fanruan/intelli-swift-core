//package generate;
//
//import junit.framework.TestCase;
//
//import java.util.*;
//
///**
// * Created by kary on 2016/6/21.
// */
//public class TimerRunnerTest extends TestCase {
//    protected int executedTimes = 0;
//
//    public void testRunTimeCancel() {
//        executedTimes=0;
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                executedTimes += 1;
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    assertFalse(true);
//                }
//
//            }
//
//        }, new Date(), 900);
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            assertFalse(true);
//        }
//        timer.cancel();
//        assertTrue(executedTimes == 1);
//    }
//
//    public void testRunTime() {
//        executedTimes=0;
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                executedTimes += 1;
//            }
//
//        }, new Date(), 100);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            assertFalse(true);
//        }
//        assertTrue(executedTimes >=10);
//        timer.purge();
//    }
//}