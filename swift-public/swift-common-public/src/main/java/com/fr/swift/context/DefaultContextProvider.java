package com.fr.swift.context;

import com.fr.swift.beans.annotation.SwiftBean;

import java.io.File;

/**
 * @author yee
 * @date 2018-12-04
 */
@SwiftBean
public class DefaultContextProvider implements ContextProvider {
    @Override
    public String getContextPath() {
        String classPath = ContextUtil.getClassPath();
        return new File(classPath).isDirectory() ? classPath + "/../" : classPath + "/../../";
    }
}
