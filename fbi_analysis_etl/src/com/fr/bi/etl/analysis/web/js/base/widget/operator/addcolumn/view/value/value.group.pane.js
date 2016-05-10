/**
 * Created by 小灰灰 on 2016/3/31.
 */
BI.AnalysisETLOperatorAddColumnValueGroupPane = BI.inherit(BI.MVCWidget, {
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

    _initView: function () {
        var self = this, o = this.options;
        self.combo = BI.createWidget({
            type: "bi.text_value_combo",
            width : self._constants.COMBO_WIDTH,
            height : self._constants.ITEM_HEIGHT,
            items : []
        });
        self.combo.on(BI.TextValueCombo.EVENT_CHANGE, function(){
        });
        self.checkBox = BI.createWidget({
            type: "bi.checkbox"
        });
        self.checkBox.on(BI.Checkbox.EVENT_CHANGE, function () {
            self.controller.clickCheckBox();
        })
        self.editorPane = BI.createWidget({
            type:"bi.horizontal",
            height: self._constants.ITEM_HEIGHT,
            width : self._constants.COMBO_WIDTH,
            items:[]
        })
        self.button = BI.createWidget({
            type: "bi.button",
            text : BI.i18nText('BI-Add'),
            height: self._constants.ITEM_HEIGHT,
            handler : function () {
                self.controller.addCondition();
            }
        });
        self.pane = BI.createWidget({
            type : 'bi.vertical',
            tgap : self._constants.GAP,
            cls : 'addcolumn-group-value-center'
        })
        BI.createWidget({
            type : 'bi.vtape',
            element : self.element,
            items : [{
                    el : {
                        type : 'bi.horizontal',
                        lgap : self._constants.GAP,
                        tgap : self._constants.GAP,
                        height : self._constants.UP_HEIGHT,
                        items : [{
                            type : 'bi.label',
                            cls : 'label-name',
                            text : BI.i18nText('BI-Value_According_To'),
                            height : self._constants.ITEM_HEIGHT
                        },
                            self.combo,
                            {
                                type : 'bi.layout',
                                width : self._constants.GAP
                        },
                            self.button, self.checkBox,
                            {
                                type : 'bi.label',
                                cls : 'label-name',
                                text : BI.i18nText('BI-ETL_Add_Column_Group_Other_Value'),
                                height : self._constants.ITEM_HEIGHT
                        }, self.editorPane]
                    },
                height : self._constants.UP_HEIGHT
            },{
                el : self.pane
            }]
        })
    },

    createEditor : function (field_type, value) {
        var self = this;
        var errorText = "";
        var validationChecker;
        switch(field_type) {
            case BICst.COLUMN.STRING : {
                validationChecker =  function (v) {
                    self.controller.setEditorValid(true)
                    return true;
                }
                break;
            }
            case BICst.COLUMN.DATE : {
                errorText =  BI.i18nText("BI-Date_Trigger_Error_Text"),
                    validationChecker = function (date) {
                        var valid =  Date.parseDateTime(date, "%Y-%x-%d").print("%Y-%x-%d") == date || Date.parseDateTime(date, "%Y-%X-%d").print("%Y-%X-%d") == date || Date.parseDateTime(date, "%Y-%x-%e").print("%Y-%x-%e") == date || Date.parseDateTime(date, "%Y-%X-%e").print("%Y-%X-%e") == date;
                        self.controller.setEditorValid(valid);
                        return valid;
                    }
                break;
            }
            case BICst.COLUMN.NUMBER : {
                errorText = BI.i18nText("BI-Numerical_Interval_Input_Data"),
                    validationChecker = function (v) {
                        var isNumber = BI.isNumeric(v)
                        self.controller.setEditorValid(isNumber);
                        return isNumber;
                    }
                break;
            }
        }
        self.editor =  BI.createWidget({
            type: "bi.text_editor",
            allowBlank : true,
            watermark: BI.i18nText("BI-Please_Enter_Value"),
            errorText: errorText,
            validationChecker : function (v) {
                var check = validationChecker(v);
                self.controller.checkValid();
                return check;
            },
            height: self._constants.ITEM_HEIGHT,
            width : self._constants.COMBO_WIDTH
        });
        self.editor.setValue(value)
        validationChecker(value);
        self.editor.on(BI.TextEditor.EVENT_CHANGE,function(v){
            self.controller.setOtherValue()
        });
        self.editorPane.empty();
        self.editorPane.addItem(self.editor)
    },

    createItem : function (field, value, field_type) {
        var pane =  BI.createWidget({
            type:ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.GROUP + "_single",
            field:field,
            value : value,
            field_type:field_type
        })
        var self = this;
        pane.on(BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.EVENT_DELETE, function () {
            self.controller.deleteItem(pane.getName())
        })
        pane.on(BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.EVENT_CHANGE, function () {
            self.controller.changeItem()
        })
        pane.on(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, function (valid) {
            self.controller.setValid(pane.getName(), valid)
        })
        pane.checkValid();
        return pane;
    },

    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnValueGroupController;
    },

    _initModel : function () {
        return BI.AnalysisETLOperatorAddColumnAllFieldsModel;
    }



});
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.GROUP, BI.AnalysisETLOperatorAddColumnValueGroupPane);