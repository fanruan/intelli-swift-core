/**
 *
 * 树状结构的表格
 *
 * Created by GUY on 2015/8/12.
 * @class BI.DynamicSummaryTreeTable
 * @extends BI.Widget
 */
BI.DynamicSummaryTreeTable = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DynamicSummaryTreeTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dynamic-summary-tree-table",
            logic: { //冻结的页面布局逻辑
                dynamic: false
            },

            isNeedResize: false,//是否需要调整列宽
            isResizeAdapt: true,//是否需要在调整列宽或区域宽度的时候它们自适应变化

            isNeedFreeze: false,//是否需要冻结单元格
            freezeCols: [], //冻结的列号,从0开始,isNeedFreeze为tree时生效

            isNeedMerge: true,//是否需要合并单元格
            mergeCols: [],
            mergeRule: function (row1, row2) { //合并规则, 默认相等时合并
                return BI.isEqual(row1, row2);
            },

            columnSize: [],
            headerRowSize: 25,
            footerRowSize: 25,
            rowSize: 25,

            regionColumnSize: false,

            header: [],
            footer: false,
            items: [],

            //交叉表头
            crossHeader: [],
            crossItems: []
        })
    },

    _getVDeep: function () {
        return this.options.crossHeader.length;//纵向深度
    },

    _getHDeep: function () {
        var o = this.options;
        return Math.max(o.mergeCols.length, o.freezeCols.length, BI.TableTree.maxDeep(o.items) - 1);
    },

    _init: function () {
        BI.DynamicSummaryTreeTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var deep = this._getHDeep();
        var vDeep = this._getVDeep();
        var header = BI.DynamicSummaryTreeTable.formatHeader(o.header, o.crossHeader, o.crossItems, deep, vDeep);
        var items = BI.DynamicSummaryTreeTable.formatItems(o.items, deep);
        items = BI.DynamicSummaryTreeTable.formatSummaryItems(items, o.crossItems, deep);
        this.table = BI.createWidget({
            type: "bi.table_view",
            element: this.element,
            logic: o.logic,

            isNeedResize: o.isNeedResize,
            isResizeAdapt: o.isResizeAdapt,

            isNeedFreeze: o.isNeedFreeze,
            freezeCols: o.freezeCols,
            isNeedMerge: o.isNeedMerge,
            mergeCols: o.mergeCols,
            mergeRule: o.mergeRule,

            columnSize: o.columnSize,
            headerRowSize: o.headerRowSize,
            footerRowSize: o.footerRowSize,
            rowSize: o.rowSize,

            regionColumnSize: o.regionColumnSize,

            header: header,
            footer: o.footer,
            items: items
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_INIT, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_INIT, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_SCROLL, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_SCROLL, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_BEFORE_REGION_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_BEFORE_REGION_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_REGION_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_REGION_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_BEFORE_COLUMN_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_BEFORE_COLUMN_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_COLUMN_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_COLUMN_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, arguments);
        });
    },

    resize: function () {
        this.table.resize();
    },

    setColumnSize: function (columnSize) {
        this.table.setColumnSize(columnSize);
    },

    getColumnSize: function () {
        return this.table.getColumnSize();
    },

    getCalculateColumnSize: function () {
        return this.table.getCalculateColumnSize();
    },

    setHeaderColumnSize: function (columnSize) {
        this.table.setHeaderColumnSize(columnSize);
    },

    setRegionColumnSize: function (columnSize) {
        this.table.setRegionColumnSize(columnSize);
    },

    getRegionColumnSize: function () {
        return this.table.getRegionColumnSize();
    },

    getCalculateRegionColumnSize: function () {
        return this.table.getCalculateRegionColumnSize();
    },

    getCalculateRegionRowSize: function () {
        return this.table.getCalculateRegionRowSize();
    },

    getClientRegionColumnSize: function () {
        return this.table.getClientRegionColumnSize();
    },

    getScrollRegionColumnSize: function () {
        return this.table.getScrollRegionColumnSize();
    },

    getScrollRegionRowSize: function () {
        return this.table.getScrollRegionRowSize();
    },

    hasVerticalScroll: function () {
        return this.table.hasVerticalScroll();
    },

    setVerticalScroll: function (scrollTop) {
        this.table.setVerticalScroll(scrollTop);
    },

    setLeftHorizontalScroll: function (scrollLeft) {
        this.table.setLeftHorizontalScroll(scrollLeft);
    },

    setRightHorizontalScroll: function (scrollLeft) {
        this.table.setRightHorizontalScroll(scrollLeft);
    },

    getVerticalScroll: function () {
        return this.table.getVerticalScroll();
    },

    getLeftHorizontalScroll: function () {
        return this.table.getLeftHorizontalScroll();
    },

    getRightHorizontalScroll: function () {
        return this.table.getRightHorizontalScroll();
    },

    getColumns: function () {
        return this.table.getColumns();
    },

    attr: function () {
        BI.DynamicSummaryTreeTable.superclass.attr.apply(this, arguments);
        this.table.attr.apply(this.table, arguments);
    },

    populate: function (items, header, crossItems, crossHeader) {
        var o = this.options;
        o.items = items || [];
        if (header) {
            o.header = header;
        }
        if (crossItems) {
            o.crossItems = crossItems;
        }
        if (crossHeader) {
            o.crossHeader = crossHeader;
        }
        var deep = this._getHDeep();
        var vDeep = this._getVDeep();
        header = BI.DynamicSummaryTreeTable.formatHeader(o.header, o.crossHeader, o.crossItems, deep, vDeep);
        items = BI.DynamicSummaryTreeTable.formatItems(o.items, deep);
        items = BI.DynamicSummaryTreeTable.formatSummaryItems(items, o.crossItems, deep);
        this.table.populate(items, header);
    },

    destroy: function () {
        this.table.destroy();
        BI.DynamicSummaryTreeTable.superclass.destroy.apply(this, arguments);
    }
});

