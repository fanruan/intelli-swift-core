/**
 * 数据点提示详细设置
 * Created by AstronautOO7 on 2016/10/12.
 */
BI.TooltipDetailedSettingCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.TooltipDetailedSettingCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-tooltip-detailed-setting",
            width: 50
        })
    },

    _init: function() {
        BI.TooltipDetailedSettingCombo.superclass._init.apply(this, arguments);
        var self = this;

        this.popup = BI.createWidget({
            type: "bi.tooltip_detailed_setting_popup"
        });
        this.popup.on(BI.TooltipDetailedSettingPopup.EVENT_CHANGE, function() {
            self.fireEvent(BI.TooltipDetailedSettingCombo.EVENT_CHANGE)
        });

        BI.createWidget({
            type: "bi.combo",
            element: this.element,
            width: this.options.width,
            el: {
                type: "bi.detailed_setting_trigger"
            },
            popup: {
                el:  this.popup,
                minWidth: 350,
                stopPropagation: false
            }
        })
    },

    setValue: function(v) {
        this.popup.setValue(v)
    },

    getValue: function() {
        return this.popup.getValue()
    }
});
BI.TooltipDetailedSettingCombo.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.tooltip_detailed_setting_combo", BI.TooltipDetailedSettingCombo);