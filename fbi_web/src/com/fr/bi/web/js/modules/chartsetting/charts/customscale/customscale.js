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

        this.maxScale = BI.createWidget({
            type: "bi.combo_custom_scale"
        });

        this.maxScale.on(BI.ComboCustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.CustomScale.EVENT_CHANGE)
        });

        this.minScale = BI.createWidget({
            type: "bi.combo_custom_scale"
        });

        this.minScale.on(BI.ComboCustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.CustomScale.EVENT_CHANGE)
        });

        this.interval = BI.createWidget({
            type: "bi.combo_custom_scale"
        });

        this.interval.on(BI.ComboCustomScale.EVENT_CHANGE, function () {
            self.fireEvent(BI.CustomScale.EVENT_CHANGE)
        });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.maxScale, this.minScale, this.interval],
            lgap: 10
        });
    },

    getValue: function () {
        return {
            maxScale: this.maxScale.getValue(),
            minScale: this.minScale.getValue(),
            interval: this.interval.getValue()
        }
    },

    setValue: function (v) {
        this.maxScale.setValue(v.maxScale);
        this.maxScale.getValue(v.minScale);
        this.interval.getValue(v.interval)
    }
});
BI.CustomScale.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.custom_scale", BI.CustomScale);