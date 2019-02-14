package com.fr.log.impl;

import com.fr.decision.log.LogSearchBox;
import com.fr.module.Activator;
import com.fr.module.extension.Prepare;

/**
 * Created by Lyon on 2018/6/21.
 */
public class SwiftLogSearchProviderActivator extends Activator implements Prepare {

    @Override
    public void start() {
//        LogSearchBox.register(SwiftLogSearchProvider.getInstance());
    }

    @Override
    public void stop() {

    }

    @Override
    public void prepare() {

    }
}
