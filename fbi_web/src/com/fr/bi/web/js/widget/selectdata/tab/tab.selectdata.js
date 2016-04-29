/**
 * 选择字段tab
 *
 * Created by GUY on 2015/11/10.
 * @class BI.SelectDataTab
 * @extends BI.Widget
 */
BI.SelectDataTab = BI.inherit(BI.Widget, {

    constants: {
        DETAIL_TAB_HEIGHT: 40
    },

    _defaultConfig: function () {
        return BI.extend(BI.SelectDataTab.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-data-tab",
            searcher: {},
            reuse: {}
        });
    },

    _init: function () {
        BI.SelectDataTab.superclass._init.apply(this, arguments);
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
        tab.on(BI.Tab.EVENT_CHANGE, function(){
            if(this.getSelect() === BICst.DETAIL_DIMENSION_REUSE){
                self.reusePane.populate();
            }
        });
        tab.setSelect(BICst.DETAIL_PACKAGES_FIELD);
    },

    _createTabs: function (v) {
        var o = this.options;
        switch (v) {
            case BICst.DETAIL_PACKAGES_FIELD:
                return this.selectPane = BI.createWidget(o.searcher, {
                    type: "bi.select_data_searcher"
                });
            case  BICst.DETAIL_DIMENSION_REUSE:
                return this.reusePane = BI.createWidget(o.reuse, {
                    type: "bi.label",
                    text: "Reuse Dimension"
                })
        }
    },

    setValue: function (v) {

    },

    getValue: function () {

    }
});
BI.SelectDataTab.EVENT_CHANGE = "SelectDataTab.EVENT_CHANGE";
$.shortcut('bi.select_data_tab', BI.SelectDataTab);