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
                        type: "bi.simple_select_data_level0_node",
                        layer: 0
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
                layer: 0,
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
        var map = {}, circleMap = {};
        var newFields = this._getAllRelativeFields(tableId, fields, circleMap);

        BI.each(newFields, function (i, field) {
            var fid = field.id;
            var fieldName = BI.Utils.getFieldNameByID(fid) || "";
            var title = (BI.Utils.getTableNameByID(tableId) || "") + "." + fieldName;
            fieldStructure.push(map[fid] = BI.extend({
                id: fid,
                pId: tableId,
                wId: o.wId,
                type: "bi.detail_select_data_level0_item",
                layer: 1,
                fieldType: BI.Utils.getFieldTypeByID(fid),
                text: fieldName,
                title: title,
                value: fid
            }, field))
        });

        if (BI.Utils.isSelfCircleTableByTableId(tableId)) {
            BI.each(fields, function (i, field) {
                var id = field.id;
                if (BI.Utils.getFieldIsCircleByID(id) === true) {
                    var fieldName = BI.Utils.getFieldNameByID(id) || "";
                    var title = (BI.Utils.getTableNameByID(tableId) || "") + "." + fieldName;
                    fieldStructure.push({
                        id: id,
                        pId: tableId,
                        type: "bi.select_data_expander",
                        text: fieldName,
                        el: BI.extend({
                            wId: o.wId,
                            text: fieldName,
                            title: title,
                            fieldType: BI.Utils.getFieldTypeByID(id),
                            value: id
                        }, field, {
                            type: "bi.select_data_level1_date_node",
                            layer: 1,
                            isParent: true,
                            open: false
                        }),
                        popup: {
                            items: self._getSelfCircleFieldsByFieldId(id, circleMap[id] || [])
                        }
                    });
                }
            });
        }
        var result = BI.Func.getSearchResult(fieldStructure, keyword);
        fields = result.matched.concat(result.finded);
        fieldStructure = [];
        BI.each(fields, function (i, f) {
            if (map[f.pId]) {
                fieldStructure.push(map[f.pId]);
            }
            fieldStructure.push(f);
        });
        return fieldStructure;
    },

    _getSelfCircleFieldsByFieldId: function (fieldId, foregion, isRelation) {
        var self = this, o = this.options;
        foregion || (foregion = []);
        var tableId = BI.Utils.getTableIdByFieldID(fieldId);
        var fieldStructure = [];
        BI.each(foregion, function (i, f) {
            var fid = f.id;
            var fieldName = BI.Utils.getFieldNameByID(fid) || "";
            var title = (BI.Utils.getTableNameByID(tableId) || "") + "." + fieldName;
            fieldStructure.push(BI.extend({
                id: fid,
                pId: tableId,
                wId: o.wId,
                type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
                layer: isRelation ? 3 : 2,
                fieldType: BI.Utils.getFieldTypeByID(fid),
                text: fieldName,
                title: title,
                value: {
                    field_id: fieldId,
                    relation: BI.Utils.getPathsFromFieldAToFieldB(fieldId, fid)[0][0]
                }
            }, f));
        });
        return fieldStructure;
    },


    _getAllRelativeFields: function (tableId, fields, map) {
        map = map || {};
        var newFields = [];
        if (BI.Utils.isSelfCircleTableByTableId(tableId)) {
            var fIds = [], fieldList = [];
            var relations = BI.Utils.getPathsFromTableAToTableB(tableId, tableId);
            BI.each(relations, function (i, path) {
                var fId = BI.Utils.getLastRelationForeignIdFromRelations(path);
                fIds.push(fId);
            });
            BI.each(fields, function (i, field) {
                var isCircle = BI.Utils.getFieldIsCircleByID(field.id);
                if (isCircle !== true && !fIds.contains(field.id)) {
                    newFields.push(field);
                }
                if (fIds.contains(field.id)) {
                    fieldList.push(field);
                }
            });
            BI.each(fields, function (i, field) {
                var isCircle = BI.Utils.getFieldIsCircleByID(field.id);
                if (isCircle === true) {
                    map[field.id] = fieldList;
                }
            });
        } else {
            newFields = fields;
        }
        return newFields;
    },

    _getFieldsStructureByTableId: function (tableId) {
        var fieldStructure = [];
        var self = this, o = this.options;

        var viewFields = [];
        var fields = o.fieldsCreator(tableId);
        var map = {};
        var newFields = this._getAllRelativeFields(tableId, fields, map);

        BI.each(newFields, function (i, field) {
            var fid = field.id;
            if (viewFields.contains(fid)) {
                return;
            }
            var fieldName = BI.Utils.getFieldNameByID(fid) || "";
            var title = (BI.Utils.getTableNameByID(tableId) || "") + "." + fieldName;
            //日期类型-特殊处理
            if (o.showDateGroup === true && BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.DATE) {
                var _type = "bi.detail_select_data_level1_item";
                fieldStructure.push({
                    id: fid,
                    pId: tableId,
                    wId: o.wId,
                    _type: field.type || _type,
                    type: "bi.detail_select_data_level1_date_node",
                    fieldType: BI.Utils.getFieldTypeByID(fid),
                    text: fieldName,
                    title: title,
                    value: fid,
                    isParent: true
                });
                fieldStructure = fieldStructure.concat(self._buildDateChildren(tableId, field, isRelation));
            } else {
                fieldStructure.push(BI.extend({
                    id: fid,
                    pId: tableId,
                    wId: o.wId,
                    type: "bi.detail_select_data_level0_item",
                    layer: 1,
                    fieldType: BI.Utils.getFieldTypeByID(fid),
                    text: fieldName,
                    title: title,
                    value: fid
                }, field))
            }
        });

        if (BI.Utils.isSelfCircleTableByTableId(tableId)) {
            BI.each(fields, function (i, field) {
                var id = field.id;
                if (BI.Utils.getFieldIsCircleByID(id) === true) {
                    var fieldName = BI.Utils.getFieldNameByID(id) || "";
                    var title = (BI.Utils.getTableNameByID(tableId) || "") + "." + fieldName;
                    fieldStructure.push({
                        id: id,
                        pId: tableId,
                        type: "bi.select_data_expander",
                        text: fieldName,
                        el: BI.extend({
                            wId: o.wId,
                            text: fieldName,
                            title: title,
                            fieldType: BI.Utils.getFieldTypeByID(id),
                            value: id
                        }, field, {
                            type: "bi.select_data_level1_date_node",
                            layer: 1,
                            isParent: true,
                            open: false
                        }),
                        popup: {
                            items: self._getSelfCircleFieldsByFieldId(id, map[id] || [])
                        }
                    });
                }
            });
        }
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
