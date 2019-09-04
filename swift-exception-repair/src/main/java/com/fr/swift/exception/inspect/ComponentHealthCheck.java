package com.fr.swift.exception.inspect;

import com.fr.swift.exception.inspect.bean.ComponentHealthInfo;
import com.fr.swift.log.SwiftLoggers;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
public class ComponentHealthCheck {

    private ComponentHealthInspector inspector;

    private volatile long lastInspectionTime;

    private volatile Object result;

    private volatile Object preTarget;

    private final long stateSustainGap;

    public ComponentHealthCheck(ComponentHealthInspector inspector, long stateSustainGap) {
        this.stateSustainGap = stateSustainGap;
        this.inspector = inspector;
        this.lastInspectionTime = 0L;
    }

    public <T> T check(ComponentHealthInfo info) {
        //发生读写冲突的后果是多做了一次检测，影响不大，所以读时不加锁，只有写时加锁
        //如果30秒内请求同样的目标则返回上次检测的结果
        if (System.currentTimeMillis() - lastInspectionTime < stateSustainGap
                && info.getTarget().equals(preTarget)) {
            return (T) result;
        }
        preTarget = info.getTarget();
        try {
            result = inspector.inspect(info);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        lastInspectionTime = System.currentTimeMillis();
        return (T) result;

    }

    public boolean isHealthy() {
        //对于一些返回值为布尔类型的服务检测可以使用此方法
        boolean health;
        health = (boolean) inspector.inspect();
        return health;
    }

}
