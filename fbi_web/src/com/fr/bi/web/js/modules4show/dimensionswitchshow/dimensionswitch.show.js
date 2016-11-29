/**
 * Created by zcf on 2016/11/4.
 */
BI.DimensionSwitchShow = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DimensionSwitchShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-switch-show",
            wId: "",
            dimensionCreator: BI.emptyFn
        });
    },

    _init: function () {
        BI.DimensionSwitchShow.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        var button = BI.createWidget({
            type: "bi.icon_button",
            width: 20,
            height: 20,
            title: BI.i18nText("BI-Detailed_Setting"),
            cls: "widget-combo-detail-font dashboard-title-detail"
        });

        this.pane = BI.createWidget({
            type: "bi.dimension_switch_popup_show",
            wId: o.wId,
            dimensionCreator: o.dimensionCreator
        });
        this.pane.on(BI.DimensionSwitchPopupShow.EVENT_CHANGE, function () {
            self.fireEvent(BI.DimensionSwitchShow.EVENT_CHANGE);
        });

        var combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            adjustXOffset: -40,
            adjustYOffset: 5,
            direction: "bottom,left",
            el: button,
            tabs: [],
            popup: {
                el: this.pane,
                minWidth: 262,
                minHeight: 300
            }
        });
        combo.on(BI.Combo.EVENT_TRIGGER_CHANGE, function () {
            self.pane.populate();
        });
    },

    getValue: function () {
        return this.pane.getValue();
    }

});
BI.DimensionSwitchShow.EVENT_CHANGE = "BI.DimensionSwitchShow.EVENT_CHANGE";
$.shortcut("bi.dimension_switch_show", BI.DimensionSwitchShow);