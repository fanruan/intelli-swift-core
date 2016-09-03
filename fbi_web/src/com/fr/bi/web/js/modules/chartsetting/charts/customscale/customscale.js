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
        var self = this, o = this.options;

        this.minScale = BI.createWidget({
            type: "bi.combo_custom_scale",
            text: BI.i18nText("BI-Min"),
            wId: o.wId
        });

        this.minScale.on(BI.ComboCustomScale.EVENT_CHANGE, function () {
            self._setTitle(BI.parseFloat(self.minScale.getValue().scale), BI.parseFloat(self.maxScale.getValue().scale));
            self.fireEvent(BI.CustomScale.EVENT_CHANGE)
        });

        this.maxScale = BI.createWidget({
            type: "bi.combo_custom_scale",
            text: BI.i18nText("BI-Max"),
            wId: o.wId

        });

        this.maxScale.on(BI.ComboCustomScale.EVENT_CHANGE, function () {
            self._setTitle(BI.parseFloat(self.minScale.getValue().scale), BI.parseFloat(self.maxScale.getValue().scale));
            self.fireEvent(BI.CustomScale.EVENT_CHANGE)
        });

        this.interval = BI.createWidget({
            type: "bi.combo_custom_scale",
            text: BI.i18nText("BI-Interval_Number"),
            wId: o.wId
        });

        this.interval.on(BI.ComboCustomScale.EVENT_CHANGE, function () {
            this.showBubble();
            self.fireEvent(BI.CustomScale.EVENT_CHANGE)
        });

        this.interval.on(BI.ComboCustomScale.EVENT_VALUE_CHANGE, function () {
            this.showBubble();
        });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.minScale, this.maxScale, this.interval]
        });
    },

    _setTitle: function (min, max) {
        if(BI.isNotNull(min) && BI.isNotNull(max) && min > max) {
            this.minScale.setTitle(BI.i18nText("BI-Minimum_Less_Than_Maximum"));
            this.maxScale.setTitle(BI.i18nText("BI-Minimum_Less_Than_Maximum"))
        }
    },

    getValue: function () {
        return {
            minScale: this.minScale.getValue(),
            maxScale: this.maxScale.getValue(),
            interval: this.interval.getValue()
        }
    },

    setValue: function (v) {
        this.minScale.setValue(v.minScale || {});
        this.maxScale.setValue(v.maxScale || {});
        this.interval.setValue(v.interval || {})
    }
});
BI.CustomScale.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.custom_scale", BI.CustomScale);