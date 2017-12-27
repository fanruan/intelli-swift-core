package com.finebi.base.data;


import com.finebi.base.data.xml.item.XmlItem;

/**
 * Created by andrew_asa on 2017/10/3.
 */
public class Field {

    @XmlItem
    String name;

    @XmlItem
    String id;

    @XmlItem
    int fieldType;

    @XmlItem
    boolean isUser;

    public Field(String name, String id, int fieldType, boolean isUser) {

        this.name = name;
        this.id = id;
        this.fieldType = fieldType;
        this.isUser = isUser;
    }

    public Field() {

    }

    public String getName() {

        return name;
    }

    public String getId() {

        return id;
    }

    public int getFieldType() {

        return fieldType;
    }

    public boolean getIsUser() {

        return isUser;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setFieldType(int fieldType) {

        this.fieldType = fieldType;
    }

    public void setIsUser(boolean user) {

        isUser = user;
    }

}
