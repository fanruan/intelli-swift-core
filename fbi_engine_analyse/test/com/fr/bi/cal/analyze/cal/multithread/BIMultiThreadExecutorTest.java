package com.fr.bi.cal.analyze.cal.multithread;

import com.fr.bi.manager.PerformancePlugManager;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/8/8.
 */
public class BIMultiThreadExecutorTest extends TestCase{
    public void testSmallCount(){
        calculate(4);
    }

    public void testBigCount(){
        calculate(9123);
    }

    private void calculate(int size){
        try {
            Class c = PerformancePlugManager.getInstance().getClass();
            Field f = c.getDeclaredField("useMultiThreadCal");
            f.setAccessible(true);
            f.setBoolean(PerformancePlugManager.getInstance(), true);
        } catch (Exception e) {
        }
        int[] result = new int[size];
        final int[] cal = new int[size];
        List<BISingleThreadCal> list = new ArrayList<BISingleThreadCal>();
        for (int i = 0; i < size;i++){
            final int index = i;
            list.add(new BISingleThreadCal() {
                @Override
                public void cal() {
                    cal[index] = index + 1;
                }
            });
            result[i] = i + 1;
        }
        BIMultiThreadExecutor.execute(list);
        for (int i = 0; i < size; i ++){
            assertEquals(result[i], cal[i]);
        }
    }
}