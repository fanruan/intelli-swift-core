/**
 * insert formula to custom scale
 * Created by AstronautOO7 on 2016/8/23.
 */
BI.CustomScaleFormula = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomScaleFormula.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-scale-formula",
            items: {
                type: 14,
                30000: [BI.UUID(), BI.UUID()]
            }
        })
    },

    _init: function () {
        BI.CustomScaleFormula.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        self.validation = 'valid';

        this.formulaTree = BI.createWidget({
            type: "bi.custom_scale_formula_field_tree",
            cls: "bi-custom-formula-field-pane",
            items: o.items
        });

        this.formulaTree.on(BI.CustomScaleFormulaFieldTree.EVENT_CHANGE, function () {
            var v = self.formulaTree.getValue();
            self.formulaEditor.insertField(self.fieldValueTextMap[v[0]]);
        });

        this.symbolGroup = BI.createWidget({
            type: "bi.symbol_group",
            height: 30,
            cls: "symbol-group-column"
        });

        this.symbolGroup.on(BI.SymbolGroup.EVENT_CHANGE, function (v) {
            self.formulaEditor.insertOperator(v)
        });

        this.formulaEditor = BI.createWidget({
            type: "bi.formula",
            showHint: false,
            tipType: "warning"
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

        this.populate(o.items)
    },

    _createTargetMap: function (state, items, targetType, text, axis) {
        var fieldMap = {};
        if(state){
            BI.each(items[targetType], function (idx, item) {
                fieldMap["max(" + axis + (idx + 1) + ")"] = item + text[0];
                fieldMap["min(" + axis + (idx + 1) + ")"] = item + text[1];
                fieldMap["average(" + axis + (idx + 1) + ")"] = item + text[2]
            });
        }else{
            BI.each(items[targetType], function (idx, item) {
                fieldMap[item + text[0]] = "max(" + axis + (idx + 1) + ")";
                fieldMap[item + text[1]] = "min(" + axis + (idx + 1) + ")";
                fieldMap[item + text[2]] = "average(" + axis + (idx + 1) + ")"
            });
        }

        return fieldMap
    },

    _createOneTargetMap: function (state, text, items, axisOne) {
        return this._createTargetMap(state, items, BICst.REGION.TARGET1, text, axisOne)
    },

    _createTwoTargetsMap: function (state, text, items, axisOne, axisTwo) {
        var fieldMap1 = this._createTargetMap(state, items, BICst.REGION.TARGET1, text, axisOne);
        var fieldMap2 = this._createTargetMap(state, items, BICst.REGION.TARGET2, text, axisTwo);

        return BI.extend(fieldMap1, fieldMap2)
    },

    _createThreeTargetsMap: function (state, text, items, axisOne, axisTwo, axisThree) {
        var fieldMap1 = this._createTargetMap(state, items, BICst.REGION.TARGET1, text, axisOne);
        var fieldMap2 = this._createTargetMap(state, items, BICst.REGION.TARGET2, text, axisTwo);
        var fieldMap3 = this._createTargetMap(state, items, BICst.REGION.TARGET3, text, axisThree);

        return BI.extend(fieldMap1, fieldMap2, fieldMap3)
    },

    _createFieldTextValueMap: function (items) {
        var text = [BI.i18nText("BI-DOT_MAX"), BI.i18nText("BI-DOT_MIN"), BI.i18nText("BI-DOT_AVERAGE")];

        switch (items.type) {
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
                return this._createTwoTargetsMap(true, text, items, BI.i18nText("BI-Left_Value_Axis"),
                    BI.i18nText("BI-Right_Value_Axis"));
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                return this._createOneTargetMap(true, text, items, BI.i18nText("BI-Value_Axis"));
                break;
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
                return this._createTwoTargetsMap(true, text, items, BI.i18nText("BI-Positive_Value_Axis"),
                    BI.i18nText("BI-Negative_Value_Axis"));
                break;
            case BICst.WIDGET.COMPARE_BAR:
                return this._createTwoTargetsMap(true, text, items, BI.i18nText("BI-Value_Axis_One"),
                    BI.i18nText("BI-Value_Axis_Two"));
                break;
            case BICst.WIDGET.RANGE_AREA:
                return this._createTwoTargetsMap(true, text, items, BI.i18nText("BI-Low_Value_Axis"),
                    BI.i18nText("BI-High_Value_Axis"));
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                return this._createThreeTargetsMap(true, text, items, BI.i18nText("BI-Left_Value_Axis"),
                    BI.i18nText("BI-Right_Value_Axis_One"), BI.i18nText("BI-Right_Value_Axis_Two"));
                break;
            case BICst.WIDGET.BUBBLE:
                return this._createTwoTargetsMap(true, text, items, BI.i18nText("BI-Y_Value_Axis"),
                    BI.i18nText("BI-X_Value_Axis"));
                break;
        }
    },

    _createFieldValueTextMap: function (items) {
        var text = [BI.i18nText("BI-DOT_MAX"), BI.i18nText("BI-DOT_MIN"), BI.i18nText("BI-DOT_AVERAGE")];

        switch (items.type) {
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
                return this._createTwoTargetsMap(false, text, items, BI.i18nText("BI-Left_Value_Axis"),
                    BI.i18nText("BI-Right_Value_Axis"));
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                return this._createOneTargetMap(false, text, items, BI.i18nText("BI-Value_Axis"));
                break;
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
                return this._createTwoTargetsMap(false, text, items, BI.i18nText("BI-Positive_Value_Axis"),
                    BI.i18nText("BI-Negative_Value_Axis"));
                break;
            case BICst.WIDGET.COMPARE_BAR:
                return this._createTwoTargetsMap(false, text, items, BI.i18nText("BI-Value_Axis_One"),
                    BI.i18nText("BI-Value_Axis_Two"));
                break;
            case BICst.WIDGET.RANGE_AREA:
                return this._createTwoTargetsMap(false, text, items, BI.i18nText("BI-Low_Value_Axis"),
                    BI.i18nText("BI-High_Value_Axis"));
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                return this._createThreeTargetsMap(false, text, items, BI.i18nText("BI-Left_Value_Axis"),
                    BI.i18nText("BI-Right_Value_Axis_One"), BI.i18nText("BI-Right_Value_Axis_Two"));
                break;
            case BICst.WIDGET.BUBBLE:
                return this._createTwoTargetsMap(false, text, items, BI.i18nText("BI-Y_Value_Axis"),
                    BI.i18nText("BI-X_Value_Axis"));
                break;
        }
    },

    _bindDragEvent: function () {
        var self = this;
        BI.each(self.formulaTree.getAllLeaves(), function (i, node) {
            node.element.draggable({
                cursorAt: {top: 5, left: 5},
                helper: function () {
                    var hint = BI.createWidget({
                        type: "bi.helper",
                        value: node.getValue(),
                        text: self.fieldValueTextMap[node.getValue()]
                    });
                    BI.createWidget({
                        element: self.element,
                        type: "bi.default",
                        items: [hint]
                    });
                    hint.element.attr({text: self.fieldValueTextMap[node.getValue()]});
                    return hint.element;

                }
            })
        });
    },

    getValue: function () {

    },

    populate: function (items) {
        this.options.items = items;
        this.fieldTextValueMap = this._createFieldTextValueMap(items);
        this.fieldValueTextMap = this._createFieldValueTextMap(items);
        this.formulaEditor.setFieldTextValueMap(this.fieldTextValueMap);
        this.formulaTree.populate(items);
        this._bindDragEvent();
    }

});
BI.CustomScaleFormula.EVENT_CHANGE = 'EVENT_CHANGE';
$.shortcut("bi.custom_scale_formula", BI.CustomScaleFormula);