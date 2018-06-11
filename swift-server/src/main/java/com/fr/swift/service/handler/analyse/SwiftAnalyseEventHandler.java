package com.fr.swift.service.handler.analyse;

import com.fr.swift.event.base.AbstractAnalyseRpcEvent;
import com.fr.swift.service.handler.base.Handler;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftAnalyseEventHandler implements Handler<AbstractAnalyseRpcEvent> {
    @Override
    public <S extends Serializable> S handle(AbstractAnalyseRpcEvent event) {
        switch (event.subEvent()) {

        }
        return null;
    }
}
