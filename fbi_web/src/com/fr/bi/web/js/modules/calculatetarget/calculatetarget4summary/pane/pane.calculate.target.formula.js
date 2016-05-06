/**
 * Created by roy on 16/3/2.
 */
BI.CalculateTargetFormulaPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetFormulaPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-formula-pane",
            model: null
        })
    },
    _init: function () {
        var self = this, o = this.options;
        BI.CalculateTargetFormulaPane.superclass._init.apply(this, arguments);
        this.formulaEditor = BI.createWidget({
            type: "bi.formula_insert",
            element: this.element,
            fieldItems: o.fieldItems
        });
        this.formulaEditor.on(BI.FormulaInsert.EVENT_CHANGE, function () {
            self.fireEvent(BI.CalculateTargetFormulaPane.EVENT_CHANGE)
        });

    },

    getValue: function () {
        var result = {};
        result.formula_value = this.formulaEditor.getValue();
        result.ids = this.formulaEditor.getUsedFields();
        return result;
    },

    setValue: function (expression) {
        this.formulaEditor.setValue(expression.formula_value);
    },

    populate: function (model) {
        var o = this.options;
        o.model = model;
        this.formulaEditor.populate(o.model.getFormulaFields());
    }
});
BI.CalculateTargetFormulaPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.calculate_target_formula_pane", BI.CalculateTargetFormulaPane);
