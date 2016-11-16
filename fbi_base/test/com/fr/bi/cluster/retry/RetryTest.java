package com.fr.bi.cluster.retry;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.json.JSONObject;
import junit.framework.TestCase;

import java.util.concurrent.Callable;

/**
 * Created by wang on 2016/11/8.
 */
public class RetryTest extends TestCase {
    public void testRetry(){
//        String result = "";
//        Callable task = new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                throw new IndexOutOfBoundsException();
//            }
//        };
//        RetryLoop retryLoop = new RetryLoop();
//        retryLoop.initial(new RetryNTimes(3, 10));
//        try {
//            result = (String) RetryLoop.retry(task, retryLoop);
//        } catch (Exception ex) {
//            BILoggerFactory.getLogger().error(ex.getMessage(), ex);
//        }
    }
}
