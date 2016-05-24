BI.AnalysisETLOperatorSelectDataController = BI.inherit(BI.MVCController, {
    _init : function () {
        BI.AnalysisETLOperatorSelectDataController.superclass._init.apply(this, arguments);
    },

    _construct : function(widget, model) {
        BI.AnalysisETLOperatorSelectDataController.superclass._construct.apply(this, arguments);
        this._editing = model.get(ETLCst.FIELDS).length === 0;
        this.trigger = BI.Utils.triggerPreview()
    },

    addField : function(fieldId, widget, model){
        if(this._editing !== true){
            return;
        }
        model.addField(fieldId)
        this._refreshState(widget, model);
    },

    _refreshSelectDataPane : function (widget, model) {

        var tables = model.getTempFieldsTables();
        tables = BI.Utils.getProbablySinglePathTables(tables)
        widget.selectPane.controller.setEnableTables(tables);
    },

    refreshPopData : function (operatorType, widget, model){
        this.trigger(widget.center, model, operatorType, ETLCst.PREVIEW.SELECT)
    },

    refreshPreviewData:function (tempModel, operatorType, widget, model) {
        this.trigger(widget.center, tempModel, operatorType, ETLCst.PREVIEW.NORMAL);
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
        if(this._editing === true) {
            widget.saveButton.setEnable(model.get(ETLCst.FIELDS).length > 0 || model.needCancel())
        }
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
        this.refreshPopData(this._editing ? ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL, widget, model);
        this._editing === true ? this._clearMask(widget) : this._showMask(widget);
    },
    
    populate : function (widget, model) {
        widget.center.populate(model.update(), {
            showContent :false
        })
        this._refreshState(widget, model);
        widget.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, model.getValue(ETLCst.FIELDS))
    }
})