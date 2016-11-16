/**
 * Created by zcf on 2016/11/15.
 */
BI.DimensionSwitchPopupShow = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DimensionSwitchPopupShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-switch-popup-show",
            wId: "",
            dimensionCreator: BI.emptyFn()
        });
    },

    _init: function () {
        BI.DimensionSwitchPopupShow.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.pane = BI.createWidget({
            type: "bi.dimension_switch_pane_show",
            wId: o.wId,
            dimensionCreator: o.dimensionCreator
        });
        this.pane.on(BI.DimensionSwitchPaneShow.EVENT_CHANGE, function () {
            self.fireEvent(BI.DimensionSwitchPopupShow.EVENT_CHANGE);
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.pane,
                top: 0,
                left: 0,
                width: 260,
                height: 300
            }]
        })
    },

    getValue: function () {
        return this.pane.getValue();
    },

    populate: function () {
        this.pane.populate();
    }
});
BI.DimensionSwitchPopupShow.EVENT_CHANGE = "BI.DimensionSwitchPopupShow.EVENT_CHANGE";
$.shortcut("bi.dimension_switch_popup_show", BI.DimensionSwitchPopupShow);