//小于号的值为：0，小于等于号的值为:1
/**
 * Created by roy on 15/9/17.
 * 闭区间: true, 开区间: false
 */
BI.NumericalIntervalCombo = BI.inherit(BI.Widget, {
    constants: {
        expandClass: "combo-expand",
        tgap: 2
    },
    _defaultConfig: function () {
        var conf = BI.NumericalIntervalCombo.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-numerical-interval-combo",
            items: [{
                text: "<(" + BI.i18nText("BI-Less_Than") + ")",
                value: 0
            }, {
                text: "≤(" + BI.i18nText("BI-Less_And_Equal") + ")",
                value: 1
            }],
            height:30,
            adjustLength: -1,
            offsetStyle: "left"
        })
    },
    _init: function () {
        var self = this, o = this.options;
        BI.NumericalIntervalCombo.superclass._init.apply(this, arguments);

        if (BI.isNumeric(o.value)) {
            o.items[o.value].selected = true
        } else {
            o.items[0].selected = true
        }

        this.trigger = BI.createWidget({
            type: "bi.numerical_interval_trigger",
            height: o.height-2,
            items: o.items,
            value: o.value
        })
        this.popup = BI.createWidget({
            type: "bi.numerical-interval-popup",
            value: o.value,
            items: o.items
        })
        this.combo = BI.createWidget({
            element: this.element,
            type: "bi.combo",
            trigger: "click,hover",
            isNeedAdjustHeight: false,
            isNeedAdjustWidth: false,
            offsetStyle: o.offsetStyle,
            adjustLength: o.adjustLength,
            el: self.trigger,
            popup: {
                el: self.popup,
                stopPropagation:false,
                tgap: self.constants.tgap
            }

        })

        this.combo.on(BI.Combo.EVENT_EXPAND, function () {
            self.trigger.hover();
        })

        this.combo.on(BI.Controller.EVENT_CHANGE,function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE,arguments);
        })

        this.combo.on(BI.Combo.EVENT_COLLAPSE, function () {
            self.trigger.dishover();
        })

        this.popup.on(BI.NumericalIntervalPopup.EVENT_CHANGE, function () {
            var value = self.popup.getValue();
            self.combo.hideView();
            self.trigger.setValue(value[0]);
            self.fireEvent(BI.NumericalIntervalCombo.EVENT_CHANGE);
        })

        this.popup.on(BI.Controller.EVENT_CHANGE,function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE,arguments);
        })

    },

    setEnable:function(b){
        this.combo.setEnable(b);
    },

    setTitle: function (v) {
        this.trigger.setTitle(v);
    },

    getValue: function () {
        return this.popup.getValue();
    },

    setValue: function (v) {
        var o = this.options, self = this;
        v = BI.isArray(v) ? v[0] : v;
        BI.each(o.items, function (i, item) {
            if (v === item.value) {
                self.popup.setValue(item.value);
                self.trigger.setValue(item.value);
            }
        })
    }
})
BI.NumericalIntervalCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.numerical_interval_combo", BI.NumericalIntervalCombo);