/**
 * Created by roy on 16/3/17.
 */
BI.AddFormulaFieldPopover = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.AddFormulaFieldPopover.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        var o = this.options;
        BI.AddFormulaFieldPopover.superclass._init.apply(this, arguments);
        this.model = new BI.AddFormulaFieldPopoverModel({
            info: o.info
        })
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
            type: "bi.editor",
            cls: "field-name-editor",
            allowBlank: true,
            quitChecker: function () {
                return false;
            },
            errorText: BI.i18nText("BI-Formula_Column_Duplicate"),
            validationChecker: function (v) {
                return !self.model.isDuplicate(v);
            },
            width: 380,
            height: 28
        });

        this.fieldNameEditor.on(BI.Editor.EVENT_VALID, function () {
            self._checkEnable();
        });

        this.fieldNameEditor.on(BI.Editor.EVENT_ERROR, function () {
            self._checkEnable();
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


        this.formulaEditor = BI.createWidget({
            type: "bi.formula_insert",
            //fieldItems: fieldItems
        });

        this.formulaEditor.on(BI.FormulaInsert.EVENT_CHANGE, function () {
            self._checkEnable();
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

    end: function () {
        var newFormulaObj = {};
        var fieldTypeArray = this.fieldTypeSegment.getValue();
        newFormulaObj.field_name = this.fieldNameEditor.getValue() || "";
        newFormulaObj.field_type = fieldTypeArray[0];
        newFormulaObj.expression = this.formulaEditor.getValue() || "";
        var data = {
            id: this.model.getID(),
            formula: newFormulaObj
        };
        this.fireEvent(BI.AddFormulaFieldPopover.EVENT_SAVE, data)
    },


    _checkEnable: function () {
        var fieldName = this.fieldNameEditor.getValue();
        var formulaValidation = this.formulaEditor.checkValidation();
        var formulaValue = this.formulaEditor.getValue();
        if (BI.isEmptyString(fieldName)) {
            this.sure.setWarningTitle(BI.i18nText("BI-Field_Name_Cannot_Be_Null"));
            this.sure.setEnable(false);
        } else if (formulaValidation === false) {
            this.sure.setWarningTitle(BI.i18nText("BI-Formula_Valid"));
            this.sure.setEnable(false);
        } else if (BI.isEmptyString(formulaValue)) {
            this.sure.setWarningTitle(BI.i18nText("BI-Added_Formula_Column_Cannot_Be_Null"));
            this.sure.setEnable(false)
        } else if (!this.fieldNameEditor.isValid()) {
            this.fieldNameEditor.setWarningTitle(BI.i18nText("BI-Formula_Column_Duplicate"));
            this.sure.setEnable(false);
            this.sure.setWarningTitle(BI.i18nText("BI-Formula_Column_Duplicate"));
        } else {
            this.sure.setWarningTitle("");
            this.sure.setEnable(true);
        }
    },

    _getFormulaFiledItems: function () {
        var fieldItems = [];
        var fields = this.model.getFields();
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
        return fieldItems
    },


    _refreshPane: function () {
        var formula = this.model.getFormula() || {};
        var formulaValue = formula.expression || "";
        var fieldName = formula.field_name || "";
        var fieldType = formula.field_type || BICst.COLUMN.NUMBER;
        this.fieldNameEditor.setValue(fieldName);
        this.fieldTypeSegment.setValue(fieldType);
        this.formulaEditor.populate(this._getFormulaFiledItems());
        this.formulaEditor.setValue(formulaValue);
        this._checkEnable();
    },

    populate: function () {
        var self = this;
        this.model.initData(function () {
            self._refreshPane();
        })
    }


});
BI.AddFormulaFieldPopover.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.add_formula_field_popover", BI.AddFormulaFieldPopover);