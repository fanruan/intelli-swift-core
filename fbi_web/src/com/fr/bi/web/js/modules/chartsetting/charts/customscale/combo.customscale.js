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
        var self = this, o = this.options;

        var label = BI.createWidget({
            type: "bi.label",
            cls: "combo-custom-scale-label",
            text: o.text,
            textHeight: 25,
            textAlign: "left"
        });

        this.pane = BI.createWidget({
            type: "bi.formula_insert",
            width: 500,
            height: 300
        });

        this.pane.on(BI.FormulaInsert.EVENT_CHANGE , function () {
            self.trigger.setValue(self.pane.getValue());
            self.fireEvent(BI.ComboCustomScale.EVENT_CHANGE)
        });

        this.trigger = BI.createWidget({
            type: "bi.custom_scale_trigger",
            text: o.text
        });

        this.trigger.on(BI.CustomScaleTrigger.EVENT_CHANGE , function() {
            self.fireEvent(BI.ComboCustomScale.EVENT_CHANGE)
        });

       var combo = BI.createWidget({
            type: "bi.combo",
            width: 110,
            isNeedAdjustWidth: false,
            isNeedAdjustHeight: false,
            el: this.trigger,
            popup: {
                el:this.pane
            },
            adjustYOffset: 5
        });

        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            element: this.element,
            items: {
                left: [label],
                right: [combo]
            },
            rhgap: 10
        })

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