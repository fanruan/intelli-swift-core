/**
 * @class BI.ChartSettingSelectColorPopup
 * @extends BI.Widget
 */
BI.ChartSettingSelectColorPopup = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ChartSettingSelectColorPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-setting-select-color-popup",
            height: 145
        })
    },

    _init: function () {
        BI.ChartSettingSelectColorPopup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.popup = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.popup.on(BI.Controller.EVENT_CHANGE, function(type, value, obj){
            self.setValue(value);
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
    },

    populate: function(items){
        this.popup.populate(BI.createItems(items, {
            type: "bi.chart_setting_wrap_select_color_item"
        }));
    },

    setValue: function (v) {
        BI.each(this.popup.getAllButtons(), function (i, item) {
            if (BI.isEqual(v, item.getValue())) {
                item.setSelected && item.setSelected(true);
            } else {
                item.setSelected && item.setSelected(false);
            }
        });
    },

    getValue: function () {
        return this.popup.getValue();
    }
});
BI.ChartSettingSelectColorPopup.EVENT_CHANGE = "ChartSettingSelectColorPopup.EVENT_CHANGE";
$.shortcut("bi.chart_setting_select_color_popup", BI.ChartSettingSelectColorPopup);