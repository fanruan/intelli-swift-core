BI.Plugin = BI.Plugin || {}

BI.Plugin.registerWidget("bi.detail_detail_select_data_level0_node", function(el){
    if (BI.Utils.getTableTypeByID(el['id']) == ETLCst.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE){
        return BI.extend({}, el, {type: "bi.analysis_detail_detail_select_data_level0_node"});
    }
});
BI.Plugin.registerWidget("bi.detail_select_data_level0_node", function(el){
    if (BI.Utils.getTableTypeByID(el['id']) == ETLCst.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE){
        return BI.extend({}, el, {type: "bi.analysis_detail_select_data_level0_node"});
    }
});

BI.Plugin.registerWidget("bi.detail_detail_select_data_level0_node", function(el){
    if (BI.Utils.getTableTypeByID(el['id']) == ETLCst.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE){
        return BI.extend({}, el, {type: "bi.select_data_level" + ETLCst.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE + "_node"});
    }
});

BI.Plugin.registerWidget("bi.data_style_tab", function(el){
    return BI.extend({}, el, {type: "bi.data_style_tab_etl"});
});