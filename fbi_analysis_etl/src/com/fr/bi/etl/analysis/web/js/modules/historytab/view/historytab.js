BI.HistoryTab = FR.extend(BI.MVCWidget, {

    _constant : {
        width:190
    },

    _defaultConfig: function () {
        return BI.extend(BI.HistoryTab.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-history-tab",
            allHistory:false,
            model : {
                items: []
            }
        })
    },


    _initController: function () {
        return BI.HistoryTabColltroller;
    },

    _initModel : function () {
        return BI.HistoryTabModel
    },

    _initView: function () {
        var self = this, o = this.options;
        this.tabButton = BI.createWidget({
            type: "bi.history_button_group",
            items:[],
            allHistory: o.allHistory,
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            tab: this.tabButton,
            defaultShowIndex:false,
            cardCreator: BI.bind(this._createTabs, this)
        });

        BI.createWidget({
            type:"bi.htape",
            element: this,
            bgap: 10,
            items: [{
                el: this.tab
            }, {
                el: {
                    type: "bi.center",
                    cls: "histroy-tab-east",
                    rgap: 10,
                    vgap: 10,
                    items: [{
                        type:"bi.vtape",
                        cls:"bi-history-tab-button-group",
                        items : [{
                            el:{
                                type:"bi.center_adapt",
                                cls:"bi-history-tab-title",
                                items :[{
                                    type:"bi.label",
                                    text: BI.i18nText("BI-History_Record")
                                }]
                            },
                            height:40
                        },{
                            el : {
                                type:"bi.layout"
                            },
                            height:10
                        },{
                            el: {
                                type: "bi.center",
                                lgap: -1,
                                scrolly: null,
                                items: [this.tabButton]
                            }
                        }]
                    }]
                },
                width: this._constant.width
            }]
        })

        // this.controller.addDefaultItems();
        // this.controller.selectLastTab();
    },

    isCurrentTheLastOperator: function(){
        var v = this.tabButton.getValue()[0];
        var position = this.controller.getIndexByValue(v);
        return position === this.controller.getSavedItems().length - 1;
    },

    _createTabs : function(v) {
        var self = this;
        var preConfig = {};
        switch (this.controller.getOperatorTypeByValue(v)) {
            case ETLCst.ANALYSIS_ETL_PAGES.FILTER:
                preConfig = {
                    value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.FILTER
                };
                break;
            case ETLCst.ANALYSIS_ETL_PAGES.GROUP_SUMMARY:
                preConfig = {
                    value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.GROUP
                };
                break;
            case ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN:
                preConfig = {
                    value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.ADD_COLUMN
                };
                break;
            case ETLCst.ANALYSIS_ETL_PAGES.USE_PART_FIELDS:
                preConfig = {
                    value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.USE_PART_FIELDS
                };
                break;
            case ETLCst.ANALYSIS_ETL_PAGES.MERGE_SHEET:
                preConfig = {
                    value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.MERGED_TABLE
                };
                break;
            case ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA:
                preConfig = {
                    extraCls: "bi-analysis-etl-operator-select-data",
                    type: ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA
                }
        }

        var tab = BI.createWidget(BI.extend({
            type: "bi.analysis_operator_pane",
            isCurrentTheLastOperator: BI.bind(this.isCurrentTheLastOperator, this)
        }, preConfig));

        BI.nextTick(function () {
            self.controller.populateOneTab(v);
        })
        tab.on(BI.Controller.EVENT_CHANGE, function(){
            if(arguments[0] === false) {
                self.controller.addNewSheet(arguments[1])
            }
        })

        tab.on(BI.TopPointerSavePane.EVENT_SAVE, function(model){
            self.controller.saveOneSheet(model);
        });

        tab.on(BI.TopPointerSavePane.EVENT_FIELD_VALID, function(fields){
            self.controller.refreshValidFields(v, fields);
            self.fireEvent(BI.HistoryTab.VALID_CHANGE)
        });

        tab.on(BI.TopPointerSavePane.EVENT_INVALID, function(title){
            self.controller.setInvalid(v, title);
            self.fireEvent(BI.HistoryTab.VALID_CHANGE)
        });

        tab.on(BI.AnalysisOperatorTitle.EVENT_SAVE, function(value, desc){
            self.controller.clickTitleSave(v, value, desc);
        });

        tab.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, arguments)
        })
        tab.on(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, function(v){
            self.controller.tempAddButton(v.getValue())
        })
        tab.on(BI.TopPointerSavePane.EVENT_CANCEL, function(v){
            self.controller.cancelTempAddButton()
        })
        tab.on(BI.MergeHistory.CANCEL, function () {
            self.controller.selectLastTab()
        })
        return tab;
    }

})
BI.HistoryTab.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
BI.HistoryTab.VALID_CHANGE = "valid_change"
BI.shortcut("bi.history_tab",BI.HistoryTab)