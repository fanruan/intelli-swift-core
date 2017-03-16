package com.fr.bi.stable.data.db;

import com.fr.json.JSONCreator;

import java.io.Serializable;
import java.util.List;

/**
 * This class created on 2016/5/20.
 *
 * @author Connery
 * @since 4.0
 */
public interface IPersistentTable extends Serializable, JSONCreator {
    void addColumn(PersistentField column);

    String getSchema();

    String getTableName();

    String getRemark();

    List<PersistentField> getFieldList();

    int getFieldSize();

    PersistentField getField(int index);

    PersistentField getField(String name);

    int getFieldIndex(String name);
}
