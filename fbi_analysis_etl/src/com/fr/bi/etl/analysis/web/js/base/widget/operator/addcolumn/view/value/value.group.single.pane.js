BI.AnalysisETLOperatorAddColumnValueGroupSinglePane = BI.inherit(BI.Widget, {
    _constants: {
        UP_HEIGHT : 50,
        ITEM_HEIGHT : 30,
        COMBO_WIDTH : 200,
        GAP : 10,
        DOWN_ITEM_HEIGHT : 32,
        FIELD_ITEM_WIDTH : 250,
        FIELD_VALUE_WIDTH : 350,
        PANE_HEIGHT : 148
    },

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls:"bi-analysis-etl-operator-add-column-all-pane",
            field: {
                text:"abc",
                fieldType : BICst.COLUMN.NUMBER
            },
            field_type:BICst.COLUMN.NUMBER,
            value : {
                filter_value : null,
                value : null
            }
        })
    },

    _init : function () {
        BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.superclass._init.apply(this, arguments)
        var self = this, o = this.options;
        var field = o.field;
        this.range = self._createFieldRangeItem(field);
        this.range.setValue(o.value.filter_value || {})
        this.editor = this._createEditor(o.field_type, o.value.value || "")

        var deleteIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: 'delete-template-font template-item-icon',
            title: BI.i18nText("BI-Remove"),
            invisible: true,
            stopPropagation: true
        });
        deleteIcon.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.EVENT_DELETE)
        });
        var item =  BI.createWidget({
            type : 'bi.horizontal',
            element:this.element,
            lgap : self._constants.GAP,
            height : self._constants.DOWN_ITEM_HEIGHT,
            items : [{
                type : 'bi.label',
                cls : 'label-name',
                text : field.text,
                height : self._constants.ITEM_HEIGHT,
                width: 90
            }, {
                type : 'bi.layout',
                width : self._constants.GAP
            },{
                type : 'bi.label',
                cls : 'label-name',
                text : field.fieldType == BICst.COLUMN.NUMBER ? BI.i18nText('BI-Between') : BI.i18nText('BI-In'),
                height : self._constants.ITEM_HEIGHT
            }, this.range,  {
                type : 'bi.layout',
                width : self._constants.GAP
            },  {
                type : 'bi.label',
                cls : 'label-name',
                text : BI.i18nText('BI-Field_Value') + '=',
                height : self._constants.ITEM_HEIGHT
            }, this.editor, deleteIcon]
        });
        item.element.hover(function () {
            deleteIcon.setVisible(true);
        }, function () {
            deleteIcon.setVisible(false);
        })
    },

    _createEditor : function (field_type, value) {
        var self = this;
        var errorText = "";
        var validationChecker;
        switch(field_type) {
            case BICst.COLUMN.STRING : {
                validationChecker =  function (v) {
                    pane.validChecked = true;
                    return true;
                }
                break;
            }
            case BICst.COLUMN.DATE : {
                errorText =  BI.i18nText("BI-Date_Trigger_Error_Text"),
                validationChecker = function (date) {
                    var valid =  Date.parseDateTime(date, "%Y-%x-%d").print("%Y-%x-%d") == date || Date.parseDateTime(date, "%Y-%X-%d").print("%Y-%X-%d") == date || Date.parseDateTime(date, "%Y-%x-%e").print("%Y-%x-%e") == date || Date.parseDateTime(date, "%Y-%X-%e").print("%Y-%X-%e") == date;
                    pane.validChecked = valid;
                    return valid;
                }
                break;
            }
            case BICst.COLUMN.NUMBER : {
                errorText = BI.i18nText("BI-Numerical_Interval_Input_Data"),
                validationChecker = function (v) {
                    var isNumber = BI.isNumeric(v)
                    pane.validChecked = isNumber;
                    return isNumber;
                }
                break;
            }
        }


        var pane =  BI.createWidget({
            type: "bi.text_editor",
            allowBlank : true,
            watermark: BI.i18nText("BI-Please_Enter_Value"),
            errorText: errorText,
            validationChecker : function (v) {
                var check = validationChecker(v);
                self.checkValid();
                return check;
            },
            height: self._constants.ITEM_HEIGHT,
            width : self._constants.FIELD_VALUE_WIDTH
        });
        pane.setValue(value)
        validationChecker(value);
        pane.on(BI.TextEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.EVENT_CHANGE)
        });
        return pane;
    },

    checkValid : function () {
        var v = this.editor.validChecked === true && this.range.validChecked === true;
        this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, v)
    },

    _createFieldRangeItem : function (field) {
        var self = this;
        switch (field.fieldType){
            case BICst.COLUMN.STRING :
                var pane =  BI.createWidget({
                    type: "bi.conf_filter_value_chooser_combo",
                    width : self._constants.FIELD_ITEM_WIDTH,
                    height : self._constants.ITEM_HEIGHT
                });
                pane.on(BI.ConfFilterValueChooserCombo.EVENT_CONFIRM, function () {
                    self.fireEvent(BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.EVENT_CHANGE)
                });
                pane.validChecked = true;
                return pane;
            case BICst.COLUMN.DATE :
                var pane =  BI.createWidget({
                    type: "bi.time_interval",
                    width : self._constants.FIELD_ITEM_WIDTH,
                    height : self._constants.ITEM_HEIGHT
                });
                pane.on(BI.TimeInterval.EVENT_CHANGE, function () {
                    self.fireEvent(BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.EVENT_CHANGE);
                });
                pane.on(BI.TimeInterval.EVENT_ERROR, function () {
                    pane.validChecked = false;
                    self.checkValid();
                });
                pane.on(BI.TimeInterval.EVENT_VALID, function () {
                    pane.validChecked = true;
                    self.checkValid();
                });
                return pane;
            case BICst.COLUMN.NUMBER :
                var pane =  BI.createWidget({
                    type: "bi.numerical_interval",
                    width : self._constants.FIELD_ITEM_WIDTH,
                    height : self._constants.ITEM_HEIGHT
                })
                pane.on(BI.NumericalInterval.EVENT_CHANGE, function () {
                    self.fireEvent(BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.EVENT_CHANGE)
                });
                pane.on(BI.NumericalInterval.EVENT_ERROR, function () {
                    pane.validChecked = false;
                    self.checkValid();
                });
                pane.on(BI.NumericalInterval.EVENT_VALID, function () {
                    pane.validChecked = true;
                    self.checkValid();
                });
                return pane;
        }
    },


    update : function () {
        return {
            field : this.options.field,
            filter_value : this.range.getValue(),
            value : this.editor.getValue(),
            field_type : this.options.field_type
        }
    }



})

BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.EVENT_DELETE = "event_delete";
BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.EVENT_CHANGE = "event_change";
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.GROUP + "_single", BI.AnalysisETLOperatorAddColumnValueGroupSinglePane);