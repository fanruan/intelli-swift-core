package com.fr.swift.service.handler.indexing;

import com.fr.swift.event.base.AbstractIndexingRpcEvent;
import com.fr.swift.service.handler.base.Handler;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftIndexingEventHandler implements Handler<AbstractIndexingRpcEvent> {
    @Override
    public <S extends Serializable> S handle(AbstractIndexingRpcEvent event) {
        switch (event.subEvent()) {

        }
        return null;
    }
}
