/**
 * custom scale
 * Created by GameJian on 2016/7/19.
 */
BI.ComboCustomScale = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ComboCustomScale.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-combo-custom-scale"
        })
    },

    _init: function () {
        BI.ComboCustomScale.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.label = BI.createWidget({
            type: "bi.label",
            cls: "combo-custom-scale-label",
            text: o.text,
            textHeight: 25,
            textAlign: "left"
        });

        this.pane = BI.createWidget({
            type: "bi.custom_scale_formula_pane",
            wId: o.wId,
            width: 500,
            height: 360
        });

        this.pane.on(BI.CustomScaleFormulaPane.EVENT_CHANGE, function () {
            var scale = self._calculate(self.pane.getAnalyzeContent());
            self.trigger.setValue(scale);
            self.combo.hideView();
            self.fireEvent(BI.ComboCustomScale.EVENT_CHANGE)
        });

        this.pane.on(BI.CustomScaleFormulaPane.EVENT_VALUE_CANCEL, function () {
            self.combo.hideView()
        });

        this.pane.on(BI.CustomScaleFormulaPane.EVENT_VALUE_CHANGE, function () {
             self.fireEvent(BI.ComboCustomScale.EVENT_VALUE_CHANGE)
        });

        this.trigger = BI.createWidget({
            type: "bi.custom_scale_trigger",
            text: o.text
        });

        this.trigger.on(BI.CustomScaleTrigger.EVENT_CHANGE, function () {
            self.fireEvent(BI.ComboCustomScale.EVENT_CHANGE)
        });

        this.combo = BI.createWidget({
            type: "bi.combo",
            width: 110,
            isNeedAdjustWidth: false,
            isNeedAdjustHeight: false,
            el: this.trigger,
            popup: {
                el: this.pane
            },
            adjustYOffset: 5
        });

        this.combo.on(BI.Combo.EVENT_AFTER_POPUPVIEW, function () {
            self.pane.refresh()
        });

        this.combo.on(BI.Combo.EVENT_AFTER_HIDEVIEW, function () {
            var scale = self._calculate(self.pane.getAnalyzeContent());
            self.trigger.setValue(scale);
            self.pane.setOldValue(self.pane.getValue())
        });

        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            element: this.element,
            items: {
                left: [this.label],
                right: [this.combo]
            },
            rlgap: 10,
            rrgap: 20
        })
    },

    _calculate: function (v) {
        v = v || [];
        var formula = "";
        BI.each(v, function (id, item) {
            var fieldRegx = /\$[\{][^\]*[\}]/;
            var str = item.match(fieldRegx);
            if (BI.isNotEmptyArray(str)) {
                var value = BI.Utils.getCalculateValue(item.substring(2, 18));
                var type = item.substring(18, item.length - 1);
                formula += value[type]
            } else {
                formula += item
            }
        });
        if(/[a-zA-Z]/.test(formula) || BI.isEmptyString(formula)){
            return formula
        }else{
            return eval(formula) === false ? "" : eval(formula)
        }
    },

    showBubble: function () {
        var self = this;
        var scale = this._calculate(this.pane.getAnalyzeContent());
        if(/[a-zA-Z]/.test(scale) || BI.parseFloat(scale) <= 0) {
            BI.Bubbles.show(self.trigger.getName() + "invalid", BI.i18nText("BI-Interval_Value_Should_Be_Positive"), self, {
                offsetStyle: "center"
            });
        }else {
            BI.Bubbles.hide(self.trigger.getName() + "invalid");
        }
    },

    setTitle: function (title) {
        this.trigger.setTitle(title)
    },

    getValue: function () {
        return {
            formula: this.pane.getValue(),
            scale: this.trigger.getValue()
        }
    },

    setValue: function (v) {
        this.pane.setValue(v.formula || "");
        var scale = this._calculate(this.pane.getAnalyzeContent());
        this.trigger.setValue(scale)
    }
});
BI.ComboCustomScale.EVENT_CHANGE = "EVENT_CHANGE";
BI.ComboCustomScale.EVENT_VALUE_CHANGE = "EVENT_VALUE_CHANGE";
$.shortcut("bi.combo_custom_scale", BI.ComboCustomScale);