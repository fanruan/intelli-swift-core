package com.fr.swift.test;

import com.fr.base.FRContext;
import com.fr.dav.LocalEnv;

/**
 * @author anchore
 * @date 2018/5/8
 */
public class Preparer {
    public static void prepareIo() {
        FRContext.setCurrentEnv(new LocalEnv(System.getProperty("user.dir")));
    }
}