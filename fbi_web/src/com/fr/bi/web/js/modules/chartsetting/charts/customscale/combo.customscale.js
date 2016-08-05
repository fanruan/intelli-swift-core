/**
 * custom scale
 * Created by GameJian on 2016/7/19.
 */
BI.ComboCustomScale = BI.inherit(BI.Widget , {
    _defaultConfig: function() {
        return BI.extend(BI.ComboCustomScale.superclass._defaultConfig.apply(this , arguments) , {
            baseCls: "bi-combo-custom-scale"
        })
    },

    _init: function() {
        BI.ComboCustomScale.superclass._init.apply(this , arguments);
        var self = this;

        this.pane = BI.createWidget({
            type: "bi.calculate_target_formula_pane",
            width: 600,
            height: 300
        });

        this.pane.on(BI.CalculateTargetFormulaPane.EVENT_CHANGE , function () {
            self.trigger.setValue(self.pane.getValue().formula_value);
            self.fireEvent(BI.ComboCustomScale.EVENT_CHANGE)
        });

        this.trigger = BI.createWidget({
            type: "bi.custom_scale_trigger"
        });

        this.trigger.on(BI.CustomScaleTrigger.EVENT_CHANGE , function() {
            self.fireEvent(BI.ComboCustomScale.EVENT_CHANGE)
        });

        BI.createWidget({
            type: "bi.combo",
            width: 150,
            height: 50,
            element: this.element,
            isNeedAdjustWidth: false,
            isNeedAdjustHeight: false,
            el: this.trigger,
            popup: {
                el:this.pane
            },
            adjustYOffset: 5
        });
    },

    getValue: function() {
        return this.pane.getValue()
    },

    setValue: function(v) {
        this.pane.setValue(v)
    }
});
BI.ComboCustomScale.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.combo_custom_scale" , BI.ComboCustomScale);