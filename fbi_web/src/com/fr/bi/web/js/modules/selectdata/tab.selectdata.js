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
            type: "bi.tab",
            element: this.element,
            tab: {
                type: "bi.line_segment",
                items: BICst.DETAIL_FIELD_REUSE_TAB,
                height: this.constants.DETAIL_TAB_HEIGHT
            },
            cardCreator: BI.bind(this._createTabs, this)
        });
        tab.on(BI.Tab.EVENT_CHANGE, function () {
            this.populate();
        });
        BI.Broadcasts.on(BICst.BROADCAST.DETAIL_EDIT_PREFIX + o.wId, function () {
            tab.setSelect(BICst.DETAIL_PACKAGES_FIELD);
        });
        tab.setSelect(BICst.DETAIL_PACKAGES_FIELD);
    },

    _createTabs: function (v) {
        var o = this.options;
        switch (v) {
            case BICst.DETAIL_PACKAGES_FIELD:
                return this.selectPane = BI.createWidget({
                    type: "bi.detail_select_data_pane"
                });
            case  BICst.DETAIL_DIMENSION_REUSE:
                return this.reusePane = BI.createWidget({
                    type: "bi.detail_select_dimension_data_pane",
                    wId: o.wId
                })
        }
    }
});
BI.DetailSelectData.EVENT_CHANGE = "DetailSelectData.EVENT_CHANGE";
$.shortcut('bi.detail_select_data', BI.DetailSelectData);