BI.AnalysisETLOperatorCenter = FR.extend(BI.MVCWidget, {

    _constant : {
        pointerWidth:30,
        padding:10,
        operatorPaneHeight:312
    },


    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorCenter.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-center",
            showContent: true,
            contentItem : {
                value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.CHOOSE_FIELD,
                type:"bi.center_adapt",
                items:[{
                    type:"bi.button",
                    text:BI.i18nText('BI-Edit'),
                    width:120
                }]
            },
            isCurrentTheLastOperator: function(){
                return true;
            },
            previewOperator : ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA
        })
    },


    _initController: function () {
        return BI.AnalysisETLOperatorCenterController;
    },

    _initView: function () {
        var self = this, o = this.options;
        this.combos = [];
        this.title = BI.createWidget({
            type:"bi.analysis_operator_title"
        })

        BI.each(this.title.getTitleButtons(), function(idx, button){
            self._createBubbleCombo(button);
        });

        this.title.on(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, function(v){
            self.controller.setCurrentEditOperator(v);
            var result = BI.find(self.title.getTitleButtons(), function(idx, button){
                if(!o.isCurrentTheLastOperator() && button.getValue() === v.getValue()){
                    self.combos[idx].showView();
                    return true;
                }
            });
            if(BI.isNull(result)){
                self.fireEvent(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, arguments);
                self.controller.showOperatorPane(v);
            }
        })

        this.title.on(BI.AnalysisOperatorTitle.EVENT_SAVE, function(v){
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_SAVE, arguments)
        })
        this.title.on(BI.AnalysisOperatorTitle.EVENT_STATE_CHANGE, function(v){
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_STATE_CHANGE, arguments)
        })

        this.previewTable = BI.createWidget({
            type: "bi.analysis_etl_preview_table",
            cls: "bi-analysis-tab-table",
            header:[],
            items:[],
            fieldValuesCreator : function(){
                return self.controller.fieldValuesCreator.apply(self, arguments);
            },
            nameValidationController: o.nameValidationController
        })
        this.previewTable.on(BI.AnalysisETLPreviewTable.EVENT_FILTER, function(filter){
            self.controller.filterChange(filter);
        })
        this.previewTable.on(BI.AnalysisETLPreviewTable.DELETE_EVENT, function(v){
            self.fireEvent(BI.AnalysisETLPreviewTable.DELETE_EVENT, v)
        })

        this.previewTable.on(BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN, function(){
            self.fireEvent(BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN, arguments)
        })

        this.previewTable.on(BI.AnalysisETLPreviewTable.EVENT_RENAME, function(index, name){
            self.fireEvent(BI.AnalysisETLOperatorCenter.EVENT_RENAME, index, name)
        })
        this.operatorCard = BI.createWidget({
            type:"bi.card",
            items : this._createOperatorCard()
        })

        this.operatorPane = BI.createWidget({
            type:"bi.top_pointer_save_pane",
            pointerWidth:this._constant.pointerWidth,
            contentItem:this.operatorCard,
            cancelHandler : function () {
                return BI.isFunction(self.operatorCard.getShowingCard().cancelHandler)
                    && self.operatorCard.getShowingCard().cancelHandler()
            },
            saveHandler : function (editing) {
                return BI.isFunction(self.operatorCard.getShowingCard().saveHandler)
                    && self.operatorCard.getShowingCard().saveHandler(editing)
            },

            checkBeforeSave : function () {
                return self.controller.checkBeforeSave(self.operatorCard.getShowingCard().update())
            }
        })
        this.registerSimpleWidget(this.operatorPane)
        this.operatorEditPane = BI.createWidget({
            type:"bi.top_pointer_save_pane",
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
                return self.controller.checkBeforeSave(self.operatorEditPane.getContentWidget().update())
            }
        })
        this.registerSimpleWidget(this.operatorEditPane)
        this.operatorEditPane.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, arguments)
        })
        this.operatorPane.on(BI.TopPointerSavePane.EVENT_CANCEL, function(){
            self.controller.clearOperator()
            self.fireEvent(BI.TopPointerSavePane.EVENT_CANCEL, arguments)
        })
        this.operatorPane.on(BI.TopPointerSavePane.EVENT_SAVE, function(){
            self.controller.doNewSave();
        })
        this.operatorEditPane.on(BI.TopPointerSavePane.EVENT_INVALID, function(){
            self.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, arguments)
        })
        this.operatorEditPane.on(BI.TopPointerSavePane.EVENT_FIELD_VALID, function(){
            self.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, arguments)
        })
        this.operatorEditPane.on(BI.TopPointerSavePane.EVENT_CANCEL, function(){
            self.controller.changeEditState(false);
            self.fireEvent(BI.TopPointerSavePane.EVENT_CANCEL, arguments)
        })
        this.operatorEditPane.on(BI.AnalysisETLOperatorCenter.DATA_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorCenter.DATA_CHANGE, arguments)
        })
        this.operatorEditPane.on(BI.TopPointerSavePane.EVENT_EDIT, function(){
            self.controller.changeEditState(true)
            self.controller.refreshPreviewData(self.options.contentItem.value.operatorType)
        })
        this.operatorEditPane.on(BI.TopPointerSavePane.EVENT_SAVE, function(){
            self.controller.doSave();
        })

        this.operatorEditPane.on(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, function (model, type) {
            self.controller.doPreviewChange(model, type)

        })

        this.operatorEditPane.on(BI.AnalysisETLOperatorAbstractController.VALID_CHANGE, function (v) {
            self.title.setEnable(v, BI.i18nText("BI-Current_Error"))
        })

        this.operatorEditPaneItem = {
            el : this.operatorEditPane,
            height:0
        }
        this.operatorPaneItem = {
            el : this.operatorPane,
            height:0
        }
        this.vtapeItem =[{
            el : this.title,
            height : 40
        }, this.operatorPaneItem,
            this.operatorEditPaneItem,{
                el : {
                    type:"bi.border",
                    cls : "preview",
                    items:{
                        north:{
                            type:"bi.layout",
                            height:10
                        },
                        center:this.previewTable,
                        south:{
                            type:"bi.layout",
                            height:10
                        },
                        east:{
                            type:"bi.layout",
                            width:10
                        },
                        west:{
                            type:"bi.layout",
                            width:10
                        },
                    }
                }
            }]
        this.vtape = BI.createWidget({
            type:"bi.vtape",
            element : this.element,
            cls:"bi-elt-operator-select-data-center",
            items:this.vtapeItem
        })
    },

    _createBubbleCombo: function(button){
        var self = this;
        var combo = BI.createWidget({
            type: "bi.bubble_combo",
            el: {},
            element: button,
            popup: {
                type: "bi.bubble_bar_popup_view",
                buttons: [{
                    value: BI.i18nText(BI.i18nText("BI-Sure")),
                    handler: function () {
                        combo.hideView();
                        self.fireEvent(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, self.controller.getCurrentEditOperator());
                        self.controller.showOperatorPane(self.controller.getCurrentEditOperator());
                    }
                }, {
                    value: BI.i18nText("BI-Cancel"),
                    level: "ignore",
                    handler: function () {
                        combo.hideView();
                    }
                }],
                el: {
                    type: "bi.vertical_adapt",
                    items: [{
                        type: "bi.label",
                        whiteSpace: "normal",
                        text: BI.i18nText("BI-Current_Operator_May_Cause_Problem_With_After_Confirm_To_Continue"),
                        cls: "delete-label",
                        textAlign: "left",
                        width: 300
                    }],
                    width: 300,
                    height: 100,
                    hgap: 20
                },
                maxHeight: 140,
                minWidth: 340
            }
        });
        this.combos.push(combo);
        return combo;
    },

    _createOperatorCard : function () {
        var items = [];
        var self = this;
        BI.each(ETLCst.ANALYSIS_TABLE_OPERATOR, function(idx, item){
            var pane = BI.createWidget({
                type: item.operatorType + ETLCst.ANALYSIS_TABLE_PANE,
                data :[]
            })
            pane.on(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, function () {
                self.operatorCard.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, arguments)
            });
            pane.on(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, function (model, type) {
                self.title.setEnable(model.isDefaultValue())
                self.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, arguments)
            })
            var card = {
                cardName:item.value,
                el : pane
            }
            items.push(card)
        })
        return items;
    },

    setEnable : function(v) {
        this.controller.setEnable(v);
    },

    populatePreview : function() {
        this.controller.populatePreviewData.apply(this.controller, arguments)
    },

    setPreviewOperator : function(operator) {
        this.controller.setPreviewOperator(operator)
    }
    //,
    //
    // hideOperatorPane : function () {
    //     this.controller.hideOperatorPane()
    // }
})
BI.AnalysisETLOperatorCenter.DATA_CHANGE="DATA_CHANGE";
BI.AnalysisETLOperatorCenter.EVENT_RENAME = "event_rename";
$.shortcut("bi.analysis_etl_operator_center", BI.AnalysisETLOperatorCenter);