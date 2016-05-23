/**
 * 选择文本字段面板
 *
 * Created by GUY on 2015/11/10.
 * @class BI.SelectDate4RealTime
 * @extend BI.Widget
 */
BI.SelectDate4RealTime = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectDate4RealTime.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        })
    },

    _init: function () {
        BI.SelectDate4RealTime.superclass._init.apply(this, arguments);
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
        this.searcher.on(BI.SelectDataSearcher.EVENT_CLICK_PACKAGE, function () {
            var pId = this.getPackageId();
            BI.Utils.setCurrentSelectPackageID(pId);
        });
        var id = BI.Utils.getCurrentSelectPackageID();
        this.searcher.setPackage(id);
    },

    _getSearchResult: function (type, keyword, packageId) {
        var self = this, o = this.options;
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
                            wId: o.wId,
                            type: "bi.detail_select_data_level0_node_4_realtime",
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
                wId: o.wId,
                type: "bi.detail_select_data_level0_node_4_realtime",
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
                if(BI.Utils.getFieldTypeByID(id) === BICst.COLUMN.DATE) {
                    viewFields.push(id);
                    items[position.row][position.col].value = id;
                }
            });
            if(viewFields.length > 0) {
                fieldStructure.push({
                    id: BI.UUID(),
                    pId: tableId,
                    type: "bi.excel_view",
                    items: items
                });
            }
        }

        BI.each(BI.Utils.getDateFieldIDsOfTableID(tableId), function (i, fid) {
            if (BI.Utils.getFieldIsUsableByID(fid) === false || viewFields.contains(fid)) {
                return;
            }
            var fName = BI.Utils.getFieldNameByID(fid);
            fieldStructure.push({
                id: fid,
                pId: tableId,
                type: isRelation ? "bi.detail_select_data_level1_item" : "bi.detail_select_data_level0_item",
                wId: o.wId,
                fieldType: BI.Utils.getFieldTypeByID(fid),
                text: fName,
                title: fName,
                value: fid,
                drag: self._createDrag(fName)
            })
        });
        return fieldStructure;
    },

    _createDrag: function (fieldName) {
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

$.shortcut("bi.select_date_4_realtime", BI.SelectDate4RealTime);