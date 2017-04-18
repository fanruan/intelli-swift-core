/**
 * Created by 小灰灰 on 2016/3/31.
 */
BI.AnalysisETLOperatorAddColumnValueSinglePane = BI.inherit(BI.Widget, {
    _constants: {
        HEIGHT: 30,
        LABEL_WIDTH: 35,
        WIDTH : 200,
        GAP : 10
    },

    render: function(){
        var self = this;
        this.model = new BI.AnalysisETLOperatorAddColumnAllFieldsModel({});
        return {
            type : 'bi.horizontal',
            lgap : self._constants.GAP,
            tgap : self._constants.GAP,
            items : [
                {
                    type : 'bi.label',
                    cls : 'label-name',
                    text : BI.i18nText('BI-Basic_Value') + ' =',
                    width : self._constants.LABEL_WIDTH,
                    textAlign : 'left'
                },
                {
                    el : {
                        type:"bi.button_group",
                        items:[],
                        height: self._constants.HEIGHT,
                        width : self._constants.WIDTH,
                        ref: function(_ref){
                            self.editorPane = _ref;
                        },
                        layouts: [{
                            type:"bi.horizontal"
                        }]
                    }
                }
            ]
        }
    },

    createEditor : function (fieldType, value) {
        var self = this;
        var errorText = "";
        var validationChecker;
        switch(fieldType) {
            case BICst.COLUMN.STRING : {
                validationChecker =  function (v) {
                    self.setEditorValid(true)
                    return true;
                }
                break;
            }
            case BICst.COLUMN.DATE : {
                errorText =  BI.i18nText("BI-Date_Trigger_Error_Text"),
                    validationChecker = function (date) {
                        var valid =  Date.parseDateTime(date, "%Y-%x-%d").print("%Y-%x-%d") == date || Date.parseDateTime(date, "%Y-%X-%d").print("%Y-%X-%d") == date || Date.parseDateTime(date, "%Y-%x-%e").print("%Y-%x-%e") == date || Date.parseDateTime(date, "%Y-%X-%e").print("%Y-%X-%e") == date;
                        self.setEditorValid(valid);
                        return valid;
                    }
                break;
            }
            case BICst.COLUMN.NUMBER : {
                errorText = BI.i18nText("BI-Numerical_Interval_Input_Data"),
                    validationChecker = function (v) {
                        var isNumber = BI.isNumeric(v)
                        self.setEditorValid(isNumber);
                        return isNumber;
                    }
                break;
            }
        }
        self.editor =  BI.createWidget({
            type: "bi.text_editor",
            allowBlank : fieldType === BICst.COLUMN.STRING,
            watermark: BI.i18nText("BI-Please_Enter_Value"),
            errorText: errorText,
            validationChecker : function (v) {
                var check = validationChecker(v);
                self.checkValid();
                return check;
            },
            height: self._constants.HEIGHT,
            width : self._constants.WIDTH
        });
        self.editor.setValue(value)
        validationChecker(value);
        self.editor.on(BI.TextEditor.EVENT_CHANGE,function(){
            self.setValue(self.editor.getValue())
        });
        self.editorPane.empty();
        self.editorPane.addItem(self.editor)
    },

    //原controller
    _checkCanSave : function () {
        if (BI.isNull(this.model.get('v'))){
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Value_Cannot_Be_Null'));
        } else {
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS,  this.valid);
        }
    },

    setEditorValid : function (v) {
        this.valid =  v;
    },

    checkValid : function () {
        this._checkCanSave()
    },

    setValue : function (value) {
        this.model.set('v', value);
        this._checkCanSave();
    },

    _populate: function(){
        var value = this.model.get('v') || "";
        this.model.set('v', value);
        this.createEditor(this.options.fieldType, value);
        this._checkCanSave()
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    changeFieldType : function (fieldType) {
        this.options.fieldType = fieldType;
        this.populate()
    },

    update: function(){
        return this.model.update()
    }

});
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.SINGLE_VALUE, BI.AnalysisETLOperatorAddColumnValueSinglePane);