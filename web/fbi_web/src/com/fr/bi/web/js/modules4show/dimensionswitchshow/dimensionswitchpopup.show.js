/**
 * Created by zcf on 2016/11/15.
 */
BI.DimensionSwitchPopupShow = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DimensionSwitchPopupShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-switch-popup-show",
            wId: "",
            popupCreator: BI.emptyFn
        });
    },

    _init: function () {
        BI.DimensionSwitchPopupShow.superclass._init.apply(this, arguments);
    },

    populate: function () {
        var self = this, o = this.options;
        if (BI.isNull(this.layout)) {
            var pane = o.popupCreator();
            this.layout = BI.createWidget({
                type: "bi.absolute",
                element: this.element,
                items: [{
                    el: pane,
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0
                }]
            });
        }
    }
});
BI.DimensionSwitchPopupShow.EVENT_CHANGE = "BI.DimensionSwitchPopupShow.EVENT_CHANGE";
$.shortcut("bi.dimension_switch_popup_show", BI.DimensionSwitchPopupShow);