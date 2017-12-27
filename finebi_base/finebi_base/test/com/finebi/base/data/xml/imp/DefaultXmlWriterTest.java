package com.finebi.base.data.xml.imp;


import com.finebi.base.data.DesiginSetting;
import com.finebi.base.data.DetailWidget;
import com.finebi.base.data.Groups;
import com.finebi.base.data.TableDemo;
import com.finebi.base.data.TableWidget;
import com.fr.third.v2.org.apache.xmlbeans.impl.jam.provider.ResourcePath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author <andrew.asa>
 * @since <pre>十月 9, 2017</pre>
 */
public class DefaultXmlWriterTest {

    @Before
    public void before() throws Exception {

    }

    @After
    public void after() throws Exception {

    }

    @Test
    public void testWrite() throws Exception {

        //File src = ResourcePath.getResourceFile("temp.xml");
        //DefaultXmlReader reader = new DefaultXmlReader();
        //Tables tables = reader.readXml(src, Tables.class);
        //File dst = ResourcePath.getResourceFile("temp-dst.xml");
        //DefaultXmlWriter writer = new DefaultXmlWriter();
        //writer.write(dst, tables);
    }

    @Test
    public void testWrite2() throws Exception {

        //File src = ResourcePath.getResourceFile("group.xml");
        //DefaultXmlReader reader = new DefaultXmlReader();
        //Groups o = reader.readXml(src, Groups.class);
        //File dst = ResourcePath.getResourceFile("group-dst.xml");
        //DefaultXmlWriter writer = new DefaultXmlWriter();
        //writer.write(dst, o);
    }

    @Test
    public void testWrite3() throws Exception {

        //File dst = ResourcePath.getResourceFile("desiginesetting-dst.xml");
        //DefaultXmlWriter writer = new DefaultXmlWriter();
        //DesiginSetting setting = new DesiginSetting();
        //setting.setName("m");
        //DetailWidget detailWidget = new DetailWidget();
        //detailWidget.setDetailName("detail");
        //detailWidget.setInitTime(1234566);
        //detailWidget.setWidgetName("detailwidget");
        //TableWidget tableWidget = new TableWidget();
        //tableWidget.setTableName("table");
        //tableWidget.setInitTime(127892);
        //tableWidget.setWidgetId("asdfsdbvsd");
        //setting.addWidgetMap("detailwidget", detailWidget);
        //setting.addWidgetMap("tablewidget", tableWidget);
        //writer.write(dst, setting);
    }

    @Test
    public void testWrite4() throws Exception {

        //TableDemo demo = new TableDemo();
        //TableWidget tableWidget = new TableWidget();
        //tableWidget.setTableName("tableWidgetName");
        //demo.setWidget(tableWidget);
        //demo.setTableDemoName("tableDemoName");
        //File dst = ResourcePath.getResourceFile("tableDemo-dst.xml");
        //DefaultXmlWriter writer = new DefaultXmlWriter();
        //writer.write(dst, demo);
    }

    @Test
    public void testWriteField() throws Exception {

    }

} 
