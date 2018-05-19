package com.fr.engine.bi.register;

import com.finebi.conf.service.listener.FineLifecycleEvent;
import com.finebi.conf.service.listener.FineLifecycleState;
import com.finebi.conf.service.listener.FoundationStatusListener;

/**
 * Created by andrew_asa on 2018/5/19.
 */
public class FoundationStatusListenerImpl implements FoundationStatusListener {

    @Override
    public void lifecycleEvent(FineLifecycleEvent var1) {

        if (var1.getLifecycle().getState().equals(FineLifecycleState.START_COMPLETE)) {
            // do something
            System.out.println();
        }
    }
}

