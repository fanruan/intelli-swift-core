BI.DynamicTab = FR.extend(BI.MVCWidget, {

    _defaultConfig: function () {
        return BI.extend(BI.DynamicTab.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-sheet-tab-dynamic",
            model : {
                items: []
            },
            height: 30
        })
    },

    _initController : function (){
        return BI.DynamictabController;
    },

    _initModel : function (){
        return BI.DynamictabModel;
    },

    _initView: function () {
        var o = this.options;
        this.tabButton = BI.createWidget( {
            type: "bi.dynamic_tab_button",
            items :[]
        })
        var self = this;
        this.tabButton.on(BI.DynamicTabButton.ADD_SHEET, function(v){
            self.controller.addNewSheet({});
        })

        this.tabButton.on(BI.DynamicTabButton.MERGE_SHEET, function(v){
            //TODO 这里要选择不同的sheet做不同的事儿
            self.controller.chooseSheetForMerge();
        })

        this.tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            tab: this.tabButton.tab,
            defaultShowIndex:false,
            cardCreator: BI.bind(this._createTabs, this)
        });
        BI.createWidget({
            type:"bi.vtape",
            element: this.element,
            items: [{
                el: this.tab
            }, {
                el:this.tabButton,
                height: o.height
            }]
        })
    },

    _createTabs : function(v) {
        var item = this.controller.getTabItem(v);
        var tab = BI.createWidget({
            type: "bi.history_tab",
            cls : "bi-animate-right-in",
            allHistory:item.allHistory
        });
        var self = this;
        BI.defer(function () {
            self.registerChildWidget(v, tab,  {
                currentTables:function () {
                    return self.controller.getCurrentTables()
                },
                getSheetName : function () {
                    return self.controller.getSheetName(v)
                },
                setSheetName : function (name) {
                    self.controller.renameSheet(name, v)
                }
            })
        });
        var self = this;
        tab.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, function (data, oldSheets) {
            self.controller.changeMergeSheet(data, oldSheets, v)
        });

        tab.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_DELETE, function () {
            self.controller.deleteMergeSheet(v)
        });
        return tab;
    }


})
$.shortcut("bi.dynamic_tab", BI.DynamicTab);