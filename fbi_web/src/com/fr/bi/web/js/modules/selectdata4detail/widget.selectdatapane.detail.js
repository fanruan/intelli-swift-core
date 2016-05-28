/**
 * Created by roy on 16/4/1.
 */
BI.DetailDetailTableSelectDataPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailDetailTableSelectDataPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-detail-select-data",
            wId: ""
        })
    },

    _init: function () {
        BI.DetailDetailTableSelectDataPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options, packageStructure = BI.Utils.getAllGroupedPackagesTreeJSON();
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

        this.searcher.on(BI.SelectDataSearcher.EVENT_CLICK_ITEM, function (value, ob) {
            if (BI.isObject(value)) {
                value = value.field_id;
            }
            var tableId = BI.Utils.getTableIdByFieldID(value);
            BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX + o.wId, ob.isSelected() ? tableId : "");
        });

        this.searcher.on(BI.SelectDataSearcher.EVENT_CLICK_PACKAGE, function () {
            var pId = this.getPackageId();
            BI.Utils.setCurrentSelectPackageID(pId);
        });

        var id = BI.Utils.getCurrentSelectPackageID();
        this.searcher.setPackage(id);

        var broadcast = function () {
            packageStructure = BI.Utils.getAllGroupedPackagesTreeJSON();
            self.searcher.populatePackages(packageStructure);
        };
        //当前组件的业务包更新
        BI.Broadcasts.on(BICst.BROADCAST.PACKAGE_PREFIX + o.wId, broadcast);
        //全局业务包更新
        BI.Broadcasts.on(BICst.BROADCAST.PACKAGE_PREFIX, broadcast);
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
                            type: "bi.detail_detail_select_data_level0_node",
                            text: BI.Utils.getTableNameByID(finded.pId),
                            title: BI.Utils.getTableNameByID(finded.pId),
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

    /**
     * 业务包中，所有表
     * @param packageId
     * @returns {Array}
     * @private
     */
    _getTablesStructureByPackId: function (packageId) {
        var tablesStructure = [], o = this.options;
        var currentTables = BI.Utils.getTableIDsOfPackageID(packageId);
        BI.each(currentTables, function (i, tid) {
            tablesStructure.push({
                id: tid,
                wId: o.wId,
                type: "bi.detail_detail_select_data_level0_node",
                text: BI.Utils.getTableNameByID(tid),
                title: BI.Utils.getTableNameByID(tid),
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
                        type: "bi.detail_detail_select_data_level1_node",
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
                    wId: o.wId,
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
                wId: o.wId,
                items: items
            });
        }

        //count, string, number
        BI.each(BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId), function (i, fid) {
            if (BI.Utils.getFieldIsUsableByID(fid) === false || viewFields.contains(fid)) {
                return;
            }
            if (BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.COUNTER) {
                return;
            }
            var fieldName = BI.Utils.getFieldNameByID(fid);
            //日期类型-特殊处理
            if (BI.Utils.getFieldTypeByID(fid) === BICst.COLUMN.DATE) {
                if (isRelation === true) {
                    fieldStructure.push({
                        id: fid,
                        pId: tableId,
                        type: "bi.select_data_expander",
                        el: {
                            type: "bi.detail_select_data_level2_date_node",
                            wId: o.wId,
                            text: fieldName,
                            title: fieldName,
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
                        wId: o.wId,
                        type: "bi.detail_select_data_level1_date_node",
                        fieldType: BI.Utils.getFieldTypeByID(fid),
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
                    wId: o.wId,
                    type: isRelation ? "bi.detail_select_data_level1_item" : "bi.detail_select_data_level0_item",
                    fieldType: BI.Utils.getFieldTypeByID(fid),
                    text: fieldName,
                    title: fieldName,
                    value: fid,
                    drag: self._createDrag(fieldName)
                })
            }
        });
        return fieldStructure;
    },

    /**
     * 拖拽事件
     * @param fieldName
     * @returns {{cursor: (BICst.cursorUrl|*), cursorAt: {left: number, top: number}, revert: boolean, drag: Function, helper: Function}}
     * @private
     */
    _createDrag: function (fieldName) {
        var self = this;
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
                                name = BI.i18nText("BI-Time_ShiKe") + "(" + name + ")";
                                break;
                        }
                        return {
                            name: name,
                            _src: {
                                id: fId.field_id + fId.group.type,
                                field_id: fId.field_id
                            },
                            type: BI.Utils.getDimensionTypeByFieldID(fId.field_id),
                            group: {type: fId.group.type}
                        }
                    }
                    return {
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
            }
        }
    },

    /**
     * 日期类型的字段展开
     * @param fieldId
     * @returns {{id: string, pId: *, type: string, text: *, title: *, value: {fId: *, group: number}, drag: *}[]}
     * @private
     */
    _buildDateChildren: function (fieldId, isRelation) {
        var o = this.options;
        var drag = this._createDrag(BI.Utils.getFieldNameByID(fieldId));
        return [{
            id: fieldId + BICst.GROUP.Y,
            pId: fieldId,
            wId: o.wId,
            type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
            fieldType: BICst.COLUMN.DATE,
            text: BI.i18nText("BI-Year_Fen"),
            title: BI.i18nText("BI-Year_Fen"),
            value: {
                field_id: fieldId,
                group: {type: BICst.GROUP.Y}
            },
            drag: drag
        }, {
            id: fieldId + BICst.GROUP.S,
            pId: fieldId,
            wId: o.wId,
            type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
            fieldType: BICst.COLUMN.DATE,
            text: BI.i18nText("BI-Quarter"),
            title: BI.i18nText("BI-Quarter"),
            value: {
                field_id: fieldId,
                group: {type: BICst.GROUP.S}
            },
            drag: drag
        }, {
            id: fieldId + BICst.GROUP.M,
            pId: fieldId,
            wId: o.wId,
            type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
            fieldType: BICst.COLUMN.DATE,
            text: BI.i18nText("BI-Multi_Date_Month"),
            title: BI.i18nText("BI-Multi_Date_Month"),
            value: {
                field_id: fieldId,
                group: {type: BICst.GROUP.M}
            },
            drag: drag
        }, {
            id: fieldId + BICst.GROUP.W,
            pId: fieldId,
            wId: o.wId,
            type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
            fieldType: BICst.COLUMN.DATE,
            text: BI.i18nText("BI-Week_XingQi"),
            title: BI.i18nText("BI-Week_XingQi"),
            value: {
                field_id: fieldId,
                group: {type: BICst.GROUP.W}
            },
            drag: drag
        }, {
            id: fieldId + BICst.GROUP.YMD,
            pId: fieldId,
            wId: o.wId,
            type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
            fieldType: BICst.COLUMN.DATE,
            text: BI.i18nText("BI-Date"),
            title: BI.i18nText("BI-Date"),
            value: {
                field_id: fieldId,
                group: {type: BICst.GROUP.YMD}
            },
            drag: drag
        }, {
            id: fieldId + BICst.GROUP.YMDHMS,
            pId: fieldId,
            wId: o.wId,
            type: isRelation ? "bi.detail_select_data_level2_item" : "bi.detail_select_data_level1_item",
            fieldType: BICst.COLUMN.DATE,
            text: BI.i18nText("BI-Time_ShiKe"),
            title: BI.i18nText("BI-Time_ShiKe"),
            value: {
                field_id: fieldId,
                group: {type: BICst.GROUP.YMDHMS}
            },
            drag: drag
        }];
    }
});
BI.extend(BI.DetailDetailTableSelectDataPane, {
    RELATION_TABLE: "__relation_table__"
});
$.shortcut("bi.detail_detail_table_select_data", BI.DetailDetailTableSelectDataPane);