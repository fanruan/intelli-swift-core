/**
 * Created by Young's on 2016/5/9.
 */
BI.GeneralQuerySelectDataPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.GeneralQuerySelectDataPane.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.GeneralQuerySelectDataPane.superclass._init.apply(this, arguments);
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
                    populate(self._getFieldStructureOfOneTable(op.node.id));
                }
            }
        });
        this.searcher.on(BI.SelectDataSearcher.EVENT_CHANGE, function () {
            self.fireEvent(BI.GeneralQuerySelectDataPane.EVENT_CHANGE, self.searcher.getValue());
        });
        this.searcher.on(BI.SelectDataSearcher.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.GeneralQuerySelectDataPane.EVENT_CLICK_ITEM, arguments);
        });

        //TODO 暂时先选中第一个业务包
        var ids = BI.Utils.getAllPackageIDs();
        this.searcher.setPackage(ids[0]);
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
            });
            BI.each(result, function (i, sch) {
                searchResult = searchResult.concat(sch.finded);
                matchResult = matchResult.concat(sch.matched);
            })
        } else {
            var result = [], map = {};
            BI.each(packages, function (i, pid) {
                var tables = BI.Utils.getTableIDsOfPackageID(pid);
                var items = [];
                BI.each(tables, function (i, tid) {
                    items = items.concat(self._getFieldStructureOfOneTable(tid));
                });
                var rs = BI.Func.getSearchResult(items, keyword);
                result.push(rs);
            });
            BI.each(result, function (i, sch) {
                BI.each(sch.matched.concat(sch.finded), function (j, finded) {
                    if (!map[finded.pId]) {
                        searchResult.push({
                            id: finded.pId,
                            type: "bi.detail_select_data_level0_node",
                            text: BI.Utils.getTableNameByID(finded.pId) || BI.Utils.getFieldNameByID(finded.pId),
                            title: BI.Utils.getTableNameByID(finded.pId) || BI.Utils.getFieldNameByID(finded.pId),
                            value: finded.pId,
                            isParent: true,
                            open: true
                        });
                        map[finded.pId] = true;
                    }
                });
                searchResult = searchResult.concat(sch.matched).concat(sch.finded);
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
     * @param packageId
     * @returns {Array}
     * @private
     */
    _getTablesStructureByPackId: function (packageId) {
        var tablesStructure = [];
        var currentTables = BI.Utils.getTableIDsOfPackageID(packageId);
        BI.each(currentTables, function (i, tid) {
            tablesStructure.push({
                id: tid,
                type: "bi.detail_select_data_level0_node",
                text: BI.Utils.getTableNameByID(tid),
                title: BI.Utils.getTableNameByID(tid),
                value: tid,
                isParent: true,
                open: false
            });
        });
        return tablesStructure;
    },

    _getFieldStructureOfOneTable: function (tableId) {
        var fieldStructure = [];

        BI.each(BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId), function (i, fid) {
            if (BI.Utils.getFieldIsUsableByID(fid) === false || BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.COUNTER) {
                return;
            }
            var fieldName = BI.Utils.getFieldNameByID(fid);
            fieldStructure.push({
                id: fid,
                pId: tableId,
                type: "bi.general_query_select_data_item",
                fieldType: BI.Utils.getFieldTypeByID(fid),
                text: fieldName,
                title: BI.Utils.getTableNameByID(tableId) + "." + fieldName,
                value: fid
            })
        });
        return fieldStructure;
    }
});
BI.GeneralQuerySelectDataPane.EVENT_CHANGE = "EVENT_CHANGE";
BI.GeneralQuerySelectDataPane.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.general_query_select_data_pane", BI.GeneralQuerySelectDataPane);
