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

    _getFieldsStructureByTableId: function (tableId) {
        var self = this, o = this.options;
        var fieldStructure = [];
        //string
        BI.each(BI.Utils.getNumberFieldIDsOfTableID(tableId), function (i, fid) {
            var fname = BI.Utils.getFieldNameByID(fid);
            fieldStructure.push({
                id: fid,
                pId: tableId,
                type: "bi.select_number_level0_item",
                wId: o.wId,
                fieldType: BI.Utils.getFieldTypeByID(fid),
                text: fname,
                value: fid,
                title: BI.Utils.getTableNameByID(tableId) + "." + fname,
                drag: self._createDrag(fname, tableId)
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