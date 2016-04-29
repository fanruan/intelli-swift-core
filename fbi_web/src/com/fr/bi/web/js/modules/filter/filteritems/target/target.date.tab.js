/**
 * @class BI.TargetDateTab
 * @extend BI.Widget
 * combo : text + icon, popup : text
 * 参见场景dashboard布局方式选择
 */
BI.TargetDateTab = BI.inherit(BI.Widget, {

    constants: {
        defaultShowWidget: 0,
        comboHeight: 30,
        comboWidth: 100
    },

    _defaultConfig: function () {
        return BI.extend(BI.TargetDateTab.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-date-tab",
            dateWidgetType: []
        })
    },

    _init: function () {
        BI.TargetDateTab.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var filterDateWidgetTypes = o.dateWidgetType;
        var dateWidgetIds = BI.filter(BI.Utils.getAllWidgetIDs(), function(id, wid){
            return BI.contains(filterDateWidgetTypes, BI.Utils.getWidgetTypeByID(wid));
        });

        var widgetItems = BI.map(dateWidgetIds, function(id, w){
            return {
                text: BI.Utils.getWidgetNameByID(w),
                value: w
            };
        });

        widgetItems = BI.sortBy(widgetItems, "text");

        var combo = BI.createWidget({
            type: "bi.text_icon_combo",
            height: this.constants.comboHeight,
            items: widgetItems
        });

        this.tab = BI.createWidget({
            type: "bi.tab",
            width: this.constants.comboWidth,
            height: this.constants.comboHeight,
            direction: "custom",
            tab: combo,
            cardCreator: BI.bind(this._cardCreator, this)
        });

        this.tab.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.tab.on(BI.Tab.EVENT_CHANGE, function(){
            self.fireEvent(BI.TargetDateTab.EVENT_CHANGE);
        });

        this.tab.setSelect(0);

        BI.createWidget({
            type: "bi.inline",
            element: this.element,
            items: [combo, {
                type: "bi.label",
                text: BI.i18nText("BI-De"),
                textAlign: "center",
                height: 30,
                lgap: 5,
                rgap: 5
            }, this.tab]
        });
    },

    _cardCreator: function(v){
        var self = this;
        var ids = BI.Utils.getAllWidgetIDs();
        if(!BI.contains(ids, v)){
            return BI.createWidget({
                type: "bi.text_icon_combo",
                height: this.constants.comboHeight,
                items: []
            });
        }
        switch (BI.Utils.getWidgetTypeByID(v)) {
            case BICst.Widget.YEAR:
                this.yearCombo = BI.createWidget({
                    type: "bi.year_param_combo"
                });
                this.yearCombo.on(BI.YearParamCombo.EVENT_CONFIRM, function(){
                    self.fireEvent(BI.TargetDateTab.EVENT_SHOW_CARD_VALUE_CHANGE);
                });
                return this.yearCombo;
            case BICst.Widget.MONTH:
                this.monthCombo = BI.createWidget({
                    type: "bi.year_month_param_combo"
                });
                this.monthCombo.on(BI.YearParamCombo.EVENT_CONFIRM, function(){
                    self.fireEvent(BI.TargetDateTab.EVENT_SHOW_CARD_VALUE_CHANGE);
                });
                return this.monthCombo;
            case BICst.Widget.QUARTER:
                this.qurterCombo = BI.createWidget({
                    type: "bi.year_season_param_combo"
                });
                this.qurterCombo.on(BI.YearSeasonParamCombo.EVENT_CONFIRM, function(){
                    self.fireEvent(BI.TargetDateTab.EVENT_SHOW_CARD_VALUE_CHANGE);
                });
                return this.qurterCombo;
            case BICst.Widget.YMD:
                this.dateCombo = BI.createWidget({
                    type: "bi.date_param_combo"
                });
                this.dateCombo.on(BI.YearParamCombo.EVENT_CONFIRM, function(){
                    self.fireEvent(BI.TargetDateTab.EVENT_SHOW_CARD_VALUE_CHANGE);
                });
                return this.dateCombo;
            case BICst.Widget.DATE:
                this.dateRangeCombo = BI.createWidget({
                    type: "bi.range_value_combo",
                    height: this.constants.comboHeight
                });
                this.dateRangeCombo.on(BI.RangeValueCombo.EVENT_CHANGE, function(){
                    self.fireEvent(BI.TargetDateTab.EVENT_SHOW_CARD_VALUE_CHANGE);
                });
                return this.dateRangeCombo;
        }
    },

    setValue: function (v) {
        this.tab.setSelect(v.wId);
        this.tab.setValue(v.filter_value);
    },

    getValue: function () {
        return {
            wId: this.tab.getSelect(),
            filter_value: this.tab.getValue()
        };
    }
});
BI.TargetDateTab.EVENT_CHANGE = "EVENT_CHANGE";
BI.TargetDateTab.EVENT_SHOW_CARD_VALUE_CHANGE = "EVENT_SHOW_CARD_VALUE_CHANGE";
$.shortcut("bi.target_date_tab", BI.TargetDateTab);