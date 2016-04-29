package com.fr.bi.stable.io.io;

import com.fr.base.BaseXMLUtils;
import com.fr.base.FRContext;
import com.fr.stable.CodeUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ListWriter implements XMLWriter {
    public static final String XML_TAG = "GroupValue";
    private List valueList;

    public ListWriter(List valueList) {
        this.valueList = valueList;
    }

    public static void writeValueListToFile(List valueList, File f) {
        ListWriter rw = new ListWriter(valueList);
        rw.writeToFile(f);
    }

    private void writeToFile(File f) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(f);
            BaseXMLUtils.writeXMLFile(os, this);
        } catch (FileNotFoundException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        Iterator iter = valueList.iterator();
        while (iter.hasNext()) {
            writer.startTAG("child").textNode(CodeUtils.passwordEncode(iter.next().toString())).end();
        }
        writer.end();
    }

}