package com.fr.swift.boot.register;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.ServiceRegistry;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.event.MaskHistoryListener;
import com.fr.swift.segment.event.PushSegmentLocationListener;
import com.fr.swift.segment.event.RemoveHistoryListener;
import com.fr.swift.segment.event.RemoveSegmentLocationListener;
import com.fr.swift.segment.event.TransferRealtimeListener;
import com.fr.swift.segment.event.UploadHistoryListener;

import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/3/5
 *
 * @author Lucifer
 * @description
 */
public class BootRegister {

    public static void registerListener() {
        TransferRealtimeListener.listen();
        UploadHistoryListener.listen();
        MaskHistoryListener.listen();
        RemoveHistoryListener.listen();
        PushSegmentLocationListener.listen();
        RemoveSegmentLocationListener.listen();
    }

    public static void registerProxy() {
        //RPC远端可接收调用的SERVICE
        ServiceRegistry serviceRegistry = ProxyServiceRegistry.get();
        Map<String, Object> proxyServices = SwiftContext.get().getBeansByAnnotations(ProxyService.class);
        for (Map.Entry<String, Object> proxyService : proxyServices.entrySet()) {
            serviceRegistry.registerService(proxyService.getValue());
        }

        ProcessHandlerRegistry processHandlerRegistry = ProxyProcessHandlerRegistry.get();
        List<Class<?>> clazzList = SwiftContext.get().getClassesByAnnotations(RegisteredHandler.class);
        for (Class<?> clazz : clazzList) {
            try {
                RegisteredHandler registeredHandler = clazz.getAnnotation(RegisteredHandler.class);
                if (registeredHandler != null) {
                    processHandlerRegistry.addHandler(registeredHandler.value(), (Class<? extends ProcessHandler>) clazz);
                }
            } catch (IllegalArgumentException error) {
                SwiftLoggers.getLogger().error(error);
            }
        }
    }
}
