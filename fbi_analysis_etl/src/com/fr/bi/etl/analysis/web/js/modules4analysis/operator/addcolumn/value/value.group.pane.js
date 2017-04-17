/**
 * Created by 小灰灰 on 2016/3/31.
 */
BI.AnalysisETLOperatorAddColumnValueGroupPane = BI.inherit(BI.Widget, {
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

    render: function(){
        var self = this;
        this.button = this.editorPane = this.pane = this.editor = this.combo = null;
        this.model = new BI.AnalysisETLOperatorAddColumnAllFieldsModel({});
        this.childPane = {};
        this.childValid = {};
        return {
            type : 'bi.vtape',
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
                        {
                            type: "bi.text_value_combo",
                            width : self._constants.COMBO_WIDTH,
                            height : self._constants.ITEM_HEIGHT,
                            items : [],
                            ref: function(_ref){
                                self.combo = _ref;
                            }
                        },
                        {
                            type : 'bi.layout',
                            width : self._constants.GAP
                        },
                        {
                            type: "bi.button",
                            text : BI.i18nText('BI-Basic_Add'),
                            height: self._constants.ITEM_HEIGHT,
                            handler : function () {
                                self.addCondition();
                            },
                            ref: function(_ref){
                                self.button = _ref;
                            }
                        }, {
                            type: "bi.checkbox",
                            listeners: [{
                                eventName: BI.Checkbox.EVENT_CHANGE,
                                action: function(){
                                    self.clickCheckBox();
                                }
                            }],
                            ref: function(_ref){
                                self.checkBox = _ref;
                            }
                        },
                        {
                            type : 'bi.label',
                            cls : 'label-name',
                            text : BI.i18nText('BI-ETL_Add_Column_Group_Other_Value'),
                            height : self._constants.ITEM_HEIGHT
                        }, {
                            type:"bi.button_group",
                            height: self._constants.ITEM_HEIGHT,
                            width : self._constants.COMBO_WIDTH,
                            items:[],
                            ref: function(_ref){
                                self.editorPane = _ref;
                            },
                            layouts: [{
                                type: "bi.horizontal"
                            }]
                        }]
                },
                height : self._constants.UP_HEIGHT
            },{
                el : {
                    type : 'bi.button_group',
                    cls : 'addcolumn-group-value-center',
                    ref: function(_ref){
                        self.pane = _ref;
                    },
                    layouts: [{
                        type: "bi.vertical",
                        tgap : self._constants.GAP
                    }]
                }
            }]
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
            height: self._constants.ITEM_HEIGHT,
            width : self._constants.COMBO_WIDTH
        });
        self.editor.setValue(value)
        validationChecker(value);
        self.editor.on(BI.TextEditor.EVENT_CHANGE,function(v){
            self.setOtherValue()
        });
        self.editorPane.populate([self.editor]);
    },

    createItem : function (field, value, fieldType, table) {
        var pane =  BI.createWidget({
            type:ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.GROUP + "_single",
            field:field,
            value : value,
            fieldType:fieldType,
            table : table
        })
        var self = this;
        pane.on(BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.EVENT_DELETE, function () {
            self.deleteItem(pane.getName())
        })
        pane.on(BI.AnalysisETLOperatorAddColumnValueGroupSinglePane.EVENT_CHANGE, function () {
            self.changeItem()
        })
        pane.on(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, function (valid) {
            self.setValid(pane.getName(), valid)
        })
        pane.checkValid();
        return pane;
    },

    //原controller
    _checkCanSave : function () {
        return BI.isNull(BI.find(this.childValid, function (idx, item) {
                return item === false;
            })) && (this.editorValid === true || !this.checkBox.isSelected()) && (BI.isNotNull(this.model.get(ETLCst.ITEMS)) && this.model.get(ETLCst.ITEMS).length > 0);
    },

    addCondition : function () {
        var v = this.combo.getValue()[0];
        var f = this.model.getFieldByValue(v);
        this._addCondition(f, {});
        this._buildItems();
        this.checkValid();
        this.pane.element.scrollTop(BI.MAX)
    },

    clickCheckBox : function () {
        this.model.set("showOther", this.checkBox.isSelected());
        this.editor.setEnable(this.checkBox.isSelected());
        if(this.checkBox.isSelected() === true) {
            this.editor.editor.focus();
        }
        this.checkValid()
    },

    setOtherValue : function () {
        this.model.set('other', this.editor.getValue());
    },

    _populate: function(){
        var fields = this.model.get(ETLCst.FIELDS);
        this.combo.populate(fields);
        this.pane.populate();

        this.combo.setValue(fields.length > 0  ? fields[0].value : null);
        this.checkBox.setSelected(this.model.get('showOther') || false);
        this.createEditor(this.options.fieldType, this.model.get('other') || "");
        this.clickCheckBox();
        var items = this.model.get(ETLCst.ITEMS);
        var self = this;
        BI.each(items, function (idx, item) {
            var field = self.model.getFieldByValue(item.field.value)
            self._addCondition(field, item)
        })
        this.checkValid()
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    _addCondition : function (field, value) {
        var pane =  this.pane.addItems([this.createItem(field, value, this.options.fieldType, this.model.get(ETLCst.PARENTS))]);
        this.childPane[pane.getName()] = pane;
        return pane;
    },

    setValid : function (key, valid) {
        this.childValid[key] = valid;
        this.checkValid()
    },

    checkValid : function () {
        this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, this._checkCanSave())
    },

    setEditorValid : function (valid) {
        this.editorValid =   valid
    },

    changeItem : function () {
        this._buildItems();
    },

    deleteItem : function (key) {
        var pane = this.childPane[key];
        if(BI.isNotNull(pane)) {
            pane.destroy()
        }
        delete this.childPane[key];
        delete this.childValid[key];
        this._buildItems();
        this.checkValid();
    },

    _buildItems : function () {
        var items = [];
        BI.each(this.childPane, function (idx, item) {
            items.push(item.update())
        })
        this.model.set(ETLCst.ITEMS, items);
    },

    changeFieldType : function (fieldType) {
        this.options.fieldType = fieldType;
        var oldValue = this.combo.getValue();
        this._construct();
        this.populate();
        this.combo.setValue(oldValue)
    },

    update: function(){
        return this.model.update()
    }



});
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.GROUP, BI.AnalysisETLOperatorAddColumnValueGroupPane);