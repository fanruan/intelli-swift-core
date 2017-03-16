/**
 * Created by 小灰灰 on 2016/3/31.
 */
BI.AnalysisETLOperatorAddColumnValueSinglePane = BI.inherit(BI.MVCWidget, {
    _constants: {
        HEIGHT: 30,
        LABEL_WIDTH: 35,
        WIDTH : 200,
        GAP : 10
    },

    _initView: function () {
        var self = this, o = this.options;
        self.editorPane = BI.createWidget({
            type:"bi.horizontal",
            items:[],
            height: self._constants.HEIGHT,
            width : self._constants.WIDTH
        });

        BI.createWidget(    {
            type : 'bi.horizontal',
            element : self.element,
            lgap : self._constants.GAP,
            tgap : self._constants.GAP,
            items : [
                {
                    type : 'bi.label',
                    cls : 'label-name',
                    text : BI.i18nText('BI-Value') + ' =',
                    width : self._constants.LABEL_WIDTH,
                    textAlign : 'left'
                },
                {
                    el : self.editorPane
                }
            ]
        });
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
            allowBlank : field_type === BICst.COLUMN.STRING,
            watermark: BI.i18nText("BI-Please_Enter_Value"),
            errorText: errorText,
            validationChecker : function (v) {
                var check = validationChecker(v);
                self.controller.checkValid();
                return check;
            },
            height: self._constants.HEIGHT,
            width : self._constants.WIDTH
        });
        self.editor.setValue(value)
        validationChecker(value);
        self.editor.on(BI.TextEditor.EVENT_CHANGE,function(){
            self.controller.setValue(self.editor.getValue())
        });
        self.editorPane.empty();
        self.editorPane.addItem(self.editor)
    },

    _initModel : function () {
        return  BI.AnalysisETLOperatorAddColumnAllFieldsModel;
    },

    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnValueSingleController;
    }

});
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.SINGLE_VALUE, BI.AnalysisETLOperatorAddColumnValueSinglePane);