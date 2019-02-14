package com.fr.swift.conf.business.table2source.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.conf.business.table2source.TableToSource;

/**
 * @author yee
 * @date 2018/3/23
 */
public class TableToSourceUnique extends UniqueKey implements TableToSource {

    private Conf<String> tableId = Holders.simple(StringUtils.EMPTY);
    private Conf<String> sourceKey = Holders.simple(StringUtils.EMPTY);

    public TableToSourceUnique() {
    }

    public TableToSourceUnique(String tableId, String sourceKey) {
        this.tableId.set(tableId);
        this.sourceKey.set(sourceKey);
    }

    @Override
    public String getTableId() {
        return tableId.get();
    }

    @Override
    public String getSourceKey() {
        return sourceKey.get();
    }

    @Override
    public void setTableId(String tableId) {
        this.tableId.set(tableId);
    }

    @Override
    public void setSourceKey(String sourceKey) {
        this.sourceKey.set(sourceKey);
    }
}
