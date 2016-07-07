/**
 * @class BI.SelectSingleRelationTableField
 * @extend BI.Widget
 * create by young
 * 设置关联表的时候选择表字段
 */
BI.SelectSingleRelationTableField = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectSingleRelationTableField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-single-relation-table-field"
        })
    },

    _init: function () {
        BI.SelectSingleRelationTableField.superclass._init.apply(this, arguments);
        this.model = this.options.model;
        this.fieldId = this.options.field_id;
        this._initAllRelationTables();
        var self = this, packageStructure = BI.Utils.getAllGroupedPackagesTreeJSON4Conf();
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: this.element,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getAllPackages(function(packs){
            self.packs = packs;
            //TODO 暂时先选中第一个业务包
            var ids = BI.Utils.getAllPackageIDs4Conf();
            if(BI.isEmptyArray(ids)) {
                ids = [BI.Utils.getCurrentPackageId4Conf()]
            }
            self.searcher.setPackage(ids[0]);
            mask.destroy();
        });
        this.searcher = BI.createWidget({
            type: "bi.select_data_searcher",
            element: this.element,
            packages: packageStructure,
            itemsCreator: function (op, populate) {
                if (BI.isKey(op.searchType) && BI.isKey(op.keyword)) {
                    self._getSearchResult(op.searchType, op.keyword, op.packageId, function (result) {
                        populate(result.finded, result.matched);
                    });
                    return;
                }
                if (!op.node) {//根节点， 根据业务包找所有的表
                    populate(self._getTablesStructureByPackId(op.packageId));
                    return;
                }
                if (BI.isNotNull(op.node.isParent)) {
                    populate(self._getFieldsStructureByTableId(op.node.id));
                }
            }
        });
        this.searcher.on(BI.SelectDataSearcher.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.SelectSingleRelationTableField.EVENT_CLICK_ITEM, arguments);
        });
    },

    _getSearchResult: function (type, keyword, packageId, callback) {
        var self = this;
        var searchResult = [], matchResult = [];
        var translations = this.model.getTranslations();
        //选择了所有数据
        if (type & BI.SelectDataSearchSegment.SECTION_ALL) {
            var packages = BI.Utils.getAllPackageIDs4Conf();
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
            var result = [], map = [];
            BI.each(packages, function (i, pId) {
                var tables = BI.Utils.getTableIDsOfPackageID4Conf(pId);
                var items = [];
                BI.each(tables, function (i, tid) {
                    items = items.concat(self._getFieldsStructureByTableId(tid));
                });
                result.push(BI.Func.getSearchResult(items, keyword));
            });
            BI.each(result, function (i, sch) {
                BI.each(sch.finded, function (j, finded) {
                    if (!map[finded.pId]) {
                        var isInvalid = self.allRelationTables.contains(finded.pId) || finded.pId === self.model.getTableIdByFieldId(self.model.getFieldId());
                        searchResult.push({
                            id: finded.pId,
                            type: "bi.select_data_level0_node",
                            text: translations[finded.pId],
                            value: finded.pId,
                            isParent: true,
                            open: !isInvalid,
                            disabled: isInvalid
                        });
                        map[finded.pId] = true;
                    }
                });
                searchResult = searchResult.concat(sch.finded);
                matchResult = matchResult.concat(sch.matched);
            })
        }
        callback({finded: searchResult, matched: matchResult});
    },

    _initAllRelationTables: function(){
        var self = this;
        var relations = this.model.getRelations(), fieldId = this.model.getFieldId();
        //灰化：所有已与当前表建立过关联关系的表灰化
        //只需要遍历connectionSet
        var connectionSet = relations.connectionSet;
        this.allRelationTables = [];
        var tableId = this.model.getTableIdByFieldId(fieldId);
        BI.each(connectionSet, function (i, pf) {
            var primaryKey = pf.primaryKey, foreignKey = pf.foreignKey;
            //修改的就不用灰化了
            if(self.fieldId === primaryKey.field_id || self.fieldId === foreignKey.field_id) {
                return;
            }
            if (tableId === self.model.getTableIdByFieldId(primaryKey.field_id)) {
                self.allRelationTables.push(self.model.getTableIdByFieldId(foreignKey.field_id));
            } else if (tableId === self.model.getTableIdByFieldId(foreignKey.field_id)) {
                self.allRelationTables.push(self.model.getTableIdByFieldId(primaryKey.field_id));
            }
        });
    },

    _getTablesStructureByPackId: function (pId) {
        var self = this;
        var translations = this.model.getTranslations();
        var tableId = this.model.getTableIdByFieldId(this.model.getFieldId());
        var tablesStructure = [];
        //当前编辑业务包从Sharing Pool取
        if (pId === BI.Utils.getCurrentPackageId4Conf()) {
            var tables = BI.Utils.getCurrentPackageTables4Conf();
            BI.each(tables, function (id, table) {
                tablesStructure.push({
                    id: id,
                    type: "bi.select_data_level0_node",
                    text: translations[id],
                    value: id,
                    isParent: true,
                    open: false,
                    disabled:self.allRelationTables.contains(id) || id === tableId,
                    title: self.allRelationTables.contains(id) ? BI.i18nText("BI-Already_Relation_With_Current_Table") : translations[id]
                });
            });
        } else {
            var tables = self.packs[pId];
            BI.each(tables, function(id, table){
                tablesStructure.push({
                    id: id,
                    type: "bi.select_data_level0_node",
                    text: translations[id],
                    value: id,
                    isParent: true,
                    open: false,
                    disabled: self.allRelationTables.contains(id) || id === tableId,
                    title: self.allRelationTables.contains(id) ? BI.i18nText("BI-Already_Relation_With_Current_Table") : translations[id]
                });
            });
        }
        return tablesStructure;
    },

    _getFieldsStructureByTableId: function (tableId) {
        var translations = this.model.getTranslations();
        var fieldStructure = [];
        var tables = BI.Utils.getCurrentPackageTables4Conf();
        var fieldType = this.model.getFieldTypeByFieldId(this.model.getFieldId());
        if (BI.isNotNull(tables[tableId])) {
            var fields = [];
            BI.each(tables[tableId].fields[0], function(i, field){
                fieldType === field.field_type && fields.push(field);
            });
            BI.each(fields, function (i, field) {
                fieldStructure.push({
                    id: field.id,
                    pId: tableId,
                    type: "bi.select_data_level0_item",
                    fieldType: fieldType,
                    text: translations[field.id] || field.field_name,
                    value: {
                        field_id: field.id
                    }
                })
            });
        } else {
            BI.some(this.packs, function(pId, pack){
                return BI.some(pack, function(tId, fields){
                    if(tableId === tId) {
                        BI.each(fields, function(i, field){
                            if(field.field_type === fieldType) {
                                fieldStructure.push({
                                    id: field.id,
                                    pId: tableId,
                                    type: "bi.select_data_level0_item",
                                    fieldType: fieldType,
                                    text: translations[field.id] || field.field_name,
                                    value: {
                                        field_id: field.id
                                    }
                                })
                            }
                        });
                    }
                });
            });
        }
        return fieldStructure;
    }

});
BI.SelectSingleRelationTableField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.select_single_relation_table_field", BI.SelectSingleRelationTableField);