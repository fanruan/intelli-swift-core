/**
 * Created by Young's on 2017/4/5.
 */
BI.AnalysisOperatorCenter = BI.inherit(BI.Widget, {

    _constant : {
        pointerWidth:30,
        padding:10,
        operatorPaneHeight:312
    },

    props: {
        extraCls: "bi-analysis-etl-operator-center",
        showContent: true,
        contentItem : {
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.CHOOSE_FIELD,
            type:"bi.center_adapt",
            items:[{
                type:"bi.button",
                text:BI.i18nText('BI-Basic_Edit'),
                width:120
            }]
        },
        isCurrentTheLastOperator: function(){
            return true;
        },
        previewOperator : ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA
    },

    render: function(){
        var self = this, o = this.options;
        this.model = new BI.AnalysisOperatorAbstarctPaneModel({});
        this.combos = [];
        this.title = BI.createWidget({
            type:"bi.analysis_operator_title"
        });

        this.title.on(BI.AnalysisOperatorTitle.CLICK_SAVE, function(){
            self.title.setTableName(self.getTableName());
        });

        this.title.on(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, function(v, isConfirm){
            if(isConfirm){
                self.fireEvent(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, arguments);
                self.showOperatorPane(v);
            }else{
                var combos = self.title.getTitleCombos();
                var result = BI.find(self.title.getTitleButtons(), function(idx, button){
                    if(!o.isCurrentTheLastOperator() && button.getValue() === v.getValue()){
                        combos[idx].showView();
                        return true;
                    }
                });
                if(BI.isNull(result)){
                    self.fireEvent(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, arguments);
                    self.showOperatorPane(v);
                }
            }
        });

        this.title.on(BI.AnalysisOperatorTitle.EVENT_SAVE, function(){
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_SAVE, arguments)
        });
        this.title.on(BI.AnalysisOperatorTitle.EVENT_STATE_CHANGE, function(v){
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_STATE_CHANGE, arguments)
        });

        this.previewTable = BI.createWidget({
            type: "bi.analysis_etl_preview_table",
            cls: "bi-analysis-tab-table",
            header:[],
            items:[],
            fieldValuesCreator : function(){
                return self.fieldValuesCreator.apply(self, arguments);
            },
            nameValidationController: o.nameValidationController
        });
        this.previewTable.on(BI.AnalysisETLPreviewTable.EVENT_FILTER, function(filter){
            self.filterChange(filter);
        });
        this.previewTable.on(BI.AnalysisETLPreviewTable.DELETE_EVENT, function(v){
            self.fireEvent(BI.AnalysisETLPreviewTable.DELETE_EVENT, v)
        });

        this.previewTable.on(BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN, function(){
            self.fireEvent(BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN, arguments)
        });

        this.previewTable.on(BI.AnalysisETLPreviewTable.EVENT_RENAME, function(index, name){
            self.fireEvent(BI.AnalysisOperatorCenter.EVENT_RENAME, index, name)
        });
        this.operatorCard = BI.createWidget({
            type:"bi.tab",
            cardCreator: BI.bind(this._createTabs, this)
        });

        this.operatorPane = BI.createWidget({
            type:"bi.analysis_top_pointer_save_pane",
            pointerWidth:this._constant.pointerWidth,
            contentItem:this.operatorCard,
            cancelHandler : function () {
                return BI.isFunction(self.operatorCard.getSelectedTab().cancelHandler)
                    && self.operatorCard.getSelectedTab().cancelHandler()
            },
            saveHandler : function (editing) {
                return BI.isFunction(self.operatorCard.getSelectedTab().saveHandler)
                    && self.operatorCard.getSelectedTab().saveHandler(editing)
            },

            checkBeforeSave : function () {
                return self.checkBeforeSave(self.operatorCard.getSelectedTab().getValue())
            }
        });

        this.operatorEditPane = BI.createWidget({
            type:"bi.analysis_top_pointer_save_pane",
            pointerWidth:this._constant.pointerWidth,
            contentItem: o.contentItem,
            cancelHandler : function () {
                return BI.isFunction(self.operatorEditPane.getContentWidget().cancelHandler)
                    && self.operatorEditPane.getContentWidget().cancelHandler()
            },
            saveHandler : function (editing) {
                return BI.isFunction(self.operatorEditPane.getContentWidget().saveHandler)
                    && self.operatorEditPane.getContentWidget().saveHandler(editing)
            },

            checkBeforeSave : function () {
                return self.checkBeforeSave(self.operatorEditPane.getContentWidget().getValue())
            }
        });

        this.operatorEditPane.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, arguments)
        });
        this.operatorPane.on(BI.AnalysisTopPointerSavePane.EVENT_CANCEL, function(){
            self.clearOperator()
            self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CANCEL, arguments)
        });
        this.operatorPane.on(BI.AnalysisTopPointerSavePane.EVENT_SAVE, function(){
            self.doNewSave();
        });
        this.operatorEditPane.on(BI.AnalysisTopPointerSavePane.EVENT_INVALID, function(){
            self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_INVALID, arguments)
        });
        this.operatorEditPane.on(BI.AnalysisTopPointerSavePane.EVENT_FIELD_VALID, function(){
            self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_FIELD_VALID, arguments)
        });
        this.operatorEditPane.on(BI.AnalysisTopPointerSavePane.EVENT_CANCEL, function(){
            self.changeEditState(false);
            self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CANCEL, arguments)
        });
        this.operatorEditPane.on(BI.AnalysisOperatorCenter.DATA_CHANGE, function () {
            self.fireEvent(BI.AnalysisOperatorCenter.DATA_CHANGE, arguments)
        });
        this.operatorEditPane.on(BI.AnalysisTopPointerSavePane.EVENT_EDIT, function(){
            self.changeEditState(true);
            self.refreshPreviewData(self.options.contentItem.value.operatorType)
        });
        this.operatorEditPane.on(BI.AnalysisTopPointerSavePane.EVENT_SAVE, function(){
            self.doSave();
        });

        this.operatorEditPane.on(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, function (model, type) {
            self.doPreviewChange(model, type)
        });

        this.operatorEditPane.on(BI.AnalysisETLOperatorAbstractController.VALID_CHANGE, function (v) {
            self.title.setEnable(v, BI.i18nText("BI-Current_Error"))
        });

        this.operatorEditPaneItem = {
            el : this.operatorEditPane,
            height:0
        };
        this.operatorPaneItem = {
            el : this.operatorPane,
            height:0
        };
        this.vtapeItem = [{
            el: this.title,
            height: 39
        }, this.operatorPaneItem,
            this.operatorEditPaneItem, {
                el: {
                    type: "bi.border",
                    cls: "preview",
                    items: {
                        north: {
                            type: "bi.layout",
                            height: 10
                        },
                        center: this.previewTable,
                        south: {
                            type: "bi.layout",
                            height: 10
                        },
                        east: {
                            type: "bi.layout",
                            width: 10
                        },
                        west: {
                            type: "bi.layout",
                            width: 10
                        }
                    }
                }
            }];
        return {
            type:"bi.vtape",
            cls:"bi-elt-operator-select-data-center",
            items:this.vtapeItem,
            ref: function(_ref){
                self.vtape = _ref;
            }
        }
    },

    _createTabs: function(v){
        var self = this;
        var tabItem = BI.find(ETLCst.ANALYSIS_TABLE_OPERATOR, function(idx, item){
            return item.value === v;
        })
        var pane = BI.createWidget({
            type: tabItem.operatorType + ETLCst.ANALYSIS_TABLE_PANE
        });
        pane.on(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, function () {
            self.operatorCard.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, arguments)
        });
        pane.on(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, function (model, type) {
            self.title.setEnable(model.isDefaultValue());
            self.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, arguments)
        });
        return pane;
    },

    populatePreview : function() {
        this.populatePreviewData.apply(this, arguments)
    },

    clearOperator : function (){
        this._hideOperatorPane()
        this.title.setEnable(true)
        this.title.clearAllSelected();
        this.title.setSaveButtonEnabled(true)
    },

    checkBeforeSave : function (table) {
        var res =  this.options.checkBeforeSave(table);
        if(res[0] === false) {
            res.push(BI.i18nText("BI-Modify_Step"))
        }
        return res;
    },

    doNewSave :function (){
        var showingCard = this.operatorPane.getContentWidget().getSelectedTab();
        var table = showingCard.getValue();
        var self = this;
        self.changeEditState(false);
        self.clearOperator();
        this.fireEvent(BI.Controller.EVENT_CHANGE, self._editing, table);
        this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CANCEL, arguments)
    },

    doSave : function(){
        var showingCard = this.operatorEditPane.getContentWidget();
        var table = showingCard.getValue();
        var self = this;
        self.changeEditState(false);
        this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_SAVE, table);
        self.refreshPreviewData(ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL)

    },

    showOperatorPane : function(v){
        this._statusAdd = true;
        this.operatorCard.setSelect(v.getValue())
        this.operatorPaneItem.height = this._constant.operatorPaneHeight;
        this.operatorEditPaneItem.height = 0;
        this.vtape.resize();
        this.operatorEditPane.hide();
        this.resetPointerPosition(v);
        var showingCard = this.operatorPane.getContentWidget().getSelectedTab();
        this.operatorPane.setEditing(true);
        //新建
        if(BI.isFunction(showingCard.populate)){
            showingCard.populate({parents : [this.model.update()]});
        }
        this.title.setSaveButtonEnabled(false)
    },


    resetPointerPosition : function (v) {
        if( this._statusAdd === true) {
            this.operatorPane.show(this._getPosFromElement(v))
        } else {
            if(this.options.showContent === true) {
                this.operatorEditPane.show(this._getPosFromValue());
            } else {
                this.operatorEditPane.hide()
            }
        }
    },

    _hideOperatorPane : function () {
        this._statusAdd = false;
        this.operatorPaneItem.height = 0;
        if(this.options.showContent === true) {
            this.operatorEditPaneItem.height = this._constant.operatorPaneHeight;
        }
        this.vtape.resize();
        this.resetPointerPosition();
        if(this.options.showContent === true) {
            this.operatorEditPane.setEditing(false);
        }
        this.operatorPane.hide();
    },

    _getPosFromValue : function () {
        var value = this.options.contentItem.value.value;
        var v = this.getElementByValue(value);
        //不存在就不指向
        if(v === null) {
            return -999;
        }
        return this._getPosFromElement(v);
    },

    _getPosFromElement : function(v) {
        var buttonPos = v.element.position().left;
        var buttonWith = v.element.outerWidth();
        return buttonPos + buttonWith/2 - this._constant.pointerWidth/2 + this._constant.padding;
    },

    changeEditState : function (editing) {
        this._editing = editing;
        this.title.setEnable(!this._editing)
    },

    setEnable : function(v) {
        //编辑状态是不允许调整他的状态的
        if(this._editing === true){
            return;
        }
        this.title.clearAllSelected();
        this.title.setEnable(v)
        this.operatorEditPane.setEnable(v)
        this.operatorPane.setEnable(v)
        this._hideOperatorPane();
    },

    doPreviewChange : function (m, type) {
        var type = (this._editing || type === ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR ) ? type : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL
        this.isError = (type === ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR)
        this.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, m, type)
    },

    filterChange : function (filter){
        var showingCard = this._statusAdd ? this.operatorPane.getContentWidget().getSelectedTab() : this.operatorPane.getContentWidget();
        showingCard.filterChange(filter);
    },

    getFilterValue : function (field){
        var showingCard = this._statusAdd ? this.operatorPane.getContentWidget().getSelectedTab() : this.operatorEditPane.getContentWidget();
        return showingCard.getFilterValue(field);
    },

    refreshPreviewData : function (v) {
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
        args.push(this);
        args.push({});
        this.populatePreviewData.apply(this, args)
    },

    setPreviewOperator : function(operator) {
        this.previewOperator = operator;
    },

    populatePreviewData : function () {
        var args = Array.prototype.slice.call(arguments, 0);
        this.currentData = BI.clone(args);
        args.push(this.previewOperator);
        this.previewTable.populate.apply(this.previewTable, args)
    },

    fieldValuesCreator : function (field, callback) {
        var table = {};
        table[ETLCst.ITEMS] = this._editing === true ? this.model.update()[ETLCst.PARENTS] : [this.model.update()];
        return BI.ETLReq.reqFieldValues({
            table : table,
            field : field
        }, callback);
    },

    _populate: function(){
        if(this.options.showContent === true) {
            this.operatorEditPane.getContentWidget().populate(this.model.update(), this.options);
        }
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
        this._hideOperatorPane();
    },

    getTableName: function(){
        return this.model.getValue("tableName");
    }
})
BI.AnalysisOperatorCenter.DATA_CHANGE="DATA_CHANGE";
BI.AnalysisOperatorCenter.EVENT_RENAME = "event_rename";
BI.shortcut("bi.analysis_operator_center", BI.AnalysisOperatorCenter);