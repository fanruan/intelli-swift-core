package com.fr.swift.service.handler.history;

import com.fr.swift.service.event.base.AbstractHistoryEvent;
import com.fr.swift.service.handler.base.Handler;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftHistoryEventHandler implements Handler<AbstractHistoryEvent> {
    @Override
    public void handle(AbstractHistoryEvent event) {
        switch (event.subEvent()) {

        }
    }
}
