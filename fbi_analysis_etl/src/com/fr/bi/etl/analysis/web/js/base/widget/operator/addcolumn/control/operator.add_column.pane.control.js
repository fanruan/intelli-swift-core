BI.AnalysisETLOperatorAddColumnPaneController = BI.inherit(BI.MVCController, {



    _init: function () {
        BI.AnalysisETLOperatorAddColumnPaneController.superclass._init.apply(this, arguments);
        this._editing = false;
    },

    populate : function () {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        var cardName = this.getDefaultCardName(widget, model);
        widget.title.populate({}, {
            columnNames: this._getAllColumnNames(model)
        })
        widget.allColumnsPane.populate(model.getAddColumns())
        widget.card.showCardByName(cardName);
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true)
    },

    getDefaultCardName : function () {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        this._editing = model.getAddColumns().length === 0;
        return this._editing ? widget._constant.SINGLE_COLUMN_CARD : widget._constant.ALL_COLUMNS_CARD;
    },

    createNewAddColumn : function () {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        widget.title.populate({},{
            columnNames: this._getAllColumnNames(model)
        });
        this._editing = true;
        widget.card.showCardByName(widget._constant.SINGLE_COLUMN_CARD);
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true)
    },

    deleteColumnByName : function (name) {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        model.deleteColumnByName(name);
        this._cancelEditColumn(widget, model);
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, model, widget.options.value.operatorType)
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, model.getAddColumns().length !== 0)
    },

    editColumnByName : function (name) {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        var column = model.getColumnByName(name);
        if(BI.isNull(column)){
            console.log("error can't find column : " + name)
            return;
        }
        this._editing = true;
        this._editColumnName = name;
        widget.title.populate(column, {
            columnNames: this._getAllColumnNames(model, name)
        });

        widget.card.showCardByName(widget._constant.SINGLE_COLUMN_CARD);
    },

    _isEditing : function () {
        return this._editing
    },

    cancelColumn : function () {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
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
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, model, widget.options.value.operatorType)
        this._cancelEditColumn(widget, model);
    },

    saveColumn : function (editing) {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
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
        BI.each(BI.concat(parent.fields, model.getAddColumns()), function (idx, item) {
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
        widget.currentEditPane.populate(BI.extend({
                fields:parent[ETLCst.FIELDS]
        }, value), {
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