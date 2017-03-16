BI.AnalysisETLOperatorMergeSheetPane = FR.extend(BI.MVCWidget, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorMergeSheetPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-merge-sheet-pane",
            model : {

            }
        })
    },

    _initModel : function () {
        return BI.AnalysisETLMergeSheetModel
    },

    _initController : function () {
        return BI.AnalysisETLOperatorMergeSheetPaneController;
    },


    _initView: function () {
        this.table = BI.createWidget({
            type:"bi.vertical",
            lgap:20,
            rgap:20,
            items:[]
        })
        this.mergeFields = BI.createWidget({
            type:"bi.table_view",
            isNeedResize: false,
            isResizeAdapt: false,
            isNeedFreeze: false,
            freezeCols: [],
            rowSize: 30,
            columnSize:[195,195],
            headerRowSize: 30,
            header: [],
            items: []
        })
        BI.createWidget({
            type:"bi.htape",
            element:this.element,
            items:[{
                el:{
                    type:"bi.vtape",
                    cls:"left",
                    items:[{
                        el:{
                            type:"bi.left_right_vertical_adapt",
                            cls:"title",
                            height:40,
                            items: {
                                left:[{
                                    el: {
                                        type:"bi.label",
                                        lgap:20,
                                        text:BI.i18nText("BI-Merge_Tables")
                                    }
                                }]
                            }
                        },
                        height:40
                    }, {
                        type:"bi.layout",
                        height:10
                    }, this.table]
                },
                width:190

            }, {
                type:"bi.vtape",
                cls : "right",
                items:[{
                    el:{
                        type:"bi.left_right_vertical_adapt",
                        cls:"title",
                        height:40,
                        items: {
                            left:[{
                                el: {
                                    type:"bi.label",
                                    lgap:10,
                                    text:BI.i18nText("BI-Merge_Fields")
                                }
                            }]
                        }
                    },
                    height:40
                }, {
                    el : {
                        type:"bi.vtape",
                        lgap:10,
                        tgap:10,
                        items:[this.mergeFields]
                    },
                } ]
            }]
        })

    },

    createTable : function (tables) {
        return BI.map(tables, function (i, item) {
            return {
                text:item.table_name,
                type:"bi.label",
                height:20,
                textAlign:"left"
            }

        })
    },

    createCell : function (cell, cls) {
        return BI.map(cell, function (i, items) {
            return BI.map(items, function (j, item) {
                return BI.extend({
                    type: "bi.label",
                    textAlign: "center",
                    cls: cls,
                    text:item
                });
            });
        })
    },


    saveHandler : function (isEditing) {
       this.controller.editMerge()
    }



})

BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE="MERGE_SHEET_CHANGE";
BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_DELETE="MERGE_SHEET_DELETE";
BI.AnalysisETLOperatorMergeSheetPane.STOP_EDITING="STOP_EDITING";
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.MERGE_SHEET + ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorMergeSheetPane);