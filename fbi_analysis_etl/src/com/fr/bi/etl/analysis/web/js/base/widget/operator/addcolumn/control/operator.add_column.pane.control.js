BI.AnalysisETLOperatorAddColumnPaneController = BI.inherit(BI.MVCController, {



    _init: function () {
        BI.AnalysisETLOperatorAddColumnPaneController.superclass._init.apply(this, arguments);
        this._editing = false;
    },

    populate : function (widget, model) {
        this._check(widget, model);
        var cardName = this.getDefaultCardName(widget, model);
        widget.title.populate({}, {
            columnNames: this._getAllColumnNames(model)
        })
        widget.allColumnsPane.populate(model.getAddColumns())
        widget.card.showCardByName(cardName);
        this.doCheck(widget);
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, model, model.isValid() ? widget.options.value.operatorType : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR)
    },
    
    doCheck : function (widget) {
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true)
    },

    _doModelCheck : function (widget, model) {
        var parent = model.get(ETLCst.PARENTS)[0];
        var self = this;
        var columns = model.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY);
        var found = BI.some(columns, function(i ,column){
            switch (column.add_column_type){
                case BICst.ETL_ADD_COLUMN_TYPE.FORMULA :
                    return self._checkFormula(widget, column, parent[ETLCst.FIELDS])
                case BICst.ETL_ADD_COLUMN_TYPE.DATE_DIFF :
                    var fields = [];
                    var f = column.item['firstField'];
                    if(f !== ETLCst.SYSTEM_TIME) {
                        fields.push(f);
                    }
                    f = column.item['secondField'];
                    if(f !== ETLCst.SYSTEM_TIME) {
                        fields.push(f);
                    }
                    return self._checkField(widget, fields, parent[ETLCst.FIELDS],column.field_name, BICst.COLUMN.DATE)
                case BICst.ETL_ADD_COLUMN_TYPE.DATE_MONTH :
                case BICst.ETL_ADD_COLUMN_TYPE.DATE_SEASON :
                case BICst.ETL_ADD_COLUMN_TYPE.DATE_YEAR :
                    return self._checkField(widget, [column.item['field']], parent[ETLCst.FIELDS],column.field_name,BICst.COLUMN.DATE)
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_CPP:
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_CPP_PERCENT:
                    var fields = [];
                    fields.push(column.item['field'])
                    fields.push(column.item['monthSeason'])
                    fields.push(column.item['period'])
                    return self._checkField(widget, fields, parent[ETLCst.FIELDS],column.field_name,BICst.COLUMN.NUMBER)
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_LP:
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_LP_PERCENT:
                    var fields = [];
                    fields.push(column.item['field'])
                    fields.push(column.item['period'])
                    return self._checkField(widget, fields, parent[ETLCst.FIELDS],column.field_name,BICst.COLUMN.NUMBER)
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_ACC:
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_RANK:
                case BICst.ETL_ADD_COLUMN_TYPE.EXPR_SUM:
                    return self._checkField(widget, [column.item['field']], parent[ETLCst.FIELDS],column.field_name,BICst.COLUMN.NUMBER)
                case BICst.ETL_ADD_COLUMN_TYPE.GROUP:
                    return BI.some(column.item['items'], function (i, item) {
                        return self._checkField(widget, [item['field']["value"]], parent[ETLCst.FIELDS],column.field_name, item['field']['fieldType'])
                    })
            }
        })
        model.setValid(!found)
        return found;
    },

    _check : function (widget, model) {
        var found = this._doModelCheck(widget, model)
        if (!found){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, model.createFields())
        } else {
            model.set(ETLCst.FIELDS, [])
        }
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.VALID_CHANGE, !found);
    },

    _checkField : function (widget, dates, fields, columnName, type) {
        return BI.find(dates, function (i, date) {
            var f = BI.find(fields, function (i, field) {
                return date === field.field_name;
            });
            if (BI.isNull(f)){
                widget.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, BI.i18nText('BI-New_Column_Name') + columnName + '' + date + BI.i18nText('BI-Not_Fount'))
                return true;
            } else  if (f.field_type !== type){
                widget.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, BI.i18nText('BI-New_Column_Name') + columnName + '' + date + BI.i18nText('BI-Illegal_Field_Type'))
                return true;
            }
        })
    },

    _checkFormula : function (widget, column, fields) {
        var fs = BI.Utils.getFieldsFromFormulaValue(column.item.formula);
        var lostField = BI.find(fs, function (i, field) {
             if(BI.isNull(BI.find(fields, function (idx, f) {
                return f.field_name === field;
                }))){
                 return field
             }
        })
        if (BI.isNotNull(lostField)){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, BI.i18nText('BI-New_Column_Name') + column.field_name + BI.i18nText('BI-Formula_Valid')) + lostField
            return true;
        }
    },

    getDefaultCardName : function (widget, model) {
        this._editing = model.getAddColumns().length === 0;
        return this._editing ? widget._constant.SINGLE_COLUMN_CARD : widget._constant.ALL_COLUMNS_CARD;
    },

    createNewAddColumn : function (widget, model) {
        widget.title.populate({},{
            columnNames: this._getAllColumnNames(model)
        });
        this._editing = true;
        widget.card.showCardByName(widget._constant.SINGLE_COLUMN_CARD);
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true)
    },

    deleteColumnByName : function (name, widget, model) {
        model.deleteColumnByName(name);
        this._cancelEditColumn(widget, model);
        this._doModelCheck(widget, model)
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, model, model.isValid() ? widget.options.value.operatorType :  ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR)
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, model.getAddColumns().length !== 0, BI.i18nText('BI-Please') + BI.i18nText('BI-Add_Column'))
    },

    editColumnByName : function (name, widget, model) {
        var column = model.getColumnByName(name);
        if(BI.isNull(column)){
            console.log("error can't find column : " + name)
            return;
        }
        this._editing = true;
        this._editColumnName = name;
        widget.card.showCardByName(widget._constant.SINGLE_COLUMN_CARD);
        widget.title.populate(column, {
            columnNames: this._getAllColumnNames(model, name)
        });
    },

    _isEditing : function () {
        return this._editing
    },

    cancelColumn : function (widget, model) {
        if(this._isEditing()){
            if(model.getAddColumns().length === 0) {
                return false;
            }
            this._cancelEditColumn(widget, model);
            return true;
        } else {
            return false;
        }
    },

    _cancelEditColumn : function (widget, model) {
        this._editing = false;
        widget.allColumnsPane.populate(model.getAddColumns());
        widget.card.showCardByName(widget._constant.ALL_COLUMNS_CARD);
        this._editColumnName = null;
    },

    _saveColumn : function (widget, model) {

        var column = widget.title.update();
        var value = widget.currentEditPane.update();
        column["item"] = value;
        if(BI.isNotNull(value.field_type)) {
            column.field_type = value.field_type;
        }
        if(BI.isNotNull(this._editColumnName)){
            model.editColumn(column, this._editColumnName);
        } else {
            model.addColumn(column);
        }
        this._doModelCheck(widget, model)
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, model, model.isValid() ? widget.options.value.operatorType :  ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR)
        this._cancelEditColumn(widget, model);
    },

    saveColumn : function (editing, widget, model) {
        if(editing !== true || !this._isEditing()) {
            return false;
        }
        this._saveColumn(widget, model);
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, model.getAddColumns().length !== 0)
        return true;
    },

    _getAllColumnNames : function (model, name) {
        var columnNames = [];
        var parent = model.get(ETLCst.PARENTS)[0];
        BI.each(BI.concat(parent[ETLCst.FIELDS], model.getAddColumns()), function (idx, item) {
            if(item.field_name !== name) {
                columnNames.push(item.field_name)
            }
        })
        return columnNames;
    },

    refreshOneConditionPane : function (type, widget, model) {
        widget.oneConditionPane.empty();
        var parent = model.get(ETLCst.PARENTS)[0];
        var c = model.getColumnByName(this._editColumnName);
        var value = BI.isNotNull(c) ? c["item"] : null
        widget.currentEditPane = BI.createWidget({
            type : ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + type
        })
        widget.currentEditPane.on(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, function () {
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, arguments)
        });
        widget.oneConditionPane.populate([widget.currentEditPane]);
        var column = widget.title.update();
        var fields = {}
        fields[ETLCst.FIELDS] = parent[ETLCst.FIELDS];
        widget.currentEditPane.populate(BI.extend(fields, value), {
            field_type : column.field_type
        })
    },


    refreshOneConditionPaneViewIfNeeded : function (widget, model) {
        if(BI.isNotNull(widget.currentEditPane) && BI.isFunction(widget.currentEditPane.controller.changeFieldType)){
            var column = widget.title.update();
            widget.currentEditPane.controller.changeFieldType(column.field_type);
        }
    }

})