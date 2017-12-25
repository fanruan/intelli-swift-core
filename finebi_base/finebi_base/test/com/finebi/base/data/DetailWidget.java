package com.finebi.base.data;


import com.finebi.base.data.xml.item.XmlItem;

/**
 * Created by andrew_asa on 2017/10/11.
 */
public class DetailWidget extends AbstractBiWidget{

    @XmlItem
    String detailName;

    public DetailWidget() {

    }

    public String getDetailName() {

        return detailName;
    }

    public void setDetailName(String detailName) {

        this.detailName = detailName;
    }
}
