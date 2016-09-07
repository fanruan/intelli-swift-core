/**
 * 带一个按钮的 styletabpane
 * @class BI.DataStyleTabPlugin
 * @extend BI.Widget
 * @type {*|void}
 */

BI.SQLDataStyleTab = BI.inherit(BI.DataStyleTab, {
    _init: function(){
        BI.SQLDataStyleTab.superclass._init.apply(this, arguments);
    }
})
$.shortcut("bi.data_style_tab_sql", BI.SQLDataStyleTab);