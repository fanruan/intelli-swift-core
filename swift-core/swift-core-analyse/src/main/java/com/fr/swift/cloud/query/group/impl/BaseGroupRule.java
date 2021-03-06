package com.fr.swift.cloud.query.group.impl;

import com.fr.swift.cloud.query.group.GroupRule;
import com.fr.swift.cloud.source.core.Core;
import com.fr.swift.cloud.source.core.CoreGenerator;

/**
 * @author anchore
 * @date 2018/1/29
 */
abstract class BaseGroupRule implements GroupRule {
    @Override
    public Core fetchObjectCore() {
        try {
            return new CoreGenerator(this).fetchObjectCore();
        } catch (Exception ignore) {

        }
        return Core.EMPTY_CORE;
    }
}