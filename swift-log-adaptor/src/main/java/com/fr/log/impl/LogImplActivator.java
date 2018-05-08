package com.fr.log.impl;

import com.fr.general.LogOperatorFactory;
import com.fr.module.Activator;
import com.fr.swift.adaptor.log.LogOperatorImpl;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class LogImplActivator extends Activator {
    @Override
    public void start() {
        LogOperatorFactory.registerLogOperatorProvider(LogOperatorImpl.getInstance());
    }

    @Override
    public void stop() {
        LogOperatorFactory.registerLogOperatorProvider(null);
    }
}