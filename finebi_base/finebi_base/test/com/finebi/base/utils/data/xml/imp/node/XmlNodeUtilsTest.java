package com.finebi.base.utils.data.xml.imp.node;

import com.finebi.base.data.DesiginSetting;
import com.finebi.base.data.Groups;
import com.finebi.base.data.Tables;
import com.finebi.base.data.xml.node.XmlNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <andrew.asa>
 * @since <pre>十月 15, 2017</pre>
 */
public class XmlNodeUtilsTest {

    @Before
    public void before() throws Exception {

    }

    @After
    public void after() throws Exception {

    }


    @Test
    public void testGetXmlNodeByClass() throws Exception {

        XmlNode node3 = XmlNodeUtils.getXmlNodeByClass(Groups.class);
        XmlNode node4 = XmlNodeUtils.getXmlNodeByClass(Tables.class);
        XmlNode node5 = XmlNodeUtils.getXmlNodeByClass(DesiginSetting.class);
    }
}
