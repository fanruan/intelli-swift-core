BI.AngleDetailedSettingCombo = BI.inherit(BI.BasicButton, {
    _defaultConfig: function () {
        return BI.extend(BI.AngleDetailedSettingCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls : 'bi-segment-button',
        });
    },
    
    _init: function () {
        BI.AngleDetailedSettingCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.start = BI.createWidget({
            type: "bi.text_editor",
            validationChecker: function () {
                if (!BI.isNumeric(self.start.getValue()) || self.start.getValue() < 0 || self.start.getValue() > self.end.getValue()) {
                    return false;
                }
            },
            value: 0,
            //errorText: BI.i18nText("BI-Please_Input_Integer"),
            //allowBlank: true,
            height: 26,
            width: 80
        });
        this.end = BI.createWidget({
            type: "bi.text_editor",
            validationChecker: function () {
                if (!BI.isNumeric(self.end.getValue()) || self.end.getValue() > 360 || self.end.getValue() < self.end.getValue()) {
                    return false;
                }
            },
            value: 360,
            //errorText: BI.i18nText("BI-Please_Input_Integer"),
            //allowBlank: true,
            height: 26,
            width: 80
        });

        this.popup = BI.createWidget({
            type: "bi.horizontal",
            height: 40,
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Start_Angle"),
                hgap: 5
            }, this.start, {
                type: "bi.label",
                text: BI.i18nText("BI-End_Angle"),
                lgap: 8,
                rgap: 5
            }, this.end]
        });

        this.combo = BI.createWidget({
            type: "bi.combo",
            height: o.height-2,
            hgap: o.hgap,
            el: {
                type: "bi.label",
                text: o.text,
                height: o.height-2
            },
            popup: {
                el: this.popup,
                minWidth: 300,
                stopPropagation: false
            },
            element: this.element
        });
    }
});
$.shortcut("bi.angle_detailed_setting_combo", BI.AngleDetailedSettingCombo);