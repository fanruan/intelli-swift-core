/**
 * Created by Young's on 2017/4/5.
 */
BI.AnalysisOperatorCenter = BI.inherit(BI.Widget, {
    _constant: {
        pointerWidth: 30,
        padding: 10,
        operatorPaneHeight: 312
    },

    props: {
        extraCls: "bi-analysis-etl-operator-center",
        showContent: true,
        contentItem: {
            value: ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.CHOOSE_FIELD,
            type: "bi.center_adapt",
            items: [{
                type: "bi.button",
                text: BI.i18nText('BI-Edit'),
                width: 120
            }]
        },
        previewOperator: ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA
    },

    render: function () {
        var self = this;
        this._createOperatorTitle();
        this._createOperatorPane();
        this._createOperatorEditPane();
        this._createPreviewTable();

        this.operatorPaneOB = {
            el: this.operatorPane,
            height: 0
        };
        this.operatorEditPaneOB = {
            el: this.operatorEditPane,
            height: 0
        };
        BI.createWidget({
            type: "bi.vtape",
            element: this,
            items: [{
                el: this.title,
                height: 40
            }, this.operatorPaneOB, this.operatorEditPaneOB, {
                type: "bi.layout",
                height: 10
            }, {
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
            }]
        })
    },

    _createOperatorTitle: function () {
        var self = this;
        this.title = BI.createWidget({
            type: "bi.analysis_operator_title"
        });
        this.title.on(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, function (v) {
            self.showOperatorPane(v);
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, arguments);
        });
        this.title.on(BI.AnalysisOperatorTitle.EVENT_SAVE, function (v) {
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_SAVE, arguments)
        });
        this.title.on(BI.AnalysisOperatorTitle.EVENT_STATE_CHANGE, function (v) {
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_STATE_CHANGE, arguments)
        });
        return this.title;
    },

    _createOperatorPane: function () {
        var self = this;
        this.operatorCard = BI.createWidget({
            type: "bi.card",
            items: this._createOperatorCard()
        });
        this.operatorPane = BI.createWidget({
            type: "bi.top_pointer_save_pane",
            pointerWidth: this._constant.pointerWidth,
            contentItem: this.operatorCard,
            cancelHandler: function () {
                return BI.isFunction(self.operatorCard.getShowingCard().cancelHandler)
                    && self.operatorCard.getShowingCard().cancelHandler()
            },
            saveHandler: function (editing) {
                return BI.isFunction(self.operatorCard.getShowingCard().saveHandler)
                    && self.operatorCard.getShowingCard().saveHandler(editing)
            },

            checkBeforeSave: function () {
                return self.checkBeforeSave(self.operatorCard.getShowingCard().getValue())
            }
        });
        this.operatorPane.on(BI.TopPointerSavePane.EVENT_CANCEL, function () {
            self.clearOperator();
            self.fireEvent(BI.TopPointerSavePane.EVENT_CANCEL, arguments)
        });
        this.operatorPane.on(BI.TopPointerSavePane.EVENT_SAVE, function () {
            self.doNewSave();
        });
        return this.operatorPane;
    },

    _createOperatorEditPane: function () {
        var self = this, o = this.options;
        this.operatorEditPane = BI.createWidget({
            type: "bi.top_pointer_save_pane",
            pointerWidth: this._constant.pointerWidth,
            contentItem: o.contentItem,
            cancelHandler: function () {
                return BI.isFunction(self.operatorEditPane.getContentWidget().cancelHandler)
                    && self.operatorEditPane.getContentWidget().cancelHandler()
            },
            saveHandler: function (editing) {
                return BI.isFunction(self.operatorEditPane.getContentWidget().saveHandler)
                    && self.operatorEditPane.getContentWidget().saveHandler(editing)
            },
            checkBeforeSave: function () {
                return self.checkBeforeSave(self.operatorEditPane.getContentWidget().update())
            }
        });
        this.operatorEditPane.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, arguments)
        });
        this.operatorEditPane.on(BI.TopPointerSavePane.EVENT_INVALID, function () {
            self.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, arguments)
        });
        this.operatorEditPane.on(BI.TopPointerSavePane.EVENT_FIELD_VALID, function () {
            self.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, arguments)
        });
        this.operatorEditPane.on(BI.TopPointerSavePane.EVENT_CANCEL, function () {
            self.changeEditState(false);
            self.fireEvent(BI.TopPointerSavePane.EVENT_CANCEL, arguments)
        });
        this.operatorEditPane.on(BI.AnalysisETLOperatorCenter.DATA_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorCenter.DATA_CHANGE, arguments)
        });
        this.operatorEditPane.on(BI.TopPointerSavePane.EVENT_EDIT, function () {
            self.changeEditState(true);
            self.refreshPreviewData(self.options.contentItem.value.operatorType)
        });
        this.operatorEditPane.on(BI.TopPointerSavePane.EVENT_SAVE, function () {
            self.doSave();
        });
        this.operatorEditPane.on(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, function (model, type) {
            self.doPreviewChange(model, type)
        });
        this.operatorEditPane.on(BI.AnalysisETLOperatorAbstractController.VALID_CHANGE, function (v) {
            self.title.setEnable(v, BI.i18nText("BI-Current_Error"))
        });
        return this.operatorEditPane;
    },

    _createPreviewTable: function () {
        var self = this;
        this.previewTable = BI.createWidget({
            type: "bi.analysis_etl_preview_table",
            cls: "bi-analysis-tab-table",
            header: [],
            items: [],
            fieldValuesCreator: function () {
                return self.fieldValuesCreator.apply(self, arguments);
            },
            nameValidationController: o.nameValidationController
        });
        this.previewTable.on(BI.AnalysisETLPreviewTable.EVENT_FILTER, function (filter) {
            self.filterChange(filter);
        });
        this.previewTable.on(BI.AnalysisETLPreviewTable.DELETE_EVENT, function (v) {
            self.fireEvent(BI.AnalysisETLPreviewTable.DELETE_EVENT, v)
        });
        this.previewTable.on(BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN, function () {
            self.fireEvent(BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN, arguments)
        });
        this.previewTable.on(BI.AnalysisETLPreviewTable.EVENT_RENAME, function (index, name) {
            self.fireEvent(BI.AnalysisETLOperatorCenter.EVENT_RENAME, index, name)
        });
        return this.previewTable;
    },

    showOperatorPane: function (v) {
        this._statusAdd = true;
        this.operatorCard.showCardByName(v.getValue());
        this.operatorPaneItem.height = this._constant.operatorPaneHeight;
        this.operatorEditPaneItem.height = 0;
        this.vtape.resize(widget.vtapeItem);
        this.operatorEditPane.hide();
        this.resetPointerPosition(v);
        var showingCard = this.operatorEditPane.getContentWidget().getShowingCard();
        this.operatorEditPane.setEditing(true);
        //新建
        if (BI.isFunction(showingCard.populate)) {
            showingCard.populate({parents: [this.options]});
        }
        this.title.setSaveButtonEnabled(false)
    },

    resetPointerPosition: function (v) {
        if (this._statusAdd === true) {
            this.operatorPane.show(this._getPosFromElement(v))
        } else {
            if (this.options.showContent === true) {
                this.operatorEditPane.show(this._getPosFromValue(widget));
            } else {
                this.operatorEditPane.hide()
            }
        }
    },

    _getPosFromElement: function (v) {
        var buttonPos = v.element.position().left;
        var buttonWith = v.element.outerWidth();
        return buttonPos + buttonWith / 2 - this._constant.pointerWidth / 2 + this._constant.padding;
    },

    _getPosFromValue: function () {
        var value = this.options.contentItem.value.value;
        var v = this.title.getElementByValue(value);
        //不存在就不指向
        if (v === null) {
            return -999;
        }
        return this._getPosFromElement(v);
    },

    _hideOperatorPane : function () {
        this._statusAdd = false;
        this.operatorPaneItem.height = 0;
        if(this.options.showContent === true) {
            this.operatorEditPaneItem.height = this._constant.operatorPaneHeight;
        }
        this.vtape.resize(this.vtapeItem);
        this.resetPointerPosition();
        if(this.options.showContent === true) {
            this.operatorEditPane.setEditing(false);
        }
        this.operatorEditPane.hide();
    },

    checkBeforeSave: function (table) {
        var res = this.options.checkBeforeSave(table);
        if (res[0] === false) {
            res.push(BI.i18nText("BI-Modify_Step"))
        }
        return res;
    },

    clearOperator : function (){
        this._hideOperatorPane();
        this.title.setEnable(true);
        this.title.clearAllSelected();
        this.title.setSaveButtonEnabled(true)
    },

    changeEditState : function (editing) {
        this._editing = editing;
        this.title.setEnable(!this._editing)
    },

    doNewSave :function (){
        var showingCard = this.operatorPane.getContentWidget().getShowingCard();
        var table = showingCard.getValue();
        this.changeEditState(false);
        this.clearOperator();
        this.fireEvent(BI.Controller.EVENT_CHANGE, this._editing, table);
        this.fireEvent(BI.TopPointerSavePane.EVENT_CANCEL, arguments)
    },

    doSave : function(){
        var showingCard = this.operatorEditPane.getContentWidget();
        var table = showingCard.getValue();
        this.changeEditState(false);
        widget.fireEvent(BI.TopPointerSavePane.EVENT_SAVE, table);
        this.refreshPreviewData(ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL)
    },

    setPreviewOperator : function(operator) {
        this.previewOperator = operator;
    },

    refreshPreviewData : function (v) {
        if(this.isError === true){
            v = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR;
        }
        this.setPreviewOperator(v);
        var args = BI.clone(this.currentData);
        if(BI.isNull(args)) {
            args = [];
            args.push([]);
            args.push([])
        }
        args.push(widget);
        args.push({});
        this.populatePreviewData.apply(this, args)
    },

    doPreviewChange : function (m, type) {
        var type = (this._editing || type === ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR ) ? type : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL;
        this.isError = (type === ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR);
        this.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, m, type)
    },

    fieldValuesCreator : function (field, callback) {
        var table = {};
        table.items = this._editing === true ? this.options.parents : [this.options];
        return BI.ETLReq.reqFieldValues({
            table : table,
            field : field
        }, callback);
    },

    filterChange : function (filter){
        var showingCard = this._statusAdd ? this.operatorPane.getContentWidget().getShowingCard() : this.operatorEditPane.getContentWidget();
        showingCard.filterChange(filter);
    },

});
BI.AnalysisOperatorCenter.DATA_CHANGE = "DATA_CHANGE";
BI.shortcut("bi.analysis_operator_center", BI.AnalysisOperatorCenter);