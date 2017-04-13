/**
 * Created by windy on 2017/4/12.
 */
BI.AnalysisETLOperatorMergeSheetPane = BI.inherit(BI.Widget, {

    props: {
        extraCls: "bi-analysis-etl-operator-merge-sheet-pane"
    },

    render: function(){
        var self = this;
        this.model = new BI.AnalysisETLMergeSheetModel({})
        return {
            type:"bi.htape",
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
                    }, {
                        type: "bi.button_group",
                        layouts: [{
                            type:"bi.vertical",
                            lgap:20,
                            rgap:20
                        }],
                        items:[],
                        ref: function(_ref){
                            self.table = _ref;
                        }
                    }]
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
                        items:[{
                            type:"bi.table_view",
                            isNeedResize: false,
                            isResizeAdapt: false,
                            isNeedFreeze: false,
                            freezeCols: [],
                            rowSize: 30,
                            columnSize:[195,195],
                            headerRowSize: 30,
                            header: [],
                            items: [],
                            ref: function(_ref){
                                self.mergeFields = _ref;
                            }
                        }]
                    }
                } ]
            }]
        }
    },

    createTable : function (tables) {
        return BI.map(tables, function (i, item) {
            return {
                text:item.tableName,
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
        this.editMerge()
    },

    _populate : function () {
        var tables = this.model.get(ETLCst.PARENTS);
        this.table.empty();
        this.table.populate(this.createTable(tables));
        this.mergeFields.populate(this.createCell(this.model.getMergeFieldsName(), "cell"), this.createCell([[tables[0].tableName,tables[1].tableName]], "header"))
        this.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, {
            update: BI.bind(this.model.update, this)
        }, ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL)
        this.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, this.model.getCopyValue("columns"))
    },


    editMerge : function () {
        var self = this;
        var currentSheets = BI.isFunction(this.options.currentTables)? this.options.currentTables(): {};
        var currentTable = [];
        BI.each(this.model.get(ETLCst.PARENTS), function (idx, item) {
            currentTable.push(item)
        })
        var sheetName = BI.isFunction(this.options.getSheetName) ? this.options.getSheetName(): null;
        var sheetName = sheetName || currentSheets.currentTable["tableName"];
        currentTable = BI.concat(currentTable, currentSheets.others)
        var self = this;
        BI.createWidget({
            type : "bi.analysis_etl_merge_sheet",
            element:BI.Layers.create(BICst.ANALYSIS_MERGE_LAYER, "body"),
            model : BI.extend(this.model.update(), {
                "tables":currentTable,
                "name":sheetName
            }),
            saveHandler : function(v) {
                var oldSheets = self.model.getSheets();
                if(BI.isFunction(self.options.setSheetName)){
                    self.options.setSheetName(v["name"])
                };
                self.fireEvent(BI.AnalysisETLOperatorCenter.DATA_CHANGE, v);
                self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.STOP_EDITING);
                if(!BI.isEqual(v["sheets"], oldSheets)) {
                    self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, v, currentTable)
                }
            },
            cancelHandler : function () {
                self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.STOP_EDITING)
            }
        })
        BI.Layers.show(BICst.ANALYSIS_MERGE_LAYER)
    }


})

BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE="MERGE_SHEET_CHANGE";
BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_DELETE="MERGE_SHEET_DELETE";
BI.AnalysisETLOperatorMergeSheetPane.STOP_EDITING="STOP_EDITING";
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.MERGE_SHEET + ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorMergeSheetPane);