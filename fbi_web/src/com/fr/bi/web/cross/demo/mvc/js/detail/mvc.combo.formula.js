FormulaComboView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FormulaComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-formula-combo-view bi-mvc-layout"
        })
    },

    _init: function () {
        FormulaComboView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var combo = BI.createWidget({
            type: "bi.formula_combo",
            fieldItems: [{
                text: "A", value: "A", fieldType: BICst.COLUMN.STRING
            }],
            width: 200,
            height: 30
        });
        combo.setValue("A+A");
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 20,
            vgap: 30,
            items: [combo]
        })
    }
});

FormulaComboModel = BI.inherit(BI.Model, {});