package com.fr.bi.cube.engine.Executor;

import com.fr.bi.cube.engine.exception.TerminateExecutorException;
import com.fr.bi.utility.TestUtil;

import java.util.ArrayList;

/**
 * Created by Connery on 2014/12/14.
 * 测试方法，把rowlist中的内容，移到resultlist中
 */
public class LazyExecutorPartner4Test extends LazyExecutorPartner<Object> implements ILazyExecutorOperation<Integer, Integer> {

    public static final Integer TIME = 10;
    public ArrayList<Integer> result;
    private ArrayList<Integer> rawList;

    public LazyExecutorPartner4Test(Long threshold, int amount) {
        rawList = new ArrayList<Integer>(amount);
        result = new ArrayList<Integer>(amount);
        this.lazyExecutor = new LazyExecutor();
        for (int i = 1; i <= amount; i++) {
            rawList.add(i);
        }
        this.lazyExecutor.initial(this, rawList.iterator());
        this.lazyExecutor.start();
    }

    public Integer getCorrespondingContent(long count) {
        waitExecutor(count);
        if (((int) count - 1) < result.size()) {
            return result.get((int) count - 1);
        } else {
            System.out.println("OUT");
            return -1;
        }
    }

    public boolean outRange(long count) {
        return (((int) count - 1) >= result.size());
    }

    @Override
    public void initPrecondition() {

    }


    @Override
    public void endCheck() {

    }

    @Override
    public Integer mainTaskConditions(Integer para_1) {
        return null;
    }

    @Override
    public boolean jumpCurrentOne(Integer para) throws TerminateExecutorException {
        TestUtil.wasteTime(TIME);
        return false;
    }

    @Override
    public void mainTask(Integer para_1, Integer para_2) throws TerminateExecutorException {
        this.result.add(para_1);
    }

    @Override
    public void executorTerminated() {

    }
}