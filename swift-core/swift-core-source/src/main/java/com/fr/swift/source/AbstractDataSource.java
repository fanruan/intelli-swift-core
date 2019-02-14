package com.fr.swift.source;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2017/11/15.
 */
public abstract class AbstractDataSource implements DataSource {
    protected SourceKey key;
    protected transient SwiftMetaData metaData;
    private transient Core core;

    @Override
    public SourceKey getSourceKey() {
        if (key == null) {
            initSourceKey();
        }
        Util.requireNonNull(key);
        return key;
    }

    protected void initSourceKey() {
        String id = fetchObjectCore().getValue();
        key = new SourceKey(id);
    }

    @Override
    public SwiftMetaData getMetadata() {
        if (metaData == null) {
            initMetaData();
            checkColumnNames();
        }
        return metaData;
    }

    protected void checkColumnNames(){
        try {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            Set<String> names = new HashSet<String>();
            for (int i = 0;i < metaData.getColumnCount(); i++){
                SwiftMetaDataColumn column = metaData.getColumn(i+1);
                if (names.contains(column.getName()) || SwiftConfigConstants.KeyWords.COLUMN_KEY_WORDS.contains(column.getName().toLowerCase())) {
                    String newName = createNewName(names, column.getName());
                    column = new MetaDataColumnBean(newName, column.getRemark(), column.getType(), column.getPrecision(), column.getScale(), column.getColumnId());
                }
                columnList.add(column);
                names.add(column.getName());
            }
            metaData = new SwiftMetaDataBean(metaData.getTableName(), metaData.getRemark(), metaData.getSchemaName(), columnList);
        } catch (Exception ignore){

        }
    }

    protected String createNewName(Set<String> names, String name){
        int i = 0;
        while (names.contains(name)){
            name = name + ++i;
        }
        return name;
    }

    protected abstract void initMetaData();

    @Override
    public Core fetchObjectCore() {
        if (core == null) {
            core = new CoreGenerator(this).fetchObjectCore();
        }
        return core;
    }
}
