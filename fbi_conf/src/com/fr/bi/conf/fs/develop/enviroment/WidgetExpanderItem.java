package com.fr.bi.conf.fs.develop.enviroment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by Connery on 2015/1/9.
 */
public class WidgetExpanderItem {
    private String widgetName;
    private Boolean isExpander;
    private Integer regionIndex;
    private String expanderString;
    private String clickedDimensionString;

    public WidgetExpanderItem(String widgetName,
                              boolean isExpander,
                              int regionIndex,
                              String expanderString,
                              String clickedDimensionString) {
        this.widgetName = widgetName;
        this.isExpander = isExpander;
        this.regionIndex = regionIndex;
        this.expanderString = expanderString;
        this.clickedDimensionString = clickedDimensionString;
    }

    public WidgetExpanderItem() {

    }

    public String getWidgetName() {
        return widgetName;
    }

    public String getClickedDimensionString() {
        return clickedDimensionString;
    }

    public Boolean getIsExpander() {
        return isExpander;
    }

    public Integer getRegionIndex() {
        return regionIndex;
    }

    public String getExpanderString() {
        return expanderString;
    }

    public Element createXML(Document document, Element expand) throws Exception {

        Element widgetNameElement = document.createElement("widgetName");
        Element isExpanderElement = document.createElement("isExpander");
        Element regionIndexElement = document.createElement("regionIndex");
        Element expanderStringElement = document.createElement("expanderString");
        Element clickedDimensionStringElement = document.createElement("clickedDimensionString");
        widgetNameElement.setTextContent(this.widgetName);
        isExpanderElement.setTextContent(this.isExpander.toString());
        regionIndexElement.setTextContent(this.regionIndex.toString());
        expanderStringElement.setTextContent(this.expanderString);
        clickedDimensionStringElement.setTextContent(this.clickedDimensionString);
        expand.appendChild(widgetNameElement);
        expand.appendChild(isExpanderElement);
        expand.appendChild(regionIndexElement);
        expand.appendChild(expanderStringElement);
        expand.appendChild(clickedDimensionStringElement);
        return expand;
    }

    public void initial(Element expand) {
        this.widgetName = expand.getElementsByTagName("widgetName").item(0).getTextContent();
        this.isExpander = Boolean.parseBoolean(expand.getElementsByTagName("isExpander").item(0).getTextContent());
        this.regionIndex = Integer.parseInt(expand.getElementsByTagName("regionIndex").item(0).getTextContent());
        this.expanderString = expand.getElementsByTagName("expanderString").item(0).getTextContent();
        this.clickedDimensionString = expand.getElementsByTagName("clickedDimensionString").item(0).getTextContent();
    }

}