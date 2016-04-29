/**
 * 选择文本字段
 *
 * Created by GUY on 2015/11/10.
 * @class BI.SelectStringTab
 * @extends BI.Widget
 */
BI.SelectStringTab = BI.inherit(BI.Widget, {

    constants: {
        DETAIL_TAB_HEIGHT: 40
    },

    _defaultConfig: function () {
        return BI.extend(BI.SelectStringTab.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-string"
        });
    },

    _init: function () {
        BI.SelectStringTab.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var tab = BI.createWidget({
            type: "bi.select_data_tab",
            element: this.element,
            searcher: {
                type: "bi.select_string_pane"
            }
        });
    },

    setValue: function (v) {

    },

    getValue: function () {

    }
});
BI.SelectStringTab.EVENT_CHANGE = "SelectStringTab.EVENT_CHANGE";
$.shortcut('bi.select_string', BI.SelectStringTab);