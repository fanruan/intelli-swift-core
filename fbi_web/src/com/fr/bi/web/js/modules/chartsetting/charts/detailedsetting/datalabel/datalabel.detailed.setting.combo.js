/**
 * 数据标签详细设置
 * Created by AstronautOO7 on 2016/11/24.
 */
BI.DataLabelDetailedSettingCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.DataLabelDetailedSettingCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-label-detailed-setting",
            width: 50
        })
    },

    _init: function() {
        BI.DataLabelDetailedSettingCombo.superclass._init.apply(this, arguments);
        var self = this;

        this.popup = BI.createWidget({
            type: "bi.data_label_detailed_setting_popup"
        });
        this.popup.on(BI.TooltipDetailedSettingPopup.EVENT_CHANGE, function() {
            self.fireEvent(BI.DataLabelDetailedSettingCombo.EVENT_CHANGE)
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
BI.DataLabelDetailedSettingCombo.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.data_label_detailed_setting_combo", BI.DataLabelDetailedSettingCombo);