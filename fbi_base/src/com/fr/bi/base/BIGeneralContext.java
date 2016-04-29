package com.fr.bi.base;

import com.fr.stable.EnvChangedListener;

/**
 * Created by GUY on 2015/3/3.
 */
public class BIGeneralContext {
    private static java.util.List envChangeListenerList = new java.util.ArrayList();

    private BIGeneralContext() {

    }

    public static void addEnvChangedListener(EnvChangedListener l) {
        envChangeListenerList.add(l);
    }

    public static void fireEnvChangeListener() {
        for (int i = 0; i < envChangeListenerList.size(); i++) {
            ((EnvChangedListener) envChangeListenerList.get(i)).envChanged();
        }
    }
}