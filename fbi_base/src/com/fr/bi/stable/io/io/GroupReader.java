package com.fr.bi.stable.io.io;

import com.fr.base.BaseXMLUtils;
import com.fr.base.FRContext;
import com.fr.general.ComparatorUtils;
import com.fr.stable.CodeUtils;
import com.fr.stable.xml.XMLFileReader;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupReader implements XMLReadable, XMLFileReader {
    public static final String XML_TAG = "v";

    private List<String> valueList;

    private GroupReader() {
    }

    public static GroupReader readFromFile(File f) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            return (GroupReader) BaseXMLUtils.readXMLFile(in, new GroupReader());
        } catch (Exception e) {
			FRContext.getLogger().error(e.getMessage(), e);
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public List<String> getValueList() {
        return valueList;
    }

    @Override
    public void readXML(XMLableReader reader) {
        this.valueList = new ArrayList<String>();
        reader.readXMLObject(new XMLReadable() {
            @Override
            public void readXML(XMLableReader reader) {
                if (reader.isChildNode()) {
                    if (ComparatorUtils.equals(reader.getTagName(), "child")) {
                        String value = reader.getElementValue();
                        try {
                            valueList.add(CodeUtils.passwordDecode(value));
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public Object readFileContent(XMLableReader reader) {
        GroupReader rw = new GroupReader();
        reader.readXMLObject(rw);
        return rw;
    }

    @Override
    public Object errorHandler() {
        GroupReader rw = new GroupReader();
        rw.valueList = new ArrayList<String>();
        return rw;
    }
}