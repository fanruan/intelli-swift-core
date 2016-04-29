/**
 * 选择文本字段
 *
 * Created by GUY on 2015/11/10.
 * @class BI.SelectNumberTab
 * @extends BI.Widget
 */
BI.SelectNumberTab = BI.inherit(BI.Widget, {

    constants: {
        DETAIL_TAB_HEIGHT: 40
    },

    _defaultConfig: function () {
        return BI.extend(BI.SelectNumberTab.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-number",
            wId: ""
        });
    },

    _init: function () {
        BI.SelectNumberTab.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var tab = BI.createWidget({
            type: "bi.select_data_tab",
            element: this.element,
            searcher: {
                type: "bi.select_number_pane",
                wId: o.wId
            }
        });
    },

    setValue: function (v) {

    },

    getValue: function () {

    }
});
BI.SelectNumberTab.EVENT_CHANGE = "SelectNumberTab.EVENT_CHANGE";
$.shortcut('bi.select_number', BI.SelectNumberTab);