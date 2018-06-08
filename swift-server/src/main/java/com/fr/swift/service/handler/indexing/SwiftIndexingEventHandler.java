package com.fr.swift.service.handler.indexing;

import com.fr.swift.event.base.AbstractIndexingEvent;
import com.fr.swift.service.handler.base.Handler;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftIndexingEventHandler implements Handler<AbstractIndexingEvent> {
    @Override
    public void handle(AbstractIndexingEvent event) {
        switch (event.subEvent()) {

        }
    }
}
