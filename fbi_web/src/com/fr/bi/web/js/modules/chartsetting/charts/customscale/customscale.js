/**
 * custom maxScale,minScale,interval
 * Created by AstronautOO7 on 2016/8/4.
 */
BI.CustomScale = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomScale.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-scale"
        })
    },

    _init: function () {
        BI.CustomScale.superclass._init.apply(this, arguments);
        var self = this;

        this.minScale = BI.createWidget({
            type: "bi.combo_custom_scale",
            text: BI.i18nText("BI-Min")
        });

        this.minScale.on(BI.ComboCustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.CustomScale.EVENT_CHANGE)
        });

        this.maxScale = BI.createWidget({
            type: "bi.combo_custom_scale",
            text: BI.i18nText("BI-Max")
        });

        this.maxScale.on(BI.ComboCustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.CustomScale.EVENT_CHANGE)
        });

        this.interval = BI.createWidget({
            type: "bi.combo_custom_scale",
            text: BI.i18nText("BI-Interval_Number")
        });

        this.interval.on(BI.ComboCustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.CustomScale.EVENT_CHANGE)
        });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.minScale, this.maxScale, this.interval]
        });
    },

    getValue: function () {
        return {
            minScale: this.minScale.getValue(),
            maxScale: this.maxScale.getValue(),
            interval: this.interval.getValue()
        }
    },

    setValue: function (v) {
        this.minScale.setValue(v.minScale);
        this.maxScale.setValue(v.maxScale);
        this.interval.setValue(v.interval)
    }
});
BI.CustomScale.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.custom_scale", BI.CustomScale);