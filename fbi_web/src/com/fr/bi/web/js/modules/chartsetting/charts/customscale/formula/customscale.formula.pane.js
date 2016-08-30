/**
 * customScale formula pane
 * Created by AstronautOO7 on 2016/8/29.
 */
BI.CustomScaleFormulaPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomScaleFormulaPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-scale-formula-pane",
            items: {
                type: 14,
                30000: [BI.UUID(), BI.UUID()],
                40000: []
            }
        })
    },
    
    _init: function () {
        BI.CustomScaleFormulaPane.superclass._init.apply(this, arguments);
        this.populate()
    },

    populate:function () {
        var self = this, items = this.options.items;

        this.formula = BI.createWidget({
            type: "bi.custom_scale_formula"
        });
        this.formula.populate(items);
        this.formula.on(BI.CustomScaleFormula.EVENT_CHANGE, function () {
            confirmButton.setEnable(self.formula.checkValidation())
        });

        var confirmButton = BI.createWidget({
            type: "bi.button",
            level: "common",
            height: 30,
            text: BI.i18nText("BI-OK")
        });

        confirmButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.CustomScaleFormulaPane.EVENT_CHANGE)
        });

        var cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            height: 30,
            text: BI.i18nText("BI-Cancel")
        });

        cancelButton.on(BI.Button.EVENT_CHANGE, function () {
            self.setValue(self.oldValue);
            self.fireEvent(BI.CustomScaleFormulaPane.EVENT_VALUE_CANCEL)
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            height: 360,
            width: 500,
            items: [{
                el: this.formula,
                height: "fill"
            }, {
                el: {
                    type: "bi.right_vertical_adapt",
                    height: 60,
                    items: [cancelButton, confirmButton],
                    hgap: 10
                },
                height: 60
            }]
        })
    },

    getValue: function () {
        return this.formula.getValue()
    },

    setValue: function (v) {
        this.oldValue = v;
        this.formula.setValue(v)
    }
});
BI.CustomScaleFormulaPane.EVENT_CHANGE = "EVENT_CHANGE";
BI.CustomScaleFormulaPane.EVENT_VALUE_CANCEL = "EVENT_VALUE_CANCEL";
$.shortcut("bi.custom_scale_formula_pane",BI.CustomScaleFormulaPane);