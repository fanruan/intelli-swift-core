package com.fr.swift.test.external;

import com.fr.swift.context.SwiftContext;
import org.junit.rules.ExternalResource;

/**
 * @author anchore
 * @date 2018/10/25
 */
public class ContextResource extends ExternalResource {

    @Override
    protected void before() {
        SwiftContext.init();
    }
}