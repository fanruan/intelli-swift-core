package com.fr.swift.service.handler.realtime;

import com.fr.swift.service.event.base.AbstractRealTimeEvent;
import com.fr.swift.service.handler.base.Handler;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftRealTimeEventHandler implements Handler<AbstractRealTimeEvent> {
    @Override
    public void handle(AbstractRealTimeEvent event) {
        switch (event.subEvent()) {

        }
    }
}
