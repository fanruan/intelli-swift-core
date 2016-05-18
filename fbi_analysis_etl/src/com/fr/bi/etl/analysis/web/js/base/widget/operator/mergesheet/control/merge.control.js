BI.AnalysisETLMergeSheetController = BI.inherit(BI.MVCController, {


    validationChecker : function (v, widget, model) {
        var allSheets = this.getSheets(widget, model);

        return BI.isNull(BI.find(allSheets, function (idx, item) {
            return item.text === v;
        }));
    },

    getSheets : function (widget, model) {

        return model.getAllSheets();
    },

    doSave : function (widget, model) {
        model.set("name", widget.nameInput.getValue())
        if(BI.isFunction(this.options.saveHandler)) {
            this.options.saveHandler(model.update())
        }
        BI.Layers.hide(BICst.ANALYSIS_MERGE_LAYER)
    },

    doCancel : function (widget, model) {
        if(BI.isFunction(this.options.cancelHandler)) {
            this.options.cancelHandler()
        }
        BI.Layers.hide(BICst.ANALYSIS_MERGE_LAYER)
    },

    getCurrentSheets : function () {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        return model.getSheets();
    },

    rename : function (idx, name, widget, model) {
        model.rename(idx, name)
    },


    setCurrent2Sheet : function (v) {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        if(model.setCurrent2Sheet(v) === true) {
            widget.preview.resetLeftRight()
            widget.refreshView();
        }
    },

    checkNameValid : function (j, v, widget, model) {
        var column = model.get("columns")
        var valid = true;
        BI.some(column, function (idx, item) {
            if(idx !== j && item.field_name === v) {
                valid = false;
                return true;
            }
        })
        return valid;
    },

    mergeTypeChange : function (widget, model) {
        widget.preview.populate(model.createPreviewData(),{})
    },

    mergeChange : function (isValid, widget, model) {
        if(isValid === true) {
            model.refreshColumnName()
            widget.preview.populate(model.createPreviewData(),{})
        }
        widget.saveButton.setEnable(isValid)
    },
    
    populate : function (widget, model) {
        widget.nameInput.setValue(model.get("name"))
        model.refreshColumnName()
        widget.preview.populate(model.createPreviewData(),{})
        widget.saveButton.setEnable(widget.mergeFields.controller.isValid())


    }

})