/**
 * 显示标题的详细设置
 * Created by AstronautOO7 on 2016/9/28.
 */
BI.ShowTitleDetailedSettingCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.ShowTitleDetailedSettingCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-title-detailed-setting"
        })
    },

    _init: function() {
        BI.ShowTitleDetailedSettingCombo.superclass._init.apply(this, arguments);
        var self = this;

        var popup = BI.createWidget({
            type: "bi.show_title_detailed_setting_popup"
        });

        BI.createWidget({
            type: "bi.combo",
            element: this.element,
            el: {
                type: "bi.detailed_setting_trigger"
            },
            popup: {
                el:  popup,
                minWidth: 320,
                stopPropagation: false
            }
        })
    }
});
$.shortcut("bi.show_title_detailed_setting_combo", BI.ShowTitleDetailedSettingCombo);