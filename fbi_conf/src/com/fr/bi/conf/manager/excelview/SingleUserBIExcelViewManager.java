package com.fr.bi.conf.manager.excelview;

import com.fr.bi.conf.manager.excelview.source.ExcelViewSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Young's on 2016/4/20.
 */
public class SingleUserBIExcelViewManager{
    private static final String XML_TAG = "SingleUserBIExcelViewManager";

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

    public void clear() {
        synchronized (views) {
            views.clear();
        }
    }



//    @Override
//    public void readXML(XMLableReader reader) {
//        if(reader.isChildNode()) {
//            String tagName= reader.getTagName();
//            if(ComparatorUtils.equals("excel_view", tagName)) {
//                String tableId = reader.getAttrAsString("tableId", StringUtils.EMPTY);
//                ExcelViewSource source = new ExcelViewSource();
//                reader.readXMLObject(source);
//                this.views.put(tableId, source);
//            }
//        }
//    }
//
//    @Override
//    public void writeXML(XMLPrintWriter writer) {
//        writer.startTAG(XML_TAG);
//        writer.attr(BIBaseConstant.VERSIONTEXT, BIBaseConstant.VERSION);
//        Iterator<Map.Entry<String, ExcelViewSource>> iterator = this.views.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, ExcelViewSource> map = iterator.next();
//            String tableId = map.getKey();
//            ExcelViewSource source = map.getValue();
//            writer.startTAG("excel_view");
//            writer.attr("tableId", tableId);
//            source.writeXML(writer);
//            writer.end();
//        }
//    }
}
