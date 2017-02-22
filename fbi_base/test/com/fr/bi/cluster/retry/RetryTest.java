package com.fr.bi.cluster.retry;

import com.finebi.cube.common.log.BILoggerFactory;
import junit.framework.TestCase;

import java.util.concurrent.Callable;

/**
 * Created by wang on 2016/11/8.
 */
public class RetryTest extends TestCase {
    public void testRetry() {
        String result = "";
        Callable task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("Time");
//                throw new IndexOutOfBoundsException();
                return "";
            }
        };
        RetryLoop retryLoop = new RetryLoop();
        retryLoop.initial(new RetryNTimes(3, 10));
        try {
            result = (String) RetryLoop.retry(task, retryLoop);
        } catch (Exception ex) {
            BILoggerFactory.getLogger().error(ex.getMessage(), ex);
        }
    }

    public void testRetryDemo() {

        Callable MiningOp = new Callable<String>() {
            int count = 0;

            @Override
            public String call() throws Exception {
                count++;
                System.out.println("Time:" + count);
                if (count < 3) {
                    throw new Exception("Empty!");
                } else {
                    return "Good! Get it,the big diamond";
                }

            }
        };
        RetryLoop retryLoop = new RetryLoop();
        retryLoop.initial(new RetryNTimes(3, 100));
        try {
            String result = (String) RetryLoop.retry(MiningOp, retryLoop);
            System.out.println(result);
        } catch (Exception ex) {
            BILoggerFactory.getLogger().error(ex.getMessage(), ex);
        }
    }
}
