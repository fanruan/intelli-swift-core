BI.AnalysisETLOperatorCenterController = BI.inherit(BI.MVCController, {

    _construct : function (widget, model) {
        BI.AnalysisETLOperatorCenterController.superclass._construct.apply(this, arguments);
        this._editing = false;
        this.previewOperator = this.options.previewOperator;
    },



    _defaultConfig: function() {
        return BI.extend(BI.Controller.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    clearOperator : function (widget){
        this._hideOperatorPane(widget)
        this._getTitle(widget).setEnable(true)
        this._getTitle(widget).clearAllSelected();
        this._getTitle(widget).setSaveButtonEnabled(true)
    },

    checkBeforeSave : function (table) {
        var res =  this.options.checkBeforeSave(table);
        if(res[0] === true) {
            res.push(BI.i18nText("BI-Modify_Step"))
        }
        return res;
    },

    doNewSave :function (widget){
        var showingCard = this._getOperatorPane(widget).getContentWidget().getShowingCard();
        var table = showingCard.update();
        var self = this;
        self.changeEditState(false, widget)
        self.clearOperator(widget)
        widget.fireEvent(BI.Controller.EVENT_CHANGE, self._editing, table);
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CANCEL, arguments)
    },

    doSave : function(widget){
        var showingCard = this._getOperatorEditPane(widget).getContentWidget();
        var table = showingCard.update();
        var self = this;
        self.changeEditState(false, widget);
        widget.fireEvent(BI.TopPointerSavePane.EVENT_SAVE, table);
        self.refreshPreviewData(ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL)

    },

    showOperatorPane : function(v, widget, model){
        this._statusAdd = true;
        this._getOperatorCard(widget).showCardByName(v.getValue())
        widget.operatorPaneItem.height = widget._constant.operatorPaneHeight;
        widget.operatorEditPaneItem.height = 0;
        this._getLayout(widget).resize(widget.vtapeItem)
        this._getOperatorEditPane(widget).hide();
        this.resetPointerPosition(widget, v);
        var showingCard = this._getOperatorPane(widget).getContentWidget().getShowingCard();
        this._getOperatorPane(widget).setEditing(true)
        //新建
        if(BI.isFunction(showingCard.populate)){
            showingCard.populate({parents : [model.update()]});
        }
        this._getTitle(widget).setSaveButtonEnabled(false)
    },


    resetPointerPosition : function (widget, v) {
        if( this._statusAdd === true) {
            this._getOperatorPane(widget).show(this._getPosFromElement(v, widget))
        } else {
            if(this.options.showContent === true) {
                this._getOperatorEditPane(widget).show(this._getPosFromValue(widget));
            } else {
                this._getOperatorEditPane(widget).hide()
            }
        }
    },


    _hideOperatorPane : function (widget) {
        this._statusAdd = false;
        widget.operatorPaneItem.height = 0;
        if(this.options.showContent === true) {
            widget.operatorEditPaneItem.height = widget._constant.operatorPaneHeight;
        }
        this._getLayout(widget).resize(widget.vtapeItem)
        this.resetPointerPosition(widget);
        if(this.options.showContent === true) {
            this._getOperatorEditPane(widget).setEditing(false);
        }
        this._getOperatorPane(widget).hide();
    },

    _getPosFromValue : function (widget) {
        var value = widget.options.contentItem.value.value
        var v = widget.title.getElementByValue(value);
        //不存在就不指向
        if(v === null) {
            return -999;
        }
        return this._getPosFromElement(v, widget);
    },

    _getPosFromElement : function(v, widget) {
        var buttonPos = v.element.position().left;
        var buttonWith = v.element.outerWidth();
        return buttonPos + buttonWith/2 - widget._constant.pointerWidth/2 + widget._constant.padding;
    },

    changeEditState : function (editing, widget) {
        this._editing = editing;
        this._getTitle(widget).setEnable(!this._editing)
    },

    setEnable : function(v, widget) {
        //编辑状态是不允许调整他的状态的
        if(this._editing === true){
            return;
        }
        this._getTitle(widget).clearAllSelected();
        this._getTitle(widget).setEnable(v)
        this._getOperatorEditPane(widget).setEnable(v)
        this._getOperatorPane(widget).setEnable(v)
        this._hideOperatorPane(widget);
    },

    _getTitle : function(widget) {
        return widget.title;
    },

    _getLayout : function(widget) {
        return widget.vtape
    },

    _getOperatorCard : function(widget) {
        return widget.operatorCard
    },

    doPreviewChange : function (m, type, widget, model) {
        var type = (this._editing || type === ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR ) ? type : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL
        this.isError = (type === ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR)
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, m, type)
    },

    _getOperatorPane : function (widget){
        return widget.operatorPane;
    },

    _getOperatorEditPane : function (widget) {
        return widget.operatorEditPane;
    },

    _getPreviewTable : function (widget) {
        return widget.previewTable;
    },

    filterChange : function (filter, widget){
        var showingCard = this._statusAdd ? this._getOperatorPane(widget).getContentWidget().getShowingCard() : this._getOperatorEditPane(widget).getContentWidget();
        showingCard.controller.filterChange(filter);
    },

    getFilterValue : function (field, widget){
        var showingCard = this._statusAdd ? this._getOperatorPane(widget).getContentWidget().getShowingCard() : this._getOperatorEditPane(widget).getContentWidget();
        return showingCard.controller.getFilterValue(field);
    },

    refreshPreviewData : function (v, widget) {
        if(this.isError === true){
            v = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR;
        }
        this.setPreviewOperator(v);
        var args = BI.clone(this.currentData);
        if(BI.isNull(args)) {
            args = []
            args.push([]);
            args.push([])
        }
        args.push(widget);
        args.push({});
        this.populatePreviewData.apply(this, args)
    },

     setPreviewOperator : function(operator) {
        this.previewOperator = operator;
    },

    populatePreviewData : function () {
        var widget = arguments[arguments.length - 2]
        var args = Array.prototype.slice.call(arguments, 0, arguments.length - 2);
        this.currentData = BI.clone(args);
        args.push(this.previewOperator)
        this._getPreviewTable(widget).populate.apply(this._getPreviewTable(widget), args)
    },

    fieldValuesCreator : function (field, callback, widget, model) {
        var table = {};
        table[ETLCst.ITEMS] = [model.update()]
        return BI.ETLReq.reqFieldValues({
            table : table,
            field : field
        }, callback);
    },
    
    populate : function (widget, model) {
        if(this.options.showContent === true) {
            widget.operatorEditPane.getContentWidget().populate(model.update(), this.options);
        }
    },

    deferChange : function (widget, model) {
        this._hideOperatorPane(widget);
    }

})