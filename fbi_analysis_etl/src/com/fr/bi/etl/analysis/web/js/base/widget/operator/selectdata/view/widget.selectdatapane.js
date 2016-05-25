/**
 * @class BI.AnalysisETLSelectDataPane
 * @extend BI.Widget
 * 选择字段
 */
BI.AnalysisETLSelectDataPane = BI.inherit(BI.MVCWidget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLSelectDataPane.superclass._defaultConfig.apply(this, arguments), {})
    },
    
    _initController : function () {
        return  BI.AnalysisETLSelectDataPaneController;
    },

    _initView: function () {
        var self = this, packageStructure = BI.Utils.getAllGroupedPackagesTreeJSON();
        this.searcher = BI.createWidget({
            type: "bi.select_data_searcher",
            element: this.element,
            packages: packageStructure,
            itemsCreator: function (op, populate) {
                if (BI.isKey(op.searchType) && BI.isKey(op.keyword)) {
                    var result = self._getSearchResult(op.searchType, op.keyword, op.packageId);
                    populate(result.finded, result.matched);
                    return;
                }
                if (!op.node) {//根节点， 根据业务包找所有的表
                    populate(self._getTablesStructureByPackId(op.packageId));
                    return;
                }
                if (BI.isNotNull(op.node.isParent)) {
                    if (op.node.fieldType === BICst.COLUMN.DATE) {
                        populate(self._buildDateChildren(op.node.id));
                        return;
                    }
                    populate(self._getFieldsStructureByTableId(op.node.id));
                }
            }
        });

        this.searcher.on(BI.SelectDataSearcher.EVENT_CLICK_ITEM, function(){
           self.fireEvent(BI.SelectDataSearcher.EVENT_CLICK_ITEM, arguments)
        })


    },

    setEnabledValue: function (v) {
        this.searcher.setEnabledValue(v)
    },

    setEnable : function (v) {
        BI.AnalysisETLSelectDataPane.superclass.setEnable.apply(this, arguments)
        this.searcher.setEnable(v)
    },

    /**
     * 搜索结果
     * @param type
     * @param keyword
     * @param packageName
     * @returns {{finded: Array, matched: Array}}
     * @private
     */
    _getSearchResult: function (type, keyword, packageId) {
        var self = this;
        var searchResult = [], matchResult = [];
        //选择了所有数据
        if (type & BI.SelectDataSearchSegment.SECTION_ALL) {
            var packages = BI.Utils.getAllPackageIDs();
        } else {
            var packages = [packageId];
        }
        //选择了表
        if (type & BI.SelectDataSearchSegment.SECTION_TABLE) {
            var result = [];
            BI.each(packages, function (i, pid) {
                var items = self._getTablesStructureByPackId(pid);
                result.push(BI.Func.getSearchResult(items, keyword));
            })
            BI.each(result, function (i, sch) {
                searchResult = searchResult.concat(sch.finded);
                matchResult = matchResult.concat(sch.matched);
            })
        } else {
            var result = [], map = [];
            BI.each(packages, function (i, pid) {
                var tables = BI.Utils.getTableIDsOfPackageID(pid);
                var items = [];
                BI.each(tables, function (i, tid) {
                    items = items.concat(self._getFieldsStructureByTableId(tid));
                });
                result.push(BI.Func.getSearchResult(items, keyword));
            });
            BI.each(result, function (i, sch) {
                BI.each(sch.finded, function (j, finded) {
                    if (!map[finded.pId]) {
                        searchResult.push({
                            id: finded.pId,
                            type: "bi.analysis_etl_select_data_level0_node",
                            text: BI.Utils.getTableNameByID(finded.pId) || BI.Utils.getFieldNameByID(finded.pId),
                            title: BI.Utils.getTableNameByID(finded.pId) || BI.Utils.getFieldNameByID(finded.pId),
                            listener : function () {
                                self.controller.registerEvents(this, finded.pId)
                            },
                            value: finded.pId,
                            isParent: true,
                            open: true
                        });
                        map[finded.pId] = true;
                    }
                });
                searchResult = searchResult.concat(sch.finded);
                matchResult = matchResult.concat(sch.matched);
            })
        }
        return {
            finded: searchResult,
            matched: matchResult
        }
    },

    /**
     * 业务包中，所有表
     * @param packageName
     * @returns {Array}
     * @private
     */
    _getTablesStructureByPackId: function (packageId) {
        var self = this;
        var tablesStructure = [];
        var currentTables = BI.Utils.getTableIDsOfPackageID(packageId);
        BI.each(currentTables, function (i, tid) {
            tablesStructure.push({
                id: tid,
                type: "bi.analysis_etl_select_data_level0_node",
                text: BI.Utils.getTableNameByID(tid),
                title: BI.Utils.getTableNameByID(tid),
                listener : function () {
                    self.controller.registerEvents(this, tid)
                },
                value: tid,
                isParent: true,
                open: false
            });
        });
        return tablesStructure;
    },

    /**
     * 单个表展开，所有字段（包含相关表）
     * @param tableId
     * @returns {Array}
     * @private
     */
    _getFieldsStructureByTableId: function (tableId) {
        var self = this;
        var fieldStructure = this._getFieldStructureOfOneTable(tableId);
        //这里加上相关表
        var relationTables = BI.Utils.getPrimaryRelationTablesByTableID(tableId);
        if(BI.isNotEmptyArray(relationTables)){
            var relationTablesStructure = []
            BI.each(relationTables, function(i, rtId){
                relationTablesStructure.push({
                    id: rtId,
                    pId: BI.AnalysisETLSelectDataPane.RELATION_TABLE,
                    type: "bi.select_data_expander",
                    el: {
                        type: "bi.analysis_etl_select_data_level1_node",
                        text: BI.Utils.getTableNameByID(rtId),
                        title: BI.Utils.getTableNameByID(rtId),
                        listener : function () {
                            self.controller.registerEvents(this, rtId)
                        },
                        value: rtId,
                        isParent: true,
                        open: false
                    },
                    popup: {
                        items: self._getFieldStructureOfOneTable(rtId, true)
                    }
                });
            });
            fieldStructure.push({
                type: "bi.relation_tables_expander",
                el: {
                    id: BI.AnalysisETLSelectDataPane.RELATION_TABLE,
                    pId: tableId,
                    type: "bi.select_data_relation_tables_node",
                    text: BI.i18nText("BI-More_Foreign_Table") + ">>",
                    title: BI.i18nText("BI-More_Foreign_Table"),
                    value: BI.AnalysisETLSelectDataPane.RELATION_TABLE,
                    isParent: true,
                    open: false
                },
                popup: {
                    items: relationTablesStructure
                }
            })
        }
        return fieldStructure;
    },

    /**
     * 区别上面的无相关表
     * @param tableId
     * @param isRelation
     * @returns {Array}
     * @private
     */
    _getFieldStructureOfOneTable: function(tableId, isRelation){
        var fieldStructure = [];
        var self = this;
        //count, string, number
        BI.each(BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId), function (i, fid) {
            if(BI.Utils.getFieldIsUsableByID(fid) === false){
                return;
            }
            if (BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.COUNTER) {
                return;
            }
            var fieldName = BI.Utils.getFieldNameByID(fid);
            //日期类型-特殊处理
            if(BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.DATE){
                if(isRelation === true){
                    fieldStructure.push({
                        id: fid,
                        pId: tableId,
                        type: "bi.select_data_expander",
                        el: {
                            type: "bi.analysis_detail_select_data_level2_date_node",
                            text: fieldName,
                            title: fieldName,
                            listener : function () {
                                self.controller.registerEvents(this, tableId)
                            },
                            value: fid,
                            isParent: true,
                            open: false
                        },
                        popup: {
                            items: self._buildDateChildren(fid, isRelation)
                        }
                    })
                } else {
                    fieldStructure.push({
                        id: fid,
                        pId: tableId,
                        type: "bi.analysis_etl_select_data_level1_date_node",
                        fieldType: BI.Utils.getFieldTypeByID(fid),
                        listener : function () {
                            self.controller.registerEvents(this, tableId)
                        },
                        text: fieldName,
                        title: fieldName,
                        value: fid,
                        isParent: true
                    });
                    fieldStructure = fieldStructure.concat(self._buildDateChildren(fid, isRelation));
                }
            } else {
                fieldStructure.push({
                    id: fid,
                    pId: tableId,
                    type: isRelation ? "bi.analysis_etl_detail_select_data_level1_item" : "bi.analysis_etl_detail_select_data_level0_item",
                    fieldType: BI.Utils.getFieldTypeByID(fid),
                    listener : function () {
                        self.controller.registerEvents(this, tableId)
                    },
                    text: fieldName,
                    title: BI.Utils.getTableNameByID(tableId) + "." + fieldName,
                    value: fid
                })
            }
        });
        return fieldStructure;
    },

    /**
     * 日期类型的字段展开
     * @param fieldId
     * @returns {{id: string, pId: *, type: string, text: *, title: *, value: {fId: *, group: number}, drag: *}[]}
     * @private
     */
    _buildDateChildren: function (fieldId, isRelation) {
        var self = this;
        return [{
            id: fieldId + "year",
            pId: fieldId,
            type: isRelation ? "bi.analysis_etl_detail_select_data_level2_item" : "bi.analysis_etl_detail_select_data_level1_item",
            text: BI.i18nText("BI-Year_Fen"),
            title: BI.i18nText("BI-Year_Fen"),
            fieldType:BICst.COLUMN.NUMBER,
            value: {
                field_id: fieldId,
                group: BICst.GROUP.Y
            }
        }, {
            id: fieldId + "quarter",
            pId: fieldId,
            type: isRelation ? "bi.analysis_etl_detail_select_data_level2_item" : "bi.analysis_etl_detail_select_data_level1_item",
            text: BI.i18nText("BI-Quarter"),
            title: BI.i18nText("BI-Quarter"),
            fieldType:BICst.COLUMN.NUMBER,
            value: {
                field_id: fieldId,
                group: BICst.GROUP.S
            }
        }, {
            id: fieldId + "month",
            pId: fieldId,
            type: isRelation ? "bi.analysis_etl_detail_select_data_level2_item" : "bi.analysis_etl_detail_select_data_level1_item",
            text: BI.i18nText("BI-Multi_Date_Month"),
            title: BI.i18nText("BI-Multi_Date_Month"),
            fieldType:BICst.COLUMN.NUMBER,
            value: {
                field_id: fieldId,
                group: BICst.GROUP.M
            }
        }, {
            id: fieldId + "week",
            pId: fieldId,
            type: isRelation ? "bi.analysis_etl_detail_select_data_level2_item" : "bi.analysis_etl_detail_select_data_level1_item",
            text: BI.i18nText("BI-Week_XingQi"),
            title: BI.i18nText("BI-Week_XingQi"),
            fieldType:BICst.COLUMN.NUMBER,
            value: {
                field_id: fieldId,
                group: BICst.GROUP.W
            }
        }, {
            id: fieldId + "date",
            pId: fieldId,
            type: isRelation ? "bi.analysis_etl_detail_select_data_level2_item" : "bi.analysis_etl_detail_select_data_level1_item",
            text: BI.i18nText("BI-Date"),
            title: BI.i18nText("BI-Date"),
            fieldType:BICst.COLUMN.DATE,
            value: {
                field_id: fieldId,
                group: BICst.GROUP.YMD
            }
        }];
    }
});
BI.extend(BI.AnalysisETLSelectDataPane, {
    RELATION_TABLE: "__relation_table__"
});
$.shortcut("bi.analysis_etl_select_data_pane", BI.AnalysisETLSelectDataPane);