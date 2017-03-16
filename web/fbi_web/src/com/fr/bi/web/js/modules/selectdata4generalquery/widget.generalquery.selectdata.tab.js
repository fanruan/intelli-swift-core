/**
 * Created by Young's on 2016/5/9.
 */
BI.GeneralQuerySelectDataTab = BI.inherit(BI.Widget, {
    
    _constants: {
        SHOW_PACK_FIELDS: 1,
        SHOW_USED_FIELDS: 2
    },
    
    _defaultConfig: function(){
        return BI.extend(BI.GeneralQuerySelectDataTab.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-general-query-select-data-tab"
        })
    },

    _init: function() {
        BI.GeneralQuerySelectDataTab.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            tab: {
                type: "bi.line_segment",
                items: [{
                    text: BI.i18nText("BI-Package_Field"),
                    value: this._constants.SHOW_PACK_FIELDS
                }, {
                    text: BI.i18nText("BI-All_In_Used_Fields"),
                    value: this._constants.SHOW_USED_FIELDS
                }],
                height: 40
            },
            cardCreator: BI.bind(this._createTabs, this)
        });
        tab.on(BI.Tab.EVENT_CHANGE, function () {
            if (this.getSelect() === BICst.DETAIL_DIMENSION_REUSE) {
                self.usedPane.populate();
            }
        });
        tab.setSelect(BICst.DETAIL_PACKAGES_FIELD);
    },

    _createTabs: function (v) {
        var self = this;
        var o = this.options;
        switch (v) {
            case this._constants.SHOW_PACK_FIELDS:
                var selectPane = BI.createWidget({
                    type: "bi.general_query_select_data_pane"
                });
                selectPane.on(BI.GeneralQuerySelectDataPane.EVENT_CLICK_ITEM, function(){
                   self.fireEvent(BI.GeneralQuerySelectDataTab.EVENT_CHANGE, arguments);
                });
                return selectPane;
            case this._constants.SHOW_USED_FIELDS:
                this.usedPane = BI.createWidget({
                    type: "bi.general_query_used_fields_pane"
                });
                this.usedPane.on(BI.GeneralQueryUsedFieldsPane.EVENT_CHANGE, function(){
                     self.fireEvent(BI.GeneralQuerySelectDataTab.EVENT_CHANGE, arguments);
                });
                return this.usedPane;
        }
    }
});
BI.GeneralQuerySelectDataTab.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.general_query_select_data_tab", BI.GeneralQuerySelectDataTab);