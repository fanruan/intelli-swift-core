package com.fr.swift.source.etl.groupsum;

import com.fr.general.ComparatorUtils;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.group.Group;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.source.core.CoreService;

import java.io.Serializable;

/**
 * Created by Handsome on 2017/12/8 0008 14:36
 */
public class SumByGroupDimension implements CoreService, Serializable {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SumByGroupDimension.class);
    //维度字段名
    @CoreField
    private String name;
    @CoreField
    private Group group;
    //改名之后的字段，不需要算md5
    private String nameText;
    @CoreField
    private ColumnType columnType;

    public String getName() {
        return name;
    }

    public String getNameText() {
        return nameText;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameText(String nameText) {
        this.nameText = nameText;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Object getKeyValue(Object value) {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SumByGroupDimension)) {
            return false;
        }

        SumByGroupDimension that = (SumByGroupDimension) o;

        if (group != null ? !ComparatorUtils.equals(group, that.group) : that.group != null) {
            return false;
        }
        if (name != null ? !ComparatorUtils.equals(name, that.name) : that.name != null) {
            return false;
        }
        return nameText != null ? ComparatorUtils.equals(nameText, that.nameText) : that.nameText == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (nameText != null ? nameText.hashCode() : 0);
        return result;
    }



    @Override
    public Core fetchObjectCore() {
        try {
            return new CoreGenerator(this).fetchObjectCore();
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Core.EMPTY_CORE;
    }


    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

}
