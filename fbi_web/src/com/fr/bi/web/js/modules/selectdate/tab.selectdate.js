/**
 * 选择文本字段
 *
 * Created by GUY on 2015/11/10.
 * @class BI.SelectDateTab
 * @extends BI.Widget
 */
BI.SelectDateTab = BI.inherit(BI.Widget, {

    constants: {
        DETAIL_TAB_HEIGHT: 40
    },

    _defaultConfig: function () {
        return BI.extend(BI.SelectDateTab.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-date"
        });
    },

    _init: function () {
        BI.SelectDateTab.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var tab = BI.createWidget({
            type: "bi.select_data_tab",
            element: this.element,
            searcher: {
                type: "bi.select_date_pane"
            }
        });
    },

    setValue: function (v) {

    },

    getValue: function () {

    }
});
BI.SelectDateTab.EVENT_CHANGE = "SelectDateTab.EVENT_CHANGE";
$.shortcut('bi.select_date', BI.SelectDateTab);