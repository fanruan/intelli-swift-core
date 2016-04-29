/**
 * 明细表选择字段
 *
 * Created by GUY on 2015/10/13.
 * @class BI.DetailSelectData
 * @extends BI.Widget
 */
BI.DetailSelectData = BI.inherit(BI.Widget, {

    constants: {
        DETAIL_TAB_HEIGHT: 40
    },

    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectData.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-select-data",
            wId: ""
        });
    },

    _init: function () {
        BI.DetailSelectData.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var tab = BI.createWidget({
            type: "bi.select_data_tab",
            element: this.element,
            searcher: {
                type: "bi.detail_select_data_pane"
            },
            reuse: {
                type: "bi.detail_select_dimension_data_pane",
                wId: o.wId
            }
        });
    },

    setValue: function (v) {

    },

    getValue: function () {

    }
});
BI.DetailSelectData.EVENT_CHANGE = "DetailSelectData.EVENT_CHANGE";
$.shortcut('bi.detail_select_data', BI.DetailSelectData);