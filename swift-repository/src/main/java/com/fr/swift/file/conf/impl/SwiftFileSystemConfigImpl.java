package com.fr.swift.file.conf.impl;

import com.fr.swift.file.conf.AbstractSwiftFileSystemConfig;
import com.fr.swift.file.system.SwiftFileSystemType;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftFileSystemConfigImpl extends AbstractSwiftFileSystemConfig {
    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public SwiftFileSystemType getType() {
        return SwiftFileSystemType.SWIFT;
    }
}
