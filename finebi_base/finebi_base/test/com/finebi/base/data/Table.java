package com.finebi.base.data;


import com.finebi.base.data.xml.item.XmlItem;
import com.finebi.base.data.xml.item.XmlListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew_asa on 2017/10/3.
 */
public class Table {

    @XmlItem
    String name;

    @XmlItem
    String id;

    @XmlItem
    int type;

    @XmlItem
    long initTime;

    @XmlListItem(clazz = Field.class)
    List<Field> fields = new ArrayList<Field>();

    public Table(String name, String id, int type, long initTime, List<Field> fields) {

        this.name = name;
        this.id = id;
        this.type = type;
        this.initTime = initTime;
        this.fields = fields;
    }

    public Table() {

    }

    public void addField(Field field) {

        if (fields == null) {
            fields = new ArrayList<Field>();
        }
        fields.add(field);
    }

    public String getName() {

        return name;
    }

    public String getId() {

        return id;
    }

    public int getType() {

        return type;
    }

    public long getInitTime() {

        return initTime;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setId(String id) {

        this.id = id;
    }

    public void setType(int type) {

        this.type = type;
    }

    public void setInitTime(long initTime) {

        this.initTime = initTime;
    }

    public List<Field> getFields() {

        return fields;
    }

    public void setFields(List<Field> fields) {

        this.fields = fields;
    }

}
