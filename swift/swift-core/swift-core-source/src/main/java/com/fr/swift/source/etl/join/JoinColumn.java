package com.fr.swift.source.etl.join;

import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.source.core.CoreService;

/**
 * Created by Handsome on 2017/12/8 0008 11:39
 */
public class JoinColumn implements CoreService {


    //etl之后的字段名
    @CoreField
    private String name;
    @CoreField
    private boolean isLeft;
    //父表字段名
    @CoreField
    private String columnName;
    private Core core;

    public JoinColumn(String name, boolean isLeft, String columnName) {
        this.name = name;
        this.isLeft = isLeft;
        this.columnName = columnName;
    }

    public JoinColumn() {

    }

    public String getName() {
        return name;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public Object clone() {
        return null;
    }

    @Override
    public Core fetchObjectCore() {
        if (core == null) {
            core = new CoreGenerator(this).fetchObjectCore();
        }
        return core;
    }
}
