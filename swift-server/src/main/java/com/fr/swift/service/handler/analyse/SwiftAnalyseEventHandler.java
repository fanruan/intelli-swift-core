package com.fr.swift.service.handler.analyse;

import com.fr.swift.event.base.AbstractAnalyseEvent;
import com.fr.swift.service.handler.base.Handler;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftAnalyseEventHandler implements Handler<AbstractAnalyseEvent> {
    @Override
    public void handle(AbstractAnalyseEvent event) {
        switch (event.subEvent()) {

        }
    }
}
