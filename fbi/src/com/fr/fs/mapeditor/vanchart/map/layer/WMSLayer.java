package com.fr.fs.mapeditor.vanchart.map.layer;

import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

public class WMSLayer implements XMLable {
    private static final String XML_TAG = "wmsLayer";
    private String layer;
    private boolean selected = false;

    public WMSLayer(String layer, boolean selected) {
        this.layer = layer;
        this.selected = selected;
    }

    public WMSLayer() {
    }

    public WMSLayer(String layer) {
        this.layer = layer;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void readXML(XMLableReader xmLableReader) {
        if(xmLableReader.isChildNode()) {
            if (xmLableReader.getTagName().equals(XML_TAG)) {
                layer = xmLableReader.getAttrAsString("layer", "");
                selected = xmLableReader.getAttrAsBoolean("selected", false);
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter xmlPrintWriter) {
        xmlPrintWriter.startTAG(XML_TAG)
                .attr("layer", layer)
                .attr("selected", selected)
                .end();

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        WMSLayer layer = (WMSLayer)super.clone();
        layer.setLayer(getLayer());
        layer.setSelected(isSelected());
        return layer;
    }


    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof WMSLayer
                && ComparatorUtils.equals(((WMSLayer) obj).getLayer(), getLayer())
                && ComparatorUtils.equals(((WMSLayer) obj).isSelected(), isSelected());
    }
}
