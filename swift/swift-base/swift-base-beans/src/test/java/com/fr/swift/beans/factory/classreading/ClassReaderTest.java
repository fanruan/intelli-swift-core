package com.fr.swift.beans.factory.classreading;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.beans.factory.bean.TestWithoutBeanPrototype;
import junit.framework.TestCase;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClassReaderTest extends TestCase {

    public void testReadAnnoations() throws ClassNotFoundException {
        String path = TestWithoutBeanPrototype.class.getResource("").getPath();
        ClassAnnotations classAnnotations = ClassReader.read(path + "TestWithoutBeanPrototype.class");
        int annotationCount = 0;
        int otherCount = 0;
        for (String annotation : classAnnotations.getAnnotationNames()) {
            try {
                Class clazz = Class.forName(annotation);
                assertTrue(clazz.equals(SwiftBean.class) || clazz.equals(SwiftScope.class));
                annotationCount++;
            } catch (Exception ignore) {
                otherCount++;
            }
        }
        assertEquals(annotationCount, 2);
        assertEquals(otherCount, classAnnotations.getAnnotationNames().size() - 2);

    }
}
