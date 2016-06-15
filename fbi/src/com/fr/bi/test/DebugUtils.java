package com.fr.bi.test;

import com.fr.stable.DebugAssistant;
import com.fr.stable.bridge.StableFactory;

/**
 * Created by daniel on 2016/6/15.
 * 在BIPlate中通过反射加载，这个类不打入jar包，从而实现代码启动自动清理缓存
 * 不要移动位置，如果代码想看带缓存的效果将返回值改成false
 */
public class DebugUtils {

    static {
        StableFactory.registerMarkedObject(DebugAssistant.class.getName(), new DebugAssistant() {
            @Override
            public boolean requireDebug() {
                return true;
            }
        });
    }
}
