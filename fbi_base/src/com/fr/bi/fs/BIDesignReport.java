package com.fr.bi.fs;

import com.fr.base.BaseXMLUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Created by 小灰灰 on 2015/7/16.
 */
public class BIDesignReport implements Serializable{
    private static final long serialVersionUID = 1654906613327753967L;
    private BIDesignSetting setting;

    public BIDesignReport(BIDesignSetting var1) {
        this.setting = var1;
    }

    protected void save(OutputStream var1) throws Exception {
        if (this.setting != null) {
            BaseXMLUtils.writeXMLFile(var1, this.setting);
        } else {
            throw new RuntimeException("setting is null");
        }
    }

    public void writeFile(File var1) throws Exception {
        FileOutputStream var2 = new FileOutputStream(var1);
        this.save(var2);
        var2.flush();
        var2.close();
    }

    public String getSuffix() {
        return ".fbi";
    }
}