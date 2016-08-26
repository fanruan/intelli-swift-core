/**
 * insert formula to custom scale
 * Created by AstronautOO7 on 2016/8/23.
 */
BI.CustomScaleFormula = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomScaleFormula.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-scale-formula"
        })
    },

    _init: function () {
        BI.CustomScaleFormula.superclass._init.apply(this, arguments);
        var self = this;

        this.formulaTree = BI.createWidget({
            type: "bi.custom_scale_formula_field_tree",
            cls: "bi-custom-formula-field-pane"
        });

        this.formulaTree.on(BI.FormulaFieldTree.EVENT_CHANGE, function () {
            var v = self.formulaTree.getValue();
            self.formulaEditor.insertField(self.fieldValueTextMap[v[0]]);
        });

        this.symbolGroup = BI.createWidget({
            type: "bi.symbol_group",
            height: 30,
            cls: "symbol-group-column"
        });

        this.symbolGroup.on(BI.CustomScaleFormula.EVENT_CHANGE, function (v) {
            self.formulaTree.insertOperator(v)
        });

        this.formulaEditor = BI.createWidget({
            type: "bi.formula",
            tipType:"warning"
        });

        this.formulaEditor.on(BI.FormulaEditor.EVENT_CHANGE, function () {
            if (BI.Func.checkFormulaValidation(self.formulaEditor.getCheckString())) {
                self.validation = "valid";
                BI.Bubbles.hide(self.getName() + "invalid");
            } else {
                BI.Bubbles.show(self.getName() + "invalid", BI.i18nText("BI-Formula_Valid"), self, {
                    offsetStyle: "center"
                });
                self.validation = "invalid"
            }
            self.fireEvent(BI.CustomScaleFormula.EVENT_CHANGE);
        });

        this.formulaEditor.on(BI.FormulaEditor.EVENT_BLUR, function () {
            BI.Bubbles.hide(self.getName() + "invalid");
            if (!self.checkValidation()) {
                self.formulaEditor.setTitle(BI.i18nText("BI-Formula_Valid"), {belowMouse: true});
            }
        });

        this.formulaEditor.on(BI.FormulaEditor.EVENT_FOCUS, function () {
            self.formulaEditor.setTitle("");
            if (!self.checkValidation()) {
                BI.Bubbles.show(self.getName() + "invalid", BI.i18nText("BI-Formula_Valid"), self, {offsetStyle: "center"});
            }
        });

        this.editorpane = BI.createWidget({
            type: "bi.vtape",
            items: [
                {
                    height: "fill",
                    el: self.formulaEditor
                }, {
                    height: 30,
                    el: self.symbolGroup
                }
            ]
        });

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [
                {
                    width: 160,
                    el: self.formulaTree
                },
                {
                    el: self.editorpane
                }
            ],
            height: 300,
            width: 500
        });

        self.formulaEditor.element.droppable({
            accept: ".bi-tree-text-leaf-item",
            drop: function (event, ui) {
                var value = ui.helper.attr("text");
                self.formulaEditor.insertField(value);
            }
        });
    }
});
BI.CustomScaleFormula.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.custom_scale_formula", BI.CustomScaleFormula);