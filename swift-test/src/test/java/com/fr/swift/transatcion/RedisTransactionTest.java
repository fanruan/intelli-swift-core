package com.fr.swift.transatcion;

import com.fr.swift.context.SwiftContext;
import junit.framework.TestCase;

/**
 * This class created on 2018/6/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RedisTransactionTest extends TestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        SwiftContext.init();

    }
}

