package com.fr.bi.stable.utils.code;

import com.finebi.cube.common.log.BILoggerFactory;

/**
 * Created by Connery on 2015/12/9.
 */
public class BILogDelegate {
    public static void errorDelegate(String message, Throwable e) {
        BILoggerFactory.getLogger().error(message, e);
    }

}