package com.fr.swift.cloud.beans.factory;

import junit.framework.TestCase;

/**
 * This class created on 2018/11/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftScannerTest extends TestCase {

    public void testScannerByName() {
        BeanRegistry testRegister = new AbstractBeanRegistry() {
        };
        SwiftBeanScanner scanner = new SwiftBeanScanner(testRegister);
        scanner.scan("com.fr.swift.cloud.beans.factory.bean");
        assertEquals(testRegister.getBeanDefinitionMap().size(), 2);
    }
}
