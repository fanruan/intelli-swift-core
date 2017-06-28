package com.fr.bi.stable.utils.time;

import com.fr.bi.base.ValueConverter;
import com.fr.bi.base.ValueConverterFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;
import com.fr.stable.collections.array.IntArray;
import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by daniel on 2016/6/8.
 */
public class RangeIndexGetterTest extends TestCase {


    private long[] createDate(int len) {
        long[] d = new long[len];
        long t = System.currentTimeMillis();
        for(int i = 0; i < len; i++) {
            d[i] = t - (long)(Math.random()*1000 * 60 * 1000* 60 *24);
        }
        return d;
    }

    private CubeTreeMap buildMap(long[] d, ValueConverter converter) {
        Map<Object, IntArray> treeMap = new TreeMap<Object, IntArray>();
        for (int i = 0; i < d.length; i ++){
            Object value = converter.result2Value(d[i]);
            IntArray list = treeMap.get(value);
            if (list == null) {
                list = new IntArray();
                treeMap.put(value, list);
            }
            list.add(i);
        }
        CubeTreeMap getter = new CubeTreeMap(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
        for (Map.Entry<Object, IntArray> entry : treeMap.entrySet()){
            getter.put(entry.getKey(), GVIFactory.createGroupValueIndexBySimpleIndex(entry.getValue()));
        }
        return getter;
    }

    public void testRandomTestGetter () {
        for( int i = 10; i <= (10^5); i *=10) {
            testRangeGetter(i);
        }
    }


    private  void testRangeGetter (int len) {
        long[] dates = createDate(len);
        CubeTreeMap<Integer> year = buildMap(dates, ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR));
        CubeTreeMap<Long> month = buildMap(dates, ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR_MONTH));
        CubeTreeMap<Long> day = buildMap(dates, ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YMD));
        RangeIndexGetter getter = new RangeIndexGetter(year, month, day);
       for(int i = 0; i < 3000; i++) {
           testRange(getter, dates);
       }
    }

    private void testRange(RangeIndexGetter getter, final long[] dates) {
        final int start = (int) (Math.random() * dates.length * 1.1);
        final int end = (int) (Math.random() * dates.length * 1.1);
        testResult(getter, dates, start, end);
    }

    private  void  testResult (RangeIndexGetter getter, final long[] dates, final  int start, final  int end) {
        final BIDay s = start >= dates.length ? null : createDay(dates[start]);
        final BIDay e = end >= dates.length ? null : createDay(dates[end]);
        GroupValueIndex gvi = getter.createRangeIndex(s, e);
        if (gvi == null){
            gvi = GVIFactory.createAllShowIndexGVI(dates.length);
        }
        gvi.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                if(s != null) {
                    assertTrue(createDay(dates[row]).getTime() >= s.getTime());
                }
                if(e != null) {
                    assertTrue(createDay(dates[row]).getTime() <= e.getTime());
                }
            }
        });
        int count = 0;
        for(int i = 0; i < dates.length; i++) {
            long d = createDay(dates[i]).getTime();
            if((s== null || d >= s.getTime() )&& (e== null || d <= e.getTime())) {
                count++;
            }
        }
        assertEquals(gvi.getRowsCountWithData(), count);
    }


    private BIDay createDay(long t) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(t);
        return new BIDay(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
    }



    public void testSingle() {
        long[] dates = new long[]{1428439009380L, 1443466793195L,
                1379916772666L, 1407824913203L, 1458362238604L,
                1380025029390L, 1423088932354L, 1465132082249L,
                1385897551991L, 1384982841078L};
        CubeTreeMap year = buildMap(dates,ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR));
        CubeTreeMap month = buildMap(dates, ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR_MONTH));
        CubeTreeMap day = buildMap(dates, ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YMD));
        int start = 5;
        int end = 6;
        RangeIndexGetter getter = new RangeIndexGetter(year, month, day);
        testResult(getter, dates, start, end);
    }

    public void testSameDay() {
        long[] dates = new long[]{1428439009380L, 1443466793195L,
                1379916772666L, 1407824913203L, 1458362238604L,
                1380025029390L, 1423088932354L, 1465132082249L,
                1385897551991L, 1384982841078L};
        CubeTreeMap year = buildMap(dates, ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR));
        CubeTreeMap month = buildMap(dates, ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YEAR_MONTH));
        CubeTreeMap day = buildMap(dates, ValueConverterFactory.createDateValueConverter(DateConstant.DATE.YMD));
        int start = 5;
        int end = 5;
        RangeIndexGetter getter = new RangeIndexGetter(year, month, day);
        testResult(getter, dates, start, end);
    }

}
