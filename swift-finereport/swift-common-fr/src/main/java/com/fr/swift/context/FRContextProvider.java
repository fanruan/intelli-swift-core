package com.fr.swift.context;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.workspace.WorkContext;

import java.io.File;

/**
 * @author yee
 * @date 2018-12-04
 */
@SwiftBean
public class FRContextProvider implements ContextProvider {
    @Override
    public String getContextPath() {
        String frPath = WorkContext.getCurrent().getPath();
        if (frPath != null) {
            return frPath + "/../";
        }
        String classPath = ContextUtil.getClassPath();
        return new File(classPath).isDirectory() ? classPath + "/../" : classPath + "/../../";
    }
}