BI.extend(BI.DynamicSummaryTreeTable, {
    // formatHeaderOnlyWithCrossHeader: function (header, crossHeader, crossItems, hDeep, vDeep) {
    //     var leaves = 0;
    //     var targetCount = 0;
    //     crossItems = BI.deepClone(crossItems);
    //
    //     function digest(node) {
    //         if (BI.isArray(node.children)) {
    //             BI.each(node.children, function (index, child) {
    //                 digest(child);
    //             });
    //             if (BI.isNotEmptyArray(node.values)) {
    //                 leaves++;
    //             }
    //             return;
    //         }
    //         if (targetCount === 0) {
    //             targetCount = node.values.length;
    //         }
    //         node.values = [""];
    //         leaves++;
    //     }
    //
    //     BI.each(crossItems, function (i, node) {
    //         digest(node);
    //     });
    //     if (targetCount > 0) {
    //         crossItems[0].children.splice(crossItems[0].children.length - targetCount);
    //         crossItems[0].values = BI.makeArray(targetCount, "");
    //     }
    //
    //     var items = BI.DynamicSummaryTreeTable.formatCrossItems(crossItems, vDeep);
    //     var result = [];
    //     BI.each(items, function (row, node) {
    //         var c = [crossHeader[row]];
    //         result.push(c.concat(node || []));
    //     });
    //     return result;
    // },

    formatHeader: function (header, crossHeader, crossItems, hDeep, vDeep) {
        var items = BI.DynamicSummaryTreeTable.formatCrossItems(crossItems, vDeep);
        var result = [];
        BI.each(items, function (row, node) {
            var c = [];
            for (var i = 0; i < hDeep; i++) {
                c.push(crossHeader[row]);
            }
            result.push(c.concat(node || []));
        });
        result.push(header);
        return result;
    },

    // formatRotatedItems: function (nodes, header, crossItems) {
    //     var result = [];
    //     var index = 0;
    //     var targetCount = 0;
    //     var values = nodes[0].values;
    //
    //     function getTargetCount(node) {
    //         if (BI.isArray(node.children)) {
    //             return BI.some(node.children, function (index, child) {
    //                 return getTargetCount(child);
    //             });
    //         }
    //         if (targetCount === 0) {
    //             targetCount = node.values.length;
    //             return true;
    //         }
    //     }
    //
    //     function put(i, index) {
    //         if (!result[i]) {
    //             result[i] = [header[i]];
    //         }
    //         result[i].push(values[index]);
    //     }
    //
    //     function digest(node) {
    //         if (BI.isArray(node.children)) {
    //             BI.each(node.children, function (index, child) {
    //                 digest(child);
    //             });
    //             if (BI.isNotEmptyArray(node.values)) {
    //                 if (node.values.length > 1) {
    //                     for (var i = 0; i < targetCount; i++) {
    //                         put(i, index++);
    //                     }
    //                 } else {
    //                     index++;
    //                 }
    //             }
    //             return;
    //         }
    //         BI.each(node.values, function (i) {
    //             put(i, index++);
    //         });
    //     }
    //
    //     BI.each(crossItems, function (i, node) {
    //         getTargetCount(node);
    //         digest(node);
    //     });
    //     return result;
    // },

    formatItems: function (nodes, deep, isCross) {
        var result = [];

        function track(store, node) {
            var next;
            if (BI.isArray(node.children)) {
                BI.each(node.children, function (index, child) {
                    var next;
                    if (store != -1) {
                        next = BI.clone(store);
                        next.push(node);
                    } else {
                        next = [];
                    }
                    track(next, child);
                });
                if (store != -1) {
                    next = BI.clone(store);
                    next.push(node);
                } else {
                    next = [];
                }
                if ((store == -1 || node.children.length > 1) && BI.isNotEmptyArray(node.values)) {
                    var cls = store === -1 ? " last" : "";
                    var id = BI.UUID();
                    for (var i = next.length; i < deep; i++) {
                        next.push({text: BI.i18nText("BI-Summary_Values"), tag: id, cls: "summary-cell" + cls});
                    }
                    if (!isCross) {
                        next = next.concat(node.values);
                    }
                    if (next.length > 0) {
                        if (!isCross) {
                            result.push(next);
                        } else {
                            for (var k = 0, l = node.values.length; k < l; k++) {
                                result.push(next);
                            }
                        }
                    }
                }
                return;
            }
            if (store != -1) {
                next = BI.clone(store);
                for (var i = next.length; i < deep; i++) {
                    next.push(node);
                }
            } else {
                next = [];
            }
            if (!isCross && BI.isArray(node.values)) {
                next = next.concat(node.values);
            }
            if (isCross && BI.isArray(node.values)) {
                for (var i = 0, len = node.values.length; i < len - 1; i++) {
                    if (next.length > 0) {
                        result.push(next);
                    }
                }
            }
            if (next.length > 0) {
                result.push(next);
            }
        }

        BI.each(nodes, function (i, node) {
            track(-1, node);
        });
        //填充空位
        BI.each(result, function (i, line) {
            var last = BI.last(line);
            for (var i = line.length; i < deep; i++) {
                line.push(last);
            }
        });
        return result;
    },

    formatCrossItems: function (nodes, deep) {
        var items = BI.DynamicSummaryTreeTable.formatItems(nodes, deep, true);
        return BI.unzip(items);
    },

    formatSummaryItems: function (items, crossItems, deep) {
        //求纵向需要去除的列
        var cols = [];
        var leaf = 0;

        function track(node) {
            if (BI.isArray(node.children)) {
                BI.each(node.children, function (index, child) {
                    track(child);
                });
                if (BI.isNotEmptyArray(node.values)) {
                    leaf++;
                    if (node.children.length === 1) {
                        cols.push(leaf - 1 + deep);
                    }
                }
                return;
            }
            if (node.values && node.values.length > 1) {
                leaf += node.values.length;
            } else {
                leaf++;
            }
        }

        BI.each(crossItems, function (i, node) {
            track(node);
        });

        if (cols.length > 0) {
            BI.each(items, function (i, node) {
                BI.removeAt(node, cols);
            })
        }
        return items;
    }
});

$.shortcut("bi.dynamic_summary_tree_table", BI.DynamicSummaryTreeTable);