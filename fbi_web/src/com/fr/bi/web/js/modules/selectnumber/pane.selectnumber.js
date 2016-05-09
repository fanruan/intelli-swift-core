/**
 * 选择文本字段面板
 *
 * Created by GUY on 2015/11/10.
 * @class BI.SelectNumberPane
 * @extend BI.Widget
 */
BI.SelectNumberPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectNumberPane.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        })
    },

    _init: function () {
        BI.SelectNumberPane.superclass._init.apply(this, arguments);
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
                if (op.node.isParent != undefined) {
                    if (op.node.fieldType === BICst.COLUMN.DATE) {
                        populate(self._buildDateChildren(op.node.id));
                        return;
                    }
                    populate(self._getFieldsStructureByTableId(op.node.id));
                }
            }
        });
        //TODO 暂时先选中第一个业务包
        var ids = BI.Utils.getAllPackageIDs();
        this.searcher.setPackage(ids[0]);
    },

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
                BI.each(sch.matched.concat(sch.finded), function (j, finded) {
                    if (!map[finded.pId]) {
                        searchResult.push({
                            id: finded.pId,
                            type: "bi.select_data_level0_node",
                            text: BI.Utils.getTableNameByID(finded.pId),
                            value: finded.pId,
                            isParent: true,
                            open: true
                        });
                        map[finded.pId] = true;
                    }
                });
                searchResult = searchResult.concat(sch.matched.concat(sch.finded));
                matchResult = matchResult.concat(sch.matched);
            })
        }
        return {
            finded: searchResult,
            matched: matchResult
        }
    },

    _getTablesStructureByPackId: function (packageId) {
        var o = this.options;
        var tablesStructure = [];
        var currentTables = BI.Utils.getTableIDsOfPackageID(packageId);
        BI.each(currentTables, function (i, tid) {
            tablesStructure.push({
                id: tid,
                type: "bi.select_data_level0_node",
                wId: o.wId,
                text: BI.Utils.getTableNameByID(tid),
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
        var self = this, o = this.options;
        var fieldStructure = this._getFieldStructureOfOneTable(tableId);
        //这里加上相关表
        var relationTables = BI.Utils.getPrimaryRelationTablesByTableID(tableId);
        BI.remove(relationTables, tableId);
        if (BI.isNotEmptyArray(relationTables)) {
            var relationTablesStructure = [];
            BI.each(relationTables, function (i, rtId) {
                relationTablesStructure.push({
                    id: rtId,
                    pId: BI.DetailDetailTableSelectDataPane.RELATION_TABLE,
                    type: "bi.select_data_expander",
                    el: {
                        type: "bi.detail_select_data_level1_node",
                        wId: o.wId,
                        text: BI.Utils.getTableNameByID(rtId),
                        title: BI.Utils.getTableNameByID(rtId),
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
                    id: BI.DetailDetailTableSelectDataPane.RELATION_TABLE,
                    pId: tableId,
                    type: "bi.select_data_relation_tables_node",
                    text: BI.i18nText("BI-More_Foreign_Table") + ">>",
                    title: BI.i18nText("BI-More_Foreign_Table"),
                    value: BI.DetailDetailTableSelectDataPane.RELATION_TABLE,
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
    _getFieldStructureOfOneTable: function (tableId, isRelation) {
        var fieldStructure = [];
        var self = this, o = this.options;

        //Excel View
        var excelView = BI.Utils.getExcelViewByTableId(tableId);
        var viewFields = [];
        if (BI.isNotNull(excelView) && BI.isNotEmptyObject(excelView.positions)) {
            var excel = excelView.excel;
            var positions = excelView.positions;
            var items = [];
            BI.each(excel, function (i, row) {
                var item = [];
                BI.each(row, function (j, cell) {
                    item.push({text: cell})
                });
                items.push(item);
            });
            BI.each(positions, function (id, position) {
                viewFields.push(id);
                items[position.row][position.col].value = id;
            });
            fieldStructure.push({
                id: BI.UUID(),
                pId: tableId,
                type: "bi.excel_view",
                items: items
            });
        }

        BI.each(BI.Utils.getNumberFieldIDsOfTableID(tableId), function (i, fid) {
            if (BI.Utils.getFieldIsUsableByID(fid) === false || viewFields.contains(fid)) {
                return;
            }
            var fieldName = BI.Utils.getFieldNameByID(fid);
            fieldStructure.push({
                id: fid,
                pId: tableId,
                type: isRelation ? "bi.detail_select_data_level1_item" : "bi.detail_select_data_level0_item",
                wId: o.wId,
                fieldType: BI.Utils.getFieldTypeByID(fid),
                text: fieldName,
                title: fieldName,
                value: fid,
                drag: self._createDrag(fieldName)
            })
        });
        return fieldStructure;
    },

    _createDrag: function (fieldName, tableId) {
        var self = this;
        return {
            cursor: BICst.cursorUrl,
            cursorAt: {left: 5, top: 5},
            drag: function (e, ui) {
                ui.helper.css({
                    left: ui.position.left,
                    top: ui.position.top,
                    cursor: BICst.cursorUrl
                });
            },
            helper: function () {
                var text = fieldName;
                var fields = self.searcher.getValue();
                if (fields.length > 1) {
                    text = BI.i18nText("BI-All_Field_Count", fields.length);
                }
                var data = BI.map(fields, function (idx, fId) {
                    return {
                        id: fId,
                        name: BI.Utils.getFieldNameByID(fId),
                        _src: {
                            id: fId,
                            field_id: fId
                        },
                        type: BI.Utils.getDimensionTypeByFieldID(fId)
                    };
                });
                var help = BI.createWidget({
                    type: "bi.helper",
                    data: {"data": data},
                    text: text
                });
                BI.createWidget({
                    type: "bi.absolute",
                    element: "body",
                    items: [{
                        el: help
                    }]
                });
                return help.element;
            }
        }
    }
});

$.shortcut("bi.select_number_pane", BI.SelectNumberPane);