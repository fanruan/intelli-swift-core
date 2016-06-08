BI.HistoryTab = FR.extend(BI.MVCWidget, {

    _constant : {
        width:180
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
        var o = this.options;
        this.tabButton = BI.createWidget({
            type: "bi.history_button_group",
            items:[],
            allHistory: o.allHistory,
            layouts: [{
                type: "bi.vertical",
                scrolly:true
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
            element: this.element,
            items: [{
                el: this.tab
            }, {
                el: {
                    type:"bi.vtape",
                    cls:"bi-history-tab-button-group",
                    items : [{
                        el:{
                            type:"bi.center_adapt",
                            cls:"bi-history-tab-title",
                            items :[{
                                type:"bi.label",
                                text: BI.i18nText("BI-History")
                            }]
                        },
                        height:40
                    },{
                        el : {
                            type:"bi.layout"
                        },
                        height:10
                    },{
                        el : this.tabButton
                    }]
                },
                width: this._constant.width
            }]
        })

        // this.controller.addDefaultItems();
        // this.controller.selectLastTab();
    },

    _createTabs : function(v) {
        var self = this;
        var tab = BI.createWidget({
            type:this.controller.getOperatorTypeByValue(v)
        })

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

        tab.on(BI.AnalysisOperatorTitle.EVENT_SAVE, function(){
            self.controller.clickTitleSave(v)
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
BI.HistoryTab.VALID_CHANGE = "valid_change"
$.shortcut("bi.history_tab",BI.HistoryTab)