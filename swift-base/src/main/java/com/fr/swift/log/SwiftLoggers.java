package com.fr.swift.log;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.util.function.Function;

/**
 * @author anchore
 */
public final class SwiftLoggers {
    private static Function<?, SwiftLogger> loggerFactory = SwiftContext.getInstance().getBean("loggerFactory", Function.class);

    /**
     * @deprecated 不用static final SwiftLogger = SwiftLoggers.getLogger();的方式了
     * 推荐直接SwiftLoggers.getLogger().error(e);
     * 这个@Deprecated只起提醒作用，不是弃用
     */
    @Deprecated
    public static SwiftLogger getLogger() {
        return loggerFactory.apply(null);
    }

    /**
     * @deprecated 不用传class啦
     */
    @Deprecated
    public static SwiftLogger getLogger(Class cls) {
        return getLogger();
    }
}