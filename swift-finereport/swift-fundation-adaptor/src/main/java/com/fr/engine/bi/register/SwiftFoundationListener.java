package com.fr.engine.bi.register;

import com.finebi.conf.service.listener.FineLifecycleEvent;
import com.finebi.conf.service.listener.FineLifecycleState;
import com.finebi.conf.service.listener.FoundationStatusListener;
import com.fr.swift.schedule.SwiftTimeScheduleService;

/**
 * This class created on 2018/5/21
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftFoundationListener implements FoundationStatusListener {

    @Override
    public void lifecycleEvent(FineLifecycleEvent event) {
        if (event.getLifecycle().getState() == FineLifecycleState.START_COMPLETE) {
            SwiftTimeScheduleService.getInstance();
        }
    }
}
