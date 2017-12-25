package com.finebi.base.utils.annotations;

import com.finebi.base.data.xml.item.XmlItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;


/**
 * @author <andrew.asa>
 * @since <pre>九月 30, 2017</pre>
 */
public class AnnotationsUtilsTest {

    @Before
    public void before() throws Exception {

    }

    @After
    public void after() throws Exception {

    }

    @Test
    public void testInvokeSetMethodForOFieldParam() throws Exception {

    }

    @Test
    public void testInvokeSetMethodForObjectFieldNameParam() throws Exception {

        AnnotationDemo demo = new AnnotationDemo("name", 123, 456);
        Object name = AnnotationsUtils.invokeGetMethod(demo, "name");
        Object time = AnnotationsUtils.invokeGetMethod(demo, "time");
        Object deep = AnnotationsUtils.invokeGetMethod(demo, "deep");
    }

    @Test
    public void testGetAnnotationFields() throws Exception {

//        Map<java.lang.reflect.Field, XmlItem> map = AnnotationsUtils.getAnnotationFields(Tables.class, XmlItem.class);
//        for (java.lang.reflect.Field field : map.keySet()) {
//            field.getType();
//            field.getName();
//            XmlItem xmlItem = map.get(field);
//            xmlItem.listElementType();
//        }

        Map<java.lang.reflect.Field, XmlItem> map2 = AnnotationsUtils.getAnnotationFields(String.class, XmlItem.class);
    }


    @Test
    public void testInvokeGetMethodForObjectField() throws Exception {

    }


    @Test
    public void testInvokeGetMethodForObjectFieldName() throws Exception {

    }


    @Test
    public void testUpperFirstChart() throws Exception {

    }

} 
