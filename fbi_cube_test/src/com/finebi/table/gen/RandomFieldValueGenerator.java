package com.finebi.table.gen;
/**
 * This class created on 2017/5/17.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.algorithem.BIRandomUitils;

import java.util.ArrayList;
import java.util.List;

public abstract class RandomFieldValueGenerator<V> {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(RandomFieldValueGenerator.class);
    protected int groupSize = 1;
    protected int rowSize = 1;
    protected List<V> groupValue = new ArrayList<V>();
    protected BIRandomUitils.RandomGenerator random = BIRandomUitils.getRandomGenerator();

//    protected static int RATIO_THRESHOLD = 10;
//    protected static int ROW_THRESHOLD = GROUP_THRESHOLD*10;

    public RandomFieldValueGenerator(int groupSize, int rowSize) {
        this.groupSize = groupSize;
        this.rowSize = rowSize;
        generateGroup();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                long time = System.currentTimeMillis();
//
//                System.out.println(System.currentTimeMillis() - time);
//
//            }
//        }).start();
    }

//    public boolean useCache() {
//        return (rowSize/groupSize)>RATIO_THRESHOLD;
//    }

    protected abstract void generateGroup();


    public V getValue() {
        int index = 0;
        index = (random.getRandomAbsInteger() % groupSize);
        return groupValue.get(index);
    }

    public boolean isDataPrepared() {
        return groupSize == groupValue.size();
    }

    public List<V> getGroupValue() {
        return groupValue;
    }
}
