package com.fr.bi.stable.utils.conf;

import com.fr.base.FRContext;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.project.ProjectConstants;

import java.io.File;

/**
 * Created by Connery on 2015/12/4.
 */
public class BISystemEnvUtils {
    private static boolean forceUseJDK_32;

    static {
        forceUseJDK_32 = new File(FRContext.getCurrentEnv().getPath()
                + File.separator + ProjectConstants.RESOURCES_NAME + File.separator + "use32.julie").exists();
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                forceUseJDK_32 = new File(FRContext.getCurrentEnv().getPath()
                        + File.separator + ProjectConstants.RESOURCES_NAME + File.separator + "use32.julie").exists();
            }
        });
    }


    /**
     * 服务器是否支持BI，需要JVM最大内存大于1G，64位JDK
     *
     * @return 服务器是否支持BI
     */
    public static boolean isSystemEnvProper() {
        return (!forceUseJDK_32) && System.getProperty("sun.arch.data.model").contains("32");
    }
}