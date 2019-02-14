package com.fr.swift.basics.base.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.IndexProcessHandler;
import com.fr.swift.config.bean.ServerCurrentStatus;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.IndexingService;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/10/25
 */
public abstract class BaseIndexProcessHandler extends BaseProcessHandler implements IndexProcessHandler {

    public BaseIndexProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected Object mergeResult(List resultList, Object... args) throws Throwable {
        Object obj = resultList.get(0);
        return handleAsyncResult(obj);
    }

    @Override
    public List<URL> processUrl(Target[] targets, Object... args) {
        IndexingService service = ProxySelector.getInstance().getFactory().getProxy(IndexingService.class);
        try {
            ServerCurrentStatus status = service.currentStatus();
            return Collections.singletonList(UrlSelector.getInstance().getFactory().getURL(status.getClusterId()));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}
