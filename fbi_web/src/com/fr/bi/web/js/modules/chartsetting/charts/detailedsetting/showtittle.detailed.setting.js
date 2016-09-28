/**
 * 显示标题的详细设置
 * Created by AstronautOO7 on 2016/9/28.
 */
BI.ShowTitleDetailedSetting = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.ShowTitleDetailedSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-title-detailed-setting"
        })
    },

    _init: function() {
        BI.ShowTitleDetailedSetting.superclass._init.apply(this, arguments);
        var self = this;

        var textTrigger = BI.createWidget({
            type: "bi.detailed_setting_trigger",
            text: BI.i18nText("BI-Set_Details"),
            width: 200,
            height: 30
        });

        // var popip = BI.createWidget({
        //     type:
        // });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [textTrigger]
        })
    }
});
$.shortcut("bi.show_title_detailed_setting", BI.ShowTitleDetailedSetting);