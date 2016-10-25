/**
 * 业务包字段选择服务
 *
 * Created by GUY on 2016/5/28.
 *
 * @class BI.PackageSelectDataService
 * @extend BI.Widget
 */
BI.PackageSelectDataService = BI.inherit(BI.Widget, {
    _const: {
        FIELD_GAP: 25
    },
    _defaultConfig: function () {
        return BI.extend(BI.PackageSelectDataService.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-package-select-data-service",
            wId: "",
            isDefaultInit: true,
            showRelativeTables: false,
            showExcelView: false,
            showDateGroup: false,
            showTime: false,    //时刻
            packageCreator: function () {
                return BI.Utils.getAllGroupedPackagesTreeJSON();
            },
            tablesCreator: function () {
                return [];
            },
            fieldsCreator: function () {
                return [];
            }
        })
    },

    _init: function () {
        BI.PackageSelectDataService.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var packageStructure = o.packageCreator();
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
                if (BI.isKey(op.node._keyword)) {
                    populate(self._getFieldsStructureByTableIdAndKeyword(op.node.id, op.node._keyword), op.node._keyword);
                    return;
                }
                if (BI.isNotNull(op.node.isParent)) {
                    if (op.node.fieldType === BICst.COLUMN.DATE) {
                        var newNode = BI.clone(op.node);
                        delete newNode.children;
                        delete newNode.isParent;
                        newNode.type = newNode._type;
                        populate(self._buildDateChildren(op.node.pId, newNode));
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
        this.searcher.on(BI.SelectDataSearcher.EVENT_CLICK_ITEM, function (value, ob) {
            if (BI.isKey(o.wId)) {
                if (BI.isObject(value)) {
                    value = value.field_id;
                }
                var tableId = BI.Utils.getTableIdByFieldID(value);
                BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX + o.wId, ob.isSelected() ? tableId : "");
            }
            self.fireEvent(BI.PackageSelectDataService.EVENT_CLICK_ITEM, arguments);
        });
        if (o.isDefaultInit === true) {
            var id = BI.Utils.getCurrentSelectPackageID();
            this.searcher.setPackage(id);
        }

        var broadcast = function () {
            packageStructure = o.packageCreator();
            self.searcher.populatePackages(packageStructure);
        };
        if (BI.isKey(o.wId)) {
            //当前组件的业务包更新
            BI.Broadcasts.on(BICst.BROADCAST.PACKAGE_PREFIX + o.wId, broadcast);
        }
        //全局业务包更新
        BI.Broadcasts.on(BICst.BROADCAST.PACKAGE_PREFIX, broadcast);
    },

    _getTitleByTableId: function (tableId) {
        var tableName = BI.Utils.getTableNameByID(tableId);
        return BI.Utils.getPackageNameByID(BI.Utils.getPackageIDByTableID(tableId)) + "." + tableName || "";
    },

    _getTitleByFieldId: function (fieldId) {
        var fieldName = BI.Utils.getFieldNameByID(fieldId);
        var tableId = BI.Utils.getTableIdByFieldID(fieldId);
        var tableName = BI.Utils.getTableNameByID(tableId);
        return BI.Utils.getPackageNameByID(BI.Utils.getPackageIDByTableID(tableId)) + "." + tableName + "." + fieldName || "";
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
        var self = this, o = this.options;
        var searchResult = [], matchResult = [];
        //选择了所有数据
        if (type & BI.SelectDataSearchSegment.SECTION_ALL) {
            var packages = this._getAllPackageIds();
        } else {
            var packages = [packageId];
        }
        var isRelation = packages.length === 1;
        //选择了表
        if (type & BI.SelectDataSearchSegment.SECTION_TABLE) {
            var result = [];
            BI.each(packages, function (i, pid) {
                var items = self._getTablesStructureByPackId(pid, isRelation);
                result.push(BI.Func.getSearchResult(items, keyword));
            });
            BI.each(result, function (i, sch) {
                searchResult = searchResult.concat(sch.finded);
                matchResult = matchResult.concat(sch.matched);
            })
        } else {
            var result = [], map = {}, tables = [], field2TableMap = {};
            BI.each(packages, function (i, pid) {
                tables = self._getTablesStructureByPackId(pid, isRelation);
                var items = [];
                BI.each(tables, function (i, table) {
                    var fields = self._getFieldsStructureByTableId(table.id || table.value);
                    BI.each(fields, function (i, filed) {
                        field2TableMap[filed.id || filed.value] = table;
                    });
                    items = items.concat(fields);
                });
                var rs = BI.Func.getSearchResult(items, keyword);
                result.push(rs);
            });
            BI.each(result, function (i, sch) {
                BI.each(sch.matched.concat(sch.finded), function (j, finded) {
                    if (!map[finded.pId]) {
                        if (BI.Utils.getTableNameByID(finded.pId)) {
                            var title = self._getTitleByTableId(finded.pId);
                        } else if (BI.Utils.getFieldNameByID(finded.pId)) {
                            var title = self._getTitleByFieldId(finded.pId);
                        }
                        searchResult.push(BI.extend({
                            id: finded.pId,
                            wId: o.wId,
                            text: BI.Utils.getTableNameByID(finded.pId) || BI.Utils.getFieldNameByID(finded.pId) || "",
                            title: title,
                            value: finded.pId,
                            type: "bi.detail_select_data_level0_node",
                            layer: 0
                        }, field2TableMap[finded.id || finded.value], {
                            isParent: true,
                            open: true,
                            _keyword: keyword
                        }));
                        map[finded.pId] = true;
                    }
                });
                //searchResult = searchResult.concat(sch.matched).concat(sch.finded);
                matchResult = matchResult.concat(sch.matched);
            })
        }
        return {
            finded: searchResult,
            matched: matchResult
        }
    },

    _getAllPackageIds: function () {
        var o = this.options;
        var packages = o.packageCreator();
        packages = BI.Tree.transformToTreeFormat(packages);
        var tree = new BI.Tree();
        tree.initTree(packages);
        var result = [];
        tree.traverse(function (node) {
            if (node.isLeaf()) {
                result.push(node.id || node.value);
            }
        });
        return result;
    },

    /**
     * 业务包中，所有表
     * @param packageId
     * @param isRelation
     * @returns {Array}
     * @private
     */
    _getTablesStructureByPackId: function (packageId, isRelation) {
        var self = this, o = this.options;
        var tablesStructure = [];
        var currentTables = o.tablesCreator(packageId);
        var currentTablesIds = BI.pluck(currentTables, "id");
        var relationAndCurrentTables = currentTables;
        if (isRelation === true) {
            BI.each(currentTablesIds, function (id, tId) {
                var relationAndCurrentTablesIds = BI.pluck(relationAndCurrentTables, "id");
                var tmpRelationAndCurrentTables = BI.filter(o.tablesCreator(tId, true), function (i, t) {
                    return !BI.contains(relationAndCurrentTablesIds, t.id);
                });
                relationAndCurrentTables = BI.concat(relationAndCurrentTables, tmpRelationAndCurrentTables);
            });
        }
        BI.each(relationAndCurrentTables, function (i, table) {
            var showText = BI.contains(currentTablesIds, table.id) ? (BI.Utils.getTableNameByID(table.id) || "")
                : (BI.Utils.getTableNameByID(table.id) || "") + "(" + BI.i18nText("BI-Relation_Table") + ")";
            tablesStructure.push(BI.extend({
                id: table.id,
                wId: o.wId,
                type: "bi.detail_select_data_level0_node",
                layer: 0,
                text: showText,
                title: self._getTitleByTableId(table.id),
                value: table.id,
                isParent: true,
                open: false
            }, table));
        });
        this.primaryFieldIds = BI.Utils.getAllPrimaryKeyByTableIds(BI.pluck(currentTables, "id"));
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
        if (o.showRelativeTables === true) {
            //这里加上相关表
            var relationTables = o.tablesCreator(tableId, true);
            BI.remove(relationTables, function (i, t) {
                return t.id === tableId;
            });
            if (BI.isNotEmptyArray(relationTables)) {
                var relationTablesStructure = [];
                BI.each(relationTables, function (i, table) {
                    relationTablesStructure.push({
                        id: table.id,
                        pId: BI.PackageSelectDataService.RELATION_TABLE,
                        type: "bi.relation_table_expander",
                        el: BI.extend({
                            type: "bi.detail_select_data_level1_node",
                            layer: 1,
                            wId: o.wId,
                            text: BI.Utils.getTableNameByID(table.id) || "",
                            title: self._getTitleByTableId(table.id),
                            value: table.id
                        }, table, {
                            isParent: true,
                            open: false
                        }),
                        popup: {
                            type: "bi.select_data_loader",
                            items: self._getFieldStructureOfOneTable(table.id, true)
                        }
                    });
                });
                fieldStructure.push({
                    type: "bi.relation_tables_expander",
                    el: {
                        id: BI.PackageSelectDataService.RELATION_TABLE,
                        pId: tableId,
                        wId: o.wId,
                        type: "bi.select_data_relation_tables_node",
                        text: BI.i18nText("BI-More_Foreign_Table") + ">>",
                        title: BI.i18nText("BI-More_Foreign_Table"),
                        value: BI.PackageSelectDataService.RELATION_TABLE,
                        isParent: true,
                        open: false
                    },
                    popup: {
                        items: relationTablesStructure
                    }
                })
            }
        }
        return fieldStructure;
    },

    _getFieldsStructureByTableIdAndKeyword: function (tableId, keyword) {
        var fieldStructure = [];
        var self = this, o = this.options;
        var fields = o.fieldsCreator(tableId);
        var fieldMap = {}, map = {};
        var newFields = BI.PackageSelectDataService.getAllRelativeFields(tableId, fields, map);

        BI.each(newFields, function (i, field) {
            var fid = field.id;
            var fieldName = BI.Utils.getFieldNameByID(fid) || "";
            //日期类型-特殊处理
            if (o.showDateGroup === true && BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.DATE) {
                var _type = "bi.detail_select_data_level1_item";
                fieldStructure.push(fieldMap[fid] = {
                    id: fid,
                    pId: tableId,
                    wId: o.wId,
                    _type: field.type || _type,
                    type: "bi.detail_select_data_level1_date_node",
                    layer: 1,
                    fieldType: BI.Utils.getFieldTypeByID(fid),
                    text: fieldName,
                    title: self._getTitleByFieldId(fid),
                    value: fid,
                    isParent: true
                });
                fieldStructure = fieldStructure.concat(self._buildDateChildren(tableId, field));
            } else {
                fieldStructure.push(fieldMap[fid] = BI.extend({
                    id: fid,
                    pId: tableId,
                    wId: o.wId,
                    type: "bi.detail_select_data_level0_item",
                    isPrimaryKey: BI.contains(self.primaryFieldIds, fid),
                    layer: 1,
                    fieldType: BI.Utils.getFieldTypeByID(fid),
                    text: fieldName,
                    title: self._getTitleByFieldId(fid),
                    value: self._getTitleByFieldId(fid),
                    drag: self._createDrag(fieldName)
                }, field))
            }
        });

        if (BI.isNotEmptyObject(map)) {
            BI.each(fields, function (i, field) {
                var id = field.id;
                if (BI.isNotEmptyArray(map[id])) {
                    var fieldName = BI.Utils.getFieldNameByID(id) || "";
                    fieldStructure.push({
                        id: id,
                        pId: tableId,
                        type: "bi.expander",
                        text: fieldName,
                        el: BI.extend({
                            wId: o.wId,
                            text: fieldName,
                            title: self._getTitleByFieldId(id),
                            keyword: keyword,
                            fieldType: BI.Utils.getFieldTypeByID(id),
                            value: id
                        }, field, {
                            type: "bi.select_data_level1_date_node",
                            layer: 1,
                            isParent: true,
                            open: false
                        }),
                        popup: {
                            type: "bi.select_data_loader",
                            items: self._getSelfCircleFieldsByFieldId(id, map[id] || [])
                        }
                    });
                }
            });
        }
        var result = BI.Func.getSearchResult(fieldStructure, keyword);
        fields = result.matched.concat(result.finded);
        fieldStructure = [];
        BI.each(fields, function (i, f) {
            if (fieldMap[f.pId]) {
                fieldStructure.push(fieldMap[f.pId]);
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

            fieldStructure.push(BI.extend({
                id: fid,
                pId: tableId,
                wId: o.wId,
                type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
                isPrimaryKey: BI.contains(self.primaryFieldIds, fid),
                layer: isRelation ? 3 : 2,
                fieldType: BI.Utils.getFieldTypeByID(fid),
                text: fieldName,
                title: self._getTitleByFieldId(fid),
                value: fid,
                drag: self._createDrag(fieldName)
            }, f));
        });
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
        var self = this, o = this.options, c = this._const;

        var viewFields = [];
        if (o.showExcelView === true) {
            //Excel View
            var excelView = BI.Utils.getExcelViewByTableId(tableId);
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
                if (viewFields.length > 0) {
                    fieldStructure.push({
                        id: BI.UUID(),
                        pId: tableId,
                        type: "bi.excel_view",
                        tableId: tableId
                    });
                }
            }
        }

        var fields = o.fieldsCreator(tableId, isRelation);
        if ((fields.length === 1 && BI.Utils.getFieldTypeByID(fields[0].id)) === BICst.COLUMN.COUNTER
            || fields.length === 0) {
            fieldStructure.push({
                type: "bi.label",
                value: BI.UUID(),
                text: BI.i18nText("BI-(Empty)"),
                pId: tableId,
                id: BI.UUID(),
                wId: o.wId,
                textAlign: "left",
                lgap: isRelation ? c.FIELD_GAP * 2 : c.FIELD_GAP,
                disabled: true
            });
            return fieldStructure;
        }
        var map = {};
        var newFields = BI.PackageSelectDataService.getAllRelativeFields(tableId, fields, map);

        BI.each(newFields, function (i, field) {
            var fid = field.id;
            if (viewFields.contains(fid)) {
                return;
            }
            var fieldName = BI.Utils.getFieldNameByID(fid) || "";
            //日期类型-特殊处理
            if (o.showDateGroup === true && BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.DATE) {
                var _type = isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item";
                if (isRelation === true) {
                    fieldStructure.push({
                        id: fid,
                        pId: tableId,
                        type: "bi.expander",
                        el: {
                            type: "bi.detail_select_data_level2_date_node",
                            layer: 2,
                            wId: o.wId,
                            _type: field.type || _type,
                            text: fieldName,
                            title: self._getTitleByFieldId(fid),
                            value: fid,
                            isParent: true,
                            open: false
                        },
                        popup: {
                            type: "bi.select_data_loader",
                            items: self._buildDateChildren(tableId, field, isRelation)
                        }
                    })
                } else {
                    fieldStructure.push({
                        id: fid,
                        pId: tableId,
                        wId: o.wId,
                        _type: field.type || _type,
                        type: "bi.detail_select_data_level1_date_node",
                        layer: 1,
                        fieldType: BI.Utils.getFieldTypeByID(fid),
                        text: fieldName,
                        title: self._getTitleByFieldId(fid),
                        value: fid,
                        isParent: true
                    });
                    //fieldStructure = fieldStructure.concat(self._buildDateChildren(tableId, field, isRelation));
                }
            } else {
                fieldStructure.push(BI.extend({
                    id: fid,
                    pId: tableId,
                    wId: o.wId,
                    type: isRelation ? "bi.detail_select_data_level1_item" : "bi.detail_select_data_level0_item",
                    layer: isRelation ? 2 : 1,
                    isPrimaryKey: BI.contains(self.primaryFieldIds, fid),
                    fieldType: BI.Utils.getFieldTypeByID(fid),
                    text: fieldName,
                    title: self._getTitleByFieldId(fid),
                    value: fid,
                    drag: self._createDrag(fieldName)
                }, field))
            }
        });

        if (BI.isNotEmptyObject(map)) {
            BI.each(fields, function (i, field) {
                var id = field.id;
                if (BI.isNotEmptyArray(map[id])) {
                    var fieldName = BI.Utils.getFieldNameByID(id) || "";
                    fieldStructure.push({
                        id: id,
                        pId: tableId,
                        type: "bi.expander",
                        text: fieldName,
                        el: BI.extend({
                            wId: o.wId,
                            text: fieldName,
                            title: self._getTitleByFieldId(id),
                            fieldType: BI.Utils.getFieldTypeByID(id),
                            value: id
                        }, field, {
                            type: isRelation ? "bi.select_data_level2_date_node" : "bi.select_data_level1_date_node",
                            layer: isRelation ? 2 : 1,
                            isParent: true,
                            open: false
                        }),
                        popup: {
                            type: "bi.select_data_loader",
                            items: self._getSelfCircleFieldsByFieldId(id, map[id] || [], isRelation)
                        }
                    });
                }
            });
        }
        return fieldStructure;
    },

    /**
     * 拖拽事件
     * @param fieldName
     * @returns {{cursor: (BICst.cursorUrl|*), cursorAt: {left: number, top: number}, revert: boolean, drag: Function, helper: Function}}
     * @private
     */
    _createDrag: function (fieldName) {
        var self = this, o = this.options;
        return {
            cursor: BICst.cursorUrl,
            cursorAt: {left: 5, top: 5},
            //revert: true,
            drag: function (e, ui) {
                //ui.helper.css({
                //    left: ui.position.left,
                //    top: ui.position.top,
                //    cursor: BICst.cursorUrl
                //});
            },
            helper: function () {
                var text = fieldName;
                var fields = self.searcher.getValue();
                if (fields.length > 1) {
                    text = BI.i18nText("BI-All_Field_Count", fields.length);
                }
                var data = BI.map(fields, function (idx, fId) {
                    if (BI.has(fId, "group")) {
                        var name = BI.Utils.getFieldNameByID(fId.field_id);
                        switch (fId.group.type) {
                            case BICst.GROUP.Y:
                                name = BI.i18nText("BI-Year_Fen") + "(" + name + ")";
                                break;
                            case BICst.GROUP.S:
                                name = BI.i18nText("BI-Quarter") + "(" + name + ")";
                                break;
                            case BICst.GROUP.M:
                                name = BI.i18nText("BI-Multi_Date_Month") + "(" + name + ")";
                                break;
                            case BICst.GROUP.W:
                                name = BI.i18nText("BI-Week_XingQi") + "(" + name + ")";
                                break;
                            case BICst.GROUP.YMD:
                                name = BI.i18nText("BI-Date") + "(" + name + ")";
                                break;
                            case BICst.GROUP.YMDHMS:
                                name = BI.i18nText("BI-Time_ShiKe") + "(" + name + ")"
                        }
                        return {
                            name: name,
                            _src: {
                                id: fId.field_id + fId.group.type,
                                field_id: fId.field_id,
                                table_id: BI.Utils.getTableIdByFieldID(fId.field_id)
                            },
                            type: BI.Utils.getDimensionTypeByFieldID(fId.field_id),
                            group: {type: fId.group.type}
                        }
                    }
                    if (BI.has(fId, "target_relation")) {
                        var name = BI.Utils.getFieldNameByID(fId.field_id);
                        if (BI.Utils.getFieldIsCircleByID(fId.field_id) === true) {
                            name = BI.Utils.getFieldNameByID(fId.field_id) + "." + BI.Utils.getFieldNameByID(BI.Utils.getForeignIdFromRelation(fId.target_relation[0]));
                        }
                        if (BI.Utils.isControlWidgetByWidgetId(o.wId)) {
                            name = BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(fId.field_id)) + '.' + name;
                        }
                        return {
                            name: name,
                            _src: {
                                id: fId.field_id,
                                field_id: fId.field_id,
                                table_id: BI.Utils.getTableIdByFieldID(fId.field_id),
                                target_relation: fId.target_relation
                            },
                            type: BI.Utils.getDimensionTypeByFieldID(fId.field_id)
                        }
                    }
                    name = BI.Utils.getFieldNameByID(fId);
                    if (o.wId && BI.Utils.isControlWidgetByWidgetId(o.wId)) {
                        name = BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(fId)) + '.' + name;
                    }
                    return {
                        name: name,
                        _src: {
                            id: fId,
                            field_id: fId,
                            table_id: BI.Utils.getTableIdByFieldID(fId)
                        },
                        type: BI.Utils.getDimensionTypeByFieldID(fId)
                    };
                });
                var help = BI.createWidget({
                    type: "bi.helper",
                    data: {data: data},
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
            },
            start: function (event, ui) {
                //通知region
                BI.Broadcasts.send(BICst.BROADCAST.FIELD_DRAG_START, self.searcher.getValue());
            },
            stop: function (event, ui) {
                BI.Broadcasts.send(BICst.BROADCAST.FIELD_DRAG_STOP);
            }
        }
    },

    /**
     * 日期类型的字段展开
     * @param fieldId
     * @returns {{id: string, pId: *, type: string, text: *, title: *, value: {fId: *, group: number}, drag: *}[]}
     * @private
     */
    _buildDateChildren: function (tableId, field, isRelation) {
        var o = this.options;
        var fieldId = field.id || field.value;
        var fieldName = field.text || BI.Utils.getFieldNameByID(fieldId) || "";
        var drag = this._createDrag(fieldName);
        var prefix = this._getTitleByFieldId(fieldId) + ".";

        var children = [BI.extend({
            wId: o.wId,
            type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
            fieldType: BICst.COLUMN.DATE,
            drag: drag
        }, field, {
            id: fieldId + BICst.GROUP.Y,
            pId: fieldId,
            text: BI.i18nText("BI-Year_Fen"),
            title: prefix + BI.i18nText("BI-Year_Fen"),
            layer: isRelation ? 3 : 2,
            value: {
                field_id: fieldId,
                group: {type: BICst.GROUP.Y}
            }
        }), BI.extend({
            wId: o.wId,
            type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
            fieldType: BICst.COLUMN.DATE,
            drag: drag
        }, field, {
            id: fieldId + BICst.GROUP.S,
            pId: fieldId,
            text: BI.i18nText("BI-Quarter"),
            title: prefix + BI.i18nText("BI-Quarter"),
            layer: isRelation ? 3 : 2,
            value: {
                field_id: fieldId,
                group: {type: BICst.GROUP.S}
            }
        }), BI.extend({
            wId: o.wId,
            type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
            fieldType: BICst.COLUMN.DATE,
            drag: drag
        }, field, {
            id: fieldId + BICst.GROUP.M,
            pId: fieldId,
            text: BI.i18nText("BI-Multi_Date_Month"),
            title: prefix + BI.i18nText("BI-Multi_Date_Month"),
            layer: isRelation ? 3 : 2,
            value: {
                field_id: fieldId,
                group: {type: BICst.GROUP.M}
            }
        }), BI.extend({
            wId: o.wId,
            type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
            fieldType: BICst.COLUMN.DATE,
            drag: drag
        }, field, {
            id: fieldId + BICst.GROUP.W,
            pId: fieldId,
            text: BI.i18nText("BI-Week_XingQi"),
            title: prefix + BI.i18nText("BI-Week_XingQi"),
            layer: isRelation ? 3 : 2,
            value: {
                field_id: fieldId,
                group: {type: BICst.GROUP.W}
            }
        }), BI.extend({
            wId: o.wId,
            type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
            fieldType: BICst.COLUMN.DATE,
            drag: drag
        }, field, {
            id: fieldId + BICst.GROUP.YMD,
            pId: fieldId,
            text: BI.i18nText("BI-Date"),
            title: prefix + BI.i18nText("BI-Date"),
            layer: isRelation ? 3 : 2,
            value: {
                field_id: fieldId,
                group: {type: BICst.GROUP.YMD}
            }
        })];
        //时刻加在明细表里面
        if (o.showTime === true) {
            children.push(BI.extend({
                wId: o.wId,
                type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
                fieldType: BICst.COLUMN.DATE,
                drag: drag
            }, field, {
                id: fieldId + BICst.GROUP.YMDHMS,
                pId: fieldId,
                text: BI.i18nText("BI-Time_ShiKe"),
                title: prefix + BI.i18nText("BI-Time_ShiKe"),
                layer: isRelation ? 3 : 2,
                value: {
                    field_id: fieldId,
                    group: {type: BICst.GROUP.YMDHMS}
                }
            }));
        }
        return children;
    },

    setPackage: function (id) {
        this.searcher.setPackage(id);
    },

    setEnable: function (b) {
        BI.PackageSelectDataService.superclass.setEnable.apply(this, arguments);
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
BI.PackageSelectDataService.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
BI.extend(BI.PackageSelectDataService, {
    RELATION_TABLE: "__relation_table__",

    getAllRelativeFields: function (tableId, fields, map) {
        map = map || {};
        var newFields = [];
        var sset = new Set(), store = [], map2Id = {}, res = {};
        BI.each(fields, function (i, field) {
            var fieldName = BI.Utils.getOriginalFieldNameByID(field.id);
            if (fieldName) {
                var name = fieldName.split("-")[0];
                map2Id[fieldName] = field.id;
                if (sset.has(name)) {
                    store.push(name);
                } else {
                    sset.add(name);
                }
            }
        });
        if (store.length > 0 &&
            BI.every(store, function (i, name) {
                return map2Id[name] != null;
            })) {
            BI.each(fields, function (i, field) {
                var fieldName = BI.Utils.getOriginalFieldNameByID(field.id);
                var name = fieldName.split("-")[0];
                var contains = store.contains(name);
                if (name !== fieldName && contains) {
                    if (!res[name]) {
                        res[name] = [];
                    }
                    res[name].push(field);
                } else if (!contains) {
                    newFields.push(field);
                }
            });
            BI.each(res, function (name, fs) {
                map[map2Id[name]] = fs;
            })
        } else {
            newFields = fields;
        }
        return newFields;
    }
});
$.shortcut("bi.package_select_data_service", BI.PackageSelectDataService);
