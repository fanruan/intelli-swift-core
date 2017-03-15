/**
 * Created by roy on 15/12/4.
 */
ETLAddFieldView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ETLAddFieldView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-add-field-view"
        })
    },
    _init: function () {
        ETLAddFieldView.superclass._init.apply(this, arguments);
    },
    _render: function (vessel) {
        var self = this;
        var formulalist = BI.createWidget({
            type: "bi.formula_list"
        });

        formulalist.on(BI.FormulaList.EVENT_CLICK_SET, function (id) {
            FloatBoxes.open(BI.UUID(), "formulaData." + id, {}, self, {
                data: {
                    fields: self.model.get("fields")
                }
            })
        });


        var items = [{
            fieldName: "fieldName1",
            formulaString: "formulaValue1"
        }, {
            fieldName: "fieldName2",
            formulaString: "formulaValue2"
        }];

        var fieldTypeSegment = BI.createWidget({
            type: "bi.segment",
            items: [
                {
                    type: "bi.icon_button",
                    cls: "select-data-field-string-font bi-segment-button",
                    value: 1,
                    selected: true
                }, {
                    type: "bi.icon_button",
                    cls: "select-data-field-number-font bi-segment-button",
                    value: 2
                }, {
                    type: "bi.icon_button",
                    cls: "select-data-field-date-font bi-segment-button",
                    value: 3
                }
            ],
            width: 240
        });


        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [
                {
                    type: "bi_formula_field_button",
                    formulaValue: "formulaValue",
                    FieldName: "fieldName"
                },
                {el: formulalist},
                {
                    el: fieldTypeSegment
                }
            ],
            vgap: 10,
            hgap: 10
        });

        formulalist.populate(items);
    }
});


ETLAddFieldModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(ETLAddFieldModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        ETLAddFieldModel.superclass._init.apply(this, arguments);
    }
});


ETLAddFieldFloatboxView = BI.inherit(BI.BarFloatSection, {
    _defaultConfig: function () {
        return BI.extend(ETLAddFieldFloatboxView.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        ETLAddFieldFloatboxView.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            value: BI.i18nText("BI-Formula_Column"),
            element: north,
            textAlign: "left",
            textHeight: 50
        })
    },

    rebuildCenter: function (center) {
        var self = this;
        var fieldNameLabel = BI.createWidget({
            type: "bi.label",
            value: BI.i18nText("BI-Column_Name") + ":",
            textHeight: 28
        });

        this.fieldNameEditor = BI.createWidget({
            type: "bi.text_editor",
            cls: "field-name-editor",
            quitChecker: function () {
                return false;
            },
            validationChecker: function (v) {
                if (self.model.get("isDuplicate", v)) {
                    self.fieldNameEditor.setWarningTitle(BI.i18nText("BI-Formula_Column_Duplicate"));
                    self.sure.setEnable(false);
                    self.sure.setWarningTitle(BI.i18nText("BI-Formula_Column_Duplicate"));
                    return false
                }
                self.fieldNameEditor.setWarningTitle("");
                return true
            },
            width: 380,
            height: 28
        });


        var fieldTypeLabel = BI.createWidget({
            type: "bi.label",
            value: BI.i18nText("BI-Field_Type") + ":",
            textHeight: 28
        });

        this.fieldTypeSegment = BI.createWidget({
            type: "bi.segment",
            items: [
                {
                    type: "bi.text_button",
                    text: BI.i18nText("BI-Number"),
                    cls: "bi-segment-button",
                    value: BICst.COLUMN.NUMBER
                }, {
                    type: "bi.text_button",
                    text: BI.i18nText("BI-Text"),
                    cls: "bi-segment-button",
                    value: BICst.COLUMN.STRING
                }, {
                    type: "bi.text_button",
                    text: BI.i18nText("BI-Time"),
                    cls: "bi-segment-button",
                    value: BICst.COLUMN.DATE

                }
            ],
            width: 240
        });

        var fieldItems = [];
        var fields = this.model.get("fields");
        BI.each(fields, function (i, fieldObjs) {
            BI.each(fieldObjs, function (i, field) {
                var fieldItem = {};
                fieldItem.fieldType = field.field_type;
                fieldItem.value = field.field_name;
                fieldItem.text = field.field_name;
                fieldItem.title = field.field_name;
                fieldItems.push(fieldItem);
            })
        });

        this.formulaEditor = BI.createWidget({
            type: "bi.formula_insert",
            fieldItems: fieldItems
        });


        return BI.createWidget({
            type: "bi.vtape",
            element: center,
            items: [
                {
                    type: "bi.left",
                    items: [
                        fieldNameLabel,
                        this.fieldNameEditor
                    ],
                    hgap: 5,
                    height: 40
                },
                {
                    type: "bi.left",
                    items: [
                        fieldTypeLabel,
                        this.fieldTypeSegment
                    ],
                    hgap: 5,
                    height: 40
                },
                this.formulaEditor
            ]
        })
    },

    refresh: function () {
        var formula = this.model.get("formula") || {};
        var formulaValue = formula.expression || "";
        var fieldName = formula.field_name || "";
        var fieldType = formula.field_type || BICst.COLUMN.NUMBER;
        this.fieldNameEditor.setValue(fieldName);
        this.fieldTypeSegment.setValue(fieldType);
        this.formulaEditor.refresh();
        this.formulaEditor.setValue(formulaValue);
    }
});

ETLAddFieldFloatboxModel = BI.inherit(BI.Model, {
    _static: function () {
        return {
            isDuplicate: function (fieldName) {
                var isDuplicated = false;
                var formulaData = this.get("formulaData");
                var fields = this.get("fields");
                var findDuplicate = BI.find(formulaData, function (id, formulaObj) {
                    if (fieldName === formulaObj.formula.field_name) {
                        return true;
                    }
                });

                BI.each(fields, function (i, fieldArray) {
                    findDuplicate = findDuplicate || BI.find(fieldArray, function (id, field) {
                            if (field.field_name === fieldName) {
                                return true
                            }
                        })
                });

                if (BI.isNotNull(findDuplicate)) {
                    isDuplicated = true;
                }
                return isDuplicated
            }
        }
    },

    _defaultConfig: function () {
        return BI.extend(ETLAddFieldFloatboxModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        ETLAddFieldFloatboxModel.superclass._init.apply(this, arguments);
    },

    refresh:function(){

    }

});