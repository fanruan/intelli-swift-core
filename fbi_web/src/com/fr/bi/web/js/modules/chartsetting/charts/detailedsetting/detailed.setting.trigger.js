/**
 * 文本trigger
 * Created by AstronautOO7 on 2016/9/28.
 */
BI.DetailedSettingTrigger = BI.inherit(BI.Trigger, {

    _defaultConfig: function () {
        var conf = BI.DetailedSettingTrigger.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-detailed-setting-trigger",
            text: BI.i18nText("BI-Set_Details"),
            height: 30
        });
    },

    _init: function () {
        BI.DetailedSettingTrigger.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.text = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            height: o.height,
            text: o.text
        });

        BI.createWidget({
            element: this.element,
            type: 'bi.htape',
            items: [this.text]
        });
    },

    setEnable: function (v) {
        BI.DetailedSettingTrigger.superclass.setEnable.apply(this, arguments);
        this.text.setEnable(v);
    },

    setValue: function (value) {
        this.text.setValue(value);
        this.text.setTitle(value);
    },

    setText: function (text) {
        this.text.setText(text);
        this.text.setTitle(text);
    }
});
$.shortcut("bi.detailed_setting_trigger", BI.DetailedSettingTrigger);