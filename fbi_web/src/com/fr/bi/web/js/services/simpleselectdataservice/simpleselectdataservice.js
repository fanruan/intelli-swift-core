/**
 * 简单字段选择服务
 *
 * Created by GUY on 2016/5/30.
 *
 * @class BI.SimpleSelectDataService
 * @extend BI.Widget
 */
BI.SimpleSelectDataService = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SimpleSelectDataService.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-simple-select-data-service",
            isDefaultInit: false,
            tablesCreator: function () {
                return [];
            },
            fieldsCreator: function () {
                return [];
            }
        })
    },

    _init: function () {
        BI.SimpleSelectDataService.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.searcher = BI.createWidget({
            type: "bi.simple_select_data_searcher",
            element: this.element,
            itemsCreator: function (op, populate) {
                if (BI.isKey(op.searchType) && BI.isKey(op.keyword)) {
                    var result = self._getSearchResult(op.searchType, op.keyword);
                    populate(result.finded, result.matched);
                    return;
                }
                if (!op.node) {//根节点， 根据业务包找所有的表
                    populate(self._getTablesStructure());
                    return;
                }
                if (BI.isKey(op.node._keyword)) {
                    populate(self._getFieldsStructureByTableIdAndKeyword(op.node.id, op.node._keyword), op.node._keyword);
                    return;
                }
                if (BI.isNotNull(op.node.isParent)) {
                    populate(self._getFieldsStructureByTableId(op.node.id));
                }
            }
        });
        this.searcher.on(BI.SelectDataSearcher.EVENT_CLICK_ITEM, function (value, ob) {
            self.fireEvent(BI.SimpleSelectDataService.EVENT_CLICK_ITEM, arguments);
        });
        if (o.isDefaultInit === true) {
            this.populate();
        }
    },

    /**
     * 搜索结果
     * @param type
     * @param keyword
     * @param packageName
     * @returns {{finded: Array, matched: Array}}
     * @private
     */
    _getSearchResult: function (type, keyword) {
        var self = this, o = this.options;
        var searchResult = [], matchResult = [];
        //选择了表
        if (type & BI.SelectDataSearchSegment.SECTION_TABLE) {
            var items = self._getTablesStructure();
            var result = BI.Func.getSearchResult(items, keyword);
            searchResult = result.finded;
            matchResult = result.matched;
        } else {
            var map = {}, field2TableMap = {};
            var tables = o.tablesCreator();
            var items = [];
            BI.each(tables, function (i, table) {
                var fields = self._getFieldsStructureByTableId(table.id || table.value);
                BI.each(fields, function (i, filed) {
                    field2TableMap[filed.id || filed.value] = table;
                });
                items = items.concat(fields);
            });
            var result = BI.Func.getSearchResult(items, keyword);
            BI.each(result.matched.concat(result.finded), function (j, finded) {
                if (!map[finded.pId]) {
                    searchResult.push(BI.extend({
                        id: finded.pId,
                        wId: o.wId,
                        text: BI.Utils.getTableNameByID(finded.pId) || BI.Utils.getFieldNameByID(finded.pId) || "",
                        title: BI.Utils.getTableNameByID(finded.pId) || BI.Utils.getFieldNameByID(finded.pId) || "",
                        value: finded.pId,
                        type: "bi.simple_select_data_level0_node"
                    }, field2TableMap[finded.id || finded.value], {
                        isParent: true,
                        open: true,
                        _keyword: keyword
                    }));
                    map[finded.pId] = true;
                }
            });
            //searchResult = searchResult.concat(result.matched).concat(result.finded);
            matchResult = matchResult.concat(result.matched);
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
    _getTablesStructure: function () {
        var o = this.options;
        var tablesStructure = [];
        var currentTables = o.tablesCreator();
        BI.each(currentTables, function (i, table) {
            tablesStructure.push(BI.extend({
                id: table.id,
                type: "bi.simple_select_data_level0_node",
                text: BI.Utils.getTableNameByID(table.id) || "",
                title: BI.Utils.getTableNameByID(table.id) || "",
                value: table.id,
                isParent: true,
                open: false
            }, table));
        });
        return tablesStructure;
    },

    _getFieldsStructureByTableIdAndKeyword: function (tableId, keyword) {
        var fieldStructure = [];
        var self = this, o = this.options;
        var fields = o.fieldsCreator(tableId);
        var search = [];
        BI.each(fields, function (i, field) {
            var fid = field.id;
            var fieldName = BI.Utils.getFieldNameByID(fid);
            search.push({
                id: fid,
                text: fieldName
            })
        });
        var result = BI.Func.getSearchResult(search, keyword);
        fields = result.matched.concat(result.finded);
        BI.each(fields, function (i, field) {
            var fid = field.id;
            var fieldName = BI.Utils.getFieldNameByID(fid) || "";
            var title = (BI.Utils.getTableNameByID(tableId) || "") + "." + fieldName;
            fieldStructure.push(BI.extend({
                id: fid,
                pId: tableId,
                type: "bi.select_data_level0_item",
                fieldType: BI.Utils.getFieldTypeByID(fid),
                text: fieldName,
                title: title,
                value: fid
            }, field));
        });
        return fieldStructure;
    },

    _getFieldsStructureByTableId: function (tableId) {
        var fieldStructure = [];
        var self = this, o = this.options;
        var fields = o.fieldsCreator(tableId);
        BI.each(fields, function (i, field) {
            var fid = field.id;
            var fieldName = BI.Utils.getFieldNameByID(fid) || "";
            var title = (BI.Utils.getTableNameByID(tableId) || "") + "." + fieldName;
            fieldStructure.push(BI.extend({
                id: fid,
                pId: tableId,
                type: "bi.select_data_level0_item",
                fieldType: BI.Utils.getFieldTypeByID(fid),
                text: fieldName,
                title: title,
                value: fid
            }, field));
        });
        return fieldStructure;
    },

    setEnable: function (b) {
        BI.SimpleSelectDataService.superclass.setEnable.apply(this, arguments);
        this.searcher.setEnable(b);
    },

    setEnabledValue: function (v) {
        this.searcher.setEnabledValue(v);
    },

    stopSearch: function () {
        this.searcher.stopSearch();
    },

    populate: function () {
        this.searcher.populate.apply(this.searcher, arguments);
    }
});
BI.SimpleSelectDataService.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.simple_select_data_service", BI.SimpleSelectDataService);
