package com.finebi.base.data;

import com.finebi.base.data.xml.XmlObjectFactory;
import com.fr.stable.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Created by andrew_asa on 2017/10/23.
 */
public class WidgetFactory implements XmlObjectFactory {

    @Override
    public Object getObject(Attributes attributes) throws Exception {

        String type = attributes.getValue("type");
        if ("1".equals(type)) {
            return new DetailWidget();
        } else if ("2".equals(type)) {
            return new TableWidget();
        }
        return null;
    }

    @Override
    public void createAttr(Object o, AttributesImpl attr) {

        String type = "0";
        if (o.getClass().equals(DetailWidget.class)) {
            type = "1";
        } else if (o.getClass().equals(TableWidget.class)) {
            type = "2";
        }
        attr.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "type", StringUtils.EMPTY, type);
    }
}
