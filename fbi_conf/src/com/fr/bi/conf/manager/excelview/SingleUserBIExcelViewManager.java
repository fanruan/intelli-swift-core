package com.fr.bi.conf.manager.excelview;

import com.fr.bi.conf.manager.excelview.source.ExcelViewSource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Young's on 2016/4/20.
 */
public class SingleUserBIExcelViewManager implements Serializable{
    private static final String XML_TAG = "SingleUserBIExcelViewManager";
    private static final long serialVersionUID = 4618800018790779538L;

    private Map<String, ExcelViewSource> views = new HashMap<String, ExcelViewSource>();

    public Map<String, ExcelViewSource> getViews() {
        return views;
    }

    public void setViews(Map<String, ExcelViewSource> views) {
        this.views = views;
    }

    public void saveExcelView(String tableId, ExcelViewSource source) {
        this.views.put(tableId, source);
    }

    public ExcelViewSource getExcelViewByTableId(String tableId) {
        return this.views.get(tableId);
    }

    public void clear() {
        synchronized (views) {
            views.clear();
        }
    }
}
