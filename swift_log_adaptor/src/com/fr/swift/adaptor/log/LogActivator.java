package com.fr.swift.adaptor.log;

import com.fr.general.LogOperatorFactory;
import com.fr.module.Activator;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class LogActivator extends Activator {
    @Override
    public void start() {
        LogOperatorFactory.registerLogOperatorProvider(LogOperatorImpl.getInstance());
    }

    @Override
    public void stop() {
        LogOperatorFactory.registerLogOperatorProvider(null);
    }
}