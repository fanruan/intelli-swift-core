BI.Plugin = BI.Plugin || {};
BI.Plugin.registerWidget("bi.detail_detail_select_data_level0_node", function(el){
    if (BI.Utils.getSQLTableTypeByID(el['id']) == SQLCst.BUSINESS_TABLE_TYPE.ANALYSIS_SQL_TYPE){
        return BI.extend({}, el, {type: "bi.analysis_detail_detail_select_data_level0_node"});
    }
});
BI.Plugin.registerWidget("bi.detail_select_data_level0_node", function(el){
    if (BI.Utils.getSQLTableTypeByID(el['id']) == SQLCst.BUSINESS_TABLE_TYPE.ANALYSIS_SQL_TYPE){
        return BI.extend({}, el, {type: "bi.analysis_detail_select_data_level0_node"});
    }
});

BI.Plugin.registerWidget("bi.detail_detail_select_data_level0_node", function(el){
    if (BI.Utils.getSQLTableTypeByID(el['id']) == SQLCst.BUSINESS_TABLE_TYPE.ANALYSIS_SQL_TYPE){
        return BI.extend({}, el, {type: "bi.select_data_level" + SQLCst.BUSINESS_TABLE_TYPE.ANALYSIS_SQL_TYPE + "_node"});
    }
});
BI.Plugin.DATA_STYLE_TAB_ITEM = BI.Plugin.DATA_STYLE_TAB_ITEM || [];
(function () {
    BI.Plugin.DATA_STYLE_TAB_ITEM.push({
        el:{
            type : 'bi.left_pointer_button_sql',
            pointerWidth : SQLCst.ENTERBUTTON.POINTERWIDTH,
            title:BI.i18nText("sql"),
            iconCls : "icon-add",
            height : SQLCst.ENTERBUTTON.HEIGHT,
            width : SQLCst.ENTERBUTTON.WIDTH,
            text: BI.i18nText('sql'),
            handler:function () {
                BI.createWidget({
                    type : "bi.analysis_etl_main",
                    element:BI.Layers.create(SQLCst.ANALYSIS_LAYER, "body")
                })
            }
        },
        left:200,
        top:SQLCst.ENTERBUTTON.GAP,
        bottom:0,
        right:0
    });
    var registered = BI.Plugin.getWidget("bi.data_style_tab");
    if (BI.isNull(registered)){
        BI.Plugin.registerWidget("bi.data_style_tab", function(el){
            var tabEl = BI.extend({}, el, { type :"bi.data_style_tab_sql"});
            var items = [{
                el: tabEl,
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            }];
            return BI.extend({}, el, {type: "bi.absolute", items: BI.concat(items, BI.Plugin.DATA_STYLE_TAB_ITEM)});
        });
    }
})();