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

        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            width: this.options.width,
            el: {
                type: "bi.detailed_setting_trigger"
            },
            popup: {
                el:  {
                    type: "bi.tooltip_detailed_setting_popup",
                    onChange: function() {
                        self.fireEvent(BI.TooltipDetailedSettingCombo.EVENT_CHANGE);
                    }
                },
                minWidth: 350,
                stopPropagation: false
            }
        })
    },

    setValue: function(v) {
        this.combo.setValue(v)
    },

    getValue: function() {
        return this.combo.getValue()
    }
});
BI.TooltipDetailedSettingCombo.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.tooltip_detailed_setting_combo", BI.TooltipDetailedSettingCombo);