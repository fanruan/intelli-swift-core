package com.fr.swift.config.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.config.MetaDataRCode;

/**
 * Created by Handsome on 2018/3/29 0030 17:16
 */
public class MetaDataRCodeConfig extends UniqueKey implements MetaDataRCode {

    private final static String NAMESPACE = "metadata";

    private Conf<String> tableId = Holders.simple(StringUtils.EMPTY);
    private Conf<String> rCode = Holders.simple(StringUtils.EMPTY);

    public MetaDataRCodeConfig() {}

    public MetaDataRCodeConfig(String tableId, String rCode) {
        this.setTableId(tableId);
        this.setRCode(rCode);
    }

    @Override
    public String getTableId() {
        return this.tableId.get();
    }

    @Override
    public void setTableId(String tableId) {
        this.tableId.set(tableId);
    }

    @Override
    public String getRCode() {
        return this.rCode.get();
    }

    @Override
    public void setRCode(String rCode) {
        this.rCode.set(rCode);
    }
}
