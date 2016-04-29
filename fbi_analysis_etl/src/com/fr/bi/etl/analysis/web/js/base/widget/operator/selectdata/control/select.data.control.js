BI.AnalysisETLOperatorSelectDataController = BI.inherit(BI.MVCController, {
    _init : function () {
        BI.AnalysisETLOperatorSelectDataController.superclass._init.apply(this, arguments);
    },

    _construct : function(widget, model) {
        BI.AnalysisETLOperatorSelectDataController.superclass._construct.apply(this, arguments);
        this._editing = model.get(ETLCst.FIELDS).length === 0;
    },

    addField : function(fieldId, widget, model){
        if(this._editing !== true){
            return;
        }
        var name = BI.Utils.getFieldNameByID(fieldId);
        var fieldType = BI.Utils.getFieldTypeByID(fieldId)
        if(fieldType === BICst.COLUMN.DATE) {
            var group = fieldId.group
            name += "(" + this._createDateString(group)+ ")";
            fieldType = this._createNewFieldType(group);
        }
        var fieldName =  model.createDistinctName(name);
        model.addField({"field_name":fieldName, "field_type" : fieldType, "id":fieldId, "group" : fieldId.group})
        this._refreshState(widget, model);
    },

    _createNewFieldType : function (group) {
        switch (group) {
            case BICst.GROUP.Y : return BICst.COLUMN.NUMBER;
            case BICst.GROUP.S : return BICst.COLUMN.NUMBER;
            case BICst.GROUP.M : return BICst.COLUMN.NUMBER;
            case BICst.GROUP.W : return BICst.COLUMN.NUMBER;
            case BICst.GROUP.YMD : return BICst.COLUMN.DATE;
        }
    },

    _createDateString : function (group) {
        switch (group) {
            case BICst.GROUP.Y : return BI.i18nText("BI-Year_Fen");
            case BICst.GROUP.S : return BI.i18nText("BI-Quarter");
            case BICst.GROUP.M : return BI.i18nText("BI-Multi_Date_Month");
            case BICst.GROUP.W : return BI.i18nText("BI-Week_XingQi");
            case BICst.GROUP.YMD : return BI.i18nText("BI-Date");
        }
    },
    
    _refreshSelectDataPane : function (widget, model) {

        var tables = this._getTablesFromFields(model.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY));
        tables = BI.Utils.getProbablySinglePathTables(tables)
        widget.selectPane.controller.setEnableTables(tables);
    },

    _getTablesFromFields : function (fields) {
        var res = {};
        BI.each(fields, function (idx, item) {
            var tableId = BI.Utils.getTableIdByFieldID(item.id)
            res[tableId] = true;
        })
        return BI.map(res, function (idx, item) {
            return idx;
        })
    },


    refreshPopData : function (widget, model){
        var operatorType = this._editing ? ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL
        BI.Utils.triggerSelectDataPreview(widget.center, model, operatorType)
    },

    refreshPreviewData:function (tempModel, operatorType, widget, model) {
        BI.Utils.triggerPreview(widget.center, tempModel, operatorType);
    },

    deleteFieldByIndex : function (v, widget, model) {
        model.removeAt(v);
        this._refreshState(widget, model);
    },

    renameField : function (index, name, widget, model) {
        model.rename(index, name)
    },

    checkNameValid : function (index, name, widget, model) {
        return model.checkNameValid(index, name);
    },
    
    sortColumn : function (oldIndex, newIndex, widget, model) {
        model.sort(oldIndex, newIndex)
        this._refreshState(widget, model);
    },

    changeEditState : function(widget, model) {
        this._editing = !this._editing;

        if(this._editing === false){
            model.save();
            var v = model.update();
            widget.fireEvent(BI.TopPointerSavePane.EVENT_SAVE, v);
        } else {
            model.cancel();
            this._refreshState(widget, model);
        }

    },


    cancelAddField : function (widget, model) {
        model.cancel();
        this.changeEditState(widget, model);
    },


    _refreshButtonState : function (widget, model) {
        widget.cancelButton.setEnable(model.needCancel());
        widget.saveButton.setText( this._editing === true ? BI.i18nText("BI-finish_add") : BI.i18nText("BI-continue_add"));
    },


    _refreshCenterState : function(widget, model) {
        var enable = !this._editing;
        //如果没有添加字段是不可以下一步的
        if(model.get(BI.AnalysisETLOperatorSelectDataModel.KEY) === 0){
            enable = false;
        }
        widget.center.setEnable(enable)
    },

    _showMask:function (widget) {
        var masker = BI.Layers.make(widget.getName(),  widget.selectPane,  {
            render : {
                cls:"disable"
            }
        });
        BI.Layers.show(widget.getName());
    },

    _clearMask : function (widget) {
        BI.Layers.hide(widget.getName());
    },

    _refreshState : function (widget, model) {
        this._refreshSelectDataPane(widget, model);
        this._refreshButtonState(widget, model);
        this._refreshCenterState(widget, model);
        this.refreshPopData(widget, model);
        this._editing === true ? this._clearMask(widget) : this._showMask(widget);
    },
    
    populate : function (widget, model) {
        widget.center.populate(model.update(), {
            showContent :false
        })
        this._refreshState(widget, model);
    }
})