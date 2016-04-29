package com.fr.bi.common.persistent.writer;

/**
 * Created by Connery on 2015/12/31.
 */
public class MainPart {
    private ArrayPart arrayPart;
    private IterablePart iterablePart;
    private MapPart mapPart;
    private NormalPart normalPart;
    private XMLAblePart xmlAblePart;

    public ArrayPart getArrayPart() {
        return arrayPart;
    }

    public void setArrayPart(ArrayPart arrayPart) {
        this.arrayPart = arrayPart;
    }

    public IterablePart getIterablePart() {
        return iterablePart;
    }

    public void setIterablePart(IterablePart iterablePart) {
        this.iterablePart = iterablePart;
    }

    public MapPart getMapPart() {
        return mapPart;
    }

    public void setMapPart(MapPart mapPart) {
        this.mapPart = mapPart;
    }

    public NormalPart getNormalPart() {
        return normalPart;
    }

    public void setNormalPart(NormalPart normalPart) {
        this.normalPart = normalPart;
    }

    public XMLAblePart getXmlAblePart() {
        return xmlAblePart;
    }

    public void setXmlAblePart(XMLAblePart xmlAblePart) {
        this.xmlAblePart = xmlAblePart;
    }
}