package com.fr.bi.cal.analyze.cal.Executor;


import com.fr.bi.cal.analyze.exception.TerminateExecutorException;

/**
 * Created by Connery on 2014/12/14.
 */
public interface ILazyExecutorOperation<T, F> {

    /**
     * 为主要部分准备执行环境
     *
     * @return
     */
    F mainTaskConditions(T para_1);

    /**
     * LazyExecutor在执行mainTaskConditions前，会判断是否跳过当前的
     * mainTask
     *
     * @return 布尔值
     */
    boolean preJumpCurrentOne(T para_1);

    /**
     * LazyExecutor在执行mainTask时，会判断是否跳过当前的
     * mainTask
     *
     * @return 布尔值
     */
    boolean jumpCurrentOne(F preCondition) throws TerminateExecutorException;

    /**
     * LazyExecutor循环体内，执行的主要部分
     * mainTask
     * T obj 遍历器next()
     */
    void mainTask(T itNext, F preCondition) throws TerminateExecutorException;

    void executorTerminated();
}