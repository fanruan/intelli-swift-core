/**
 * Created by Young's on 2017/1/17.
 */
BI.GroupTable = BI.inherit(BI.Pane, {
    _defaultConfig: function () {
        return BI.extend(BI.GroupTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-group-table"
        });
    },

    _init: function () {
        BI.GroupTable.superclass._init.apply(this, arguments);
        var self = this;
        this._initModel();
        this._createTable();
        this.errorPane = BI.createWidget({
            type: "bi.table_chart_error_pane",
            invisible: true
        });
        this.errorPane.element.css("z-index", 1);
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.errorPane,
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }]
        });
        BI.ResizeDetector.addResizeListener(this, function () {
            self.resize();
        });
    },

    _initModel: function () {
        var o = this.options;
        this.model = new BI.GroupTableModel({
            wId: o.wId,
            status: o.status
        });
    },

    _createTable: function () {
        var self = this, o = this.options;
        this.table && this.table.destroy();
        this.vPage = 1;
        this.hPage = 1;
        var tableStyle = this.model.getTableForm();
        var publicAttr = {
            type: "bi.page_table",
            isNeedFreeze: null,
            summaryCellStyleGetter: function (isLast) {
                return isLast ? BI.SummaryTableHelper.getLastSummaryStyles(self.model.getThemeColor(), self.model.getTableStyle()) :
                    BI.SummaryTableHelper.getSummaryStyles(self.model.getThemeColor(), self.model.getTableStyle())
            },
            sequenceCellStyleGetter: function (index) {
                return BI.SummaryTableHelper.getBodyStyles(self.model.getThemeColor(), self.model.getTableStyle(), index);
            },
            headerCellStyleGetter: function () {
                return BI.SummaryTableHelper.getHeaderStyles(self.model.getThemeColor(), self.model.getTableStyle());
            },
            mergeRule: function (col1, col2) {
                if (col1.tag && col2.tag) {
                    return col1.tag === col2.tag;
                }
                return col1 === col2;
            },
            itemsCreator: function (op, populate) {
                var vPage = op.vpage, hPage = op.hpage;
                var pageOperator = self.model.getPageOperatorByPages(vPage, hPage, self.vPage, self.hPage);
                self.hPage = hPage;
                self.vPage = vPage;
                self.model.setPageOperator(pageOperator);
                self._onPageChange(function (items, header, crossItems, crossHeader) {
                    populate(items, header, crossItems, crossHeader);
                })
            },
            pager: {
                pages: false,
                curr: 1,
                firstPage: 1,
                horizontal: {
                    pages: false, //总页数
                    curr: 1, //初始化当前页， pages为数字时可用
                    hasPrev: function () {
                        return self.model.getPage()[2] === 1;
                    },
                    hasNext: function () {
                        return self.model.getPage()[3] === 1;
                    },
                    firstPage: 1,
                    lastPage: BI.emptyFn
                },
                vertical: {
                    pages: false, //总页数
                    curr: 1, //初始化当前页， pages为数字时可用
                    hasPrev: function () {
                        return self.model.getPage()[0] === 1;
                    },
                    hasNext: function () {
                        return self.model.getPage()[1] === 1;
                    },
                    firstPage: 1,
                    lastPage: BI.emptyFn
                }
            },
            isNeedMerge: true
        };
        switch (tableStyle) {
            case BICst.TABLE_FORM.OPEN_COL:
                this.tableForm = BICst.TABLE_FORM.OPEN_COL;
                this.table = BI.createWidget(publicAttr, {
                    el: {
                        type: "bi.sequence_table",
                        el: {
                            type: "bi.dynamic_summary_layer_tree_table",
                            el: {
                                type: "bi.adaptive_table",
                                el: {
                                    type: "bi.resizable_table",
                                    el: {
                                        type: "bi.collection_table"
                                    }
                                }
                            },
                        },
                        sequence: {
                            type: "bi.sequence_table_tree_number"
                        }
                    }
                });
                break;
            default :
                this.tableForm = BICst.TABLE_FORM.OPEN_ROW;
                this.table = BI.createWidget(publicAttr, {
                    el: {
                        type: "bi.sequence_table",
                        el: {
                            type: "bi.dynamic_summary_tree_table",
                            el: {
                                type: "bi.adaptive_table",
                                el: {
                                    type: "bi.resizable_table",
                                    el: {
                                        type: "bi.collection_table"
                                    }
                                }
                            },
                        },
                        sequence: {
                            type: "bi.sequence_table_dynamic_number"
                        }
                    }
                });
                break;
        }
        this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
            var columnSize = this.getRegionColumnSize();
            self.model.setStoredRegionColumnSize(columnSize[0]);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            var columnSize = BI.clone(this.getColumnSize());
            self.fireEvent(BI.GroupTable.EVENT_CHANGE, {settings: BI.extend(BI.Utils.getWidgetSettingsByID(self.model.getWidgetId()), {column_size: columnSize})});
        });
        if (this.model.getPageOperator() === BICst.TABLE_PAGE_OPERATOR.ROW_NEXT || this.model.getPageOperator() === BICst.TABLE_PAGE_OPERATOR.ROW_PRE) {
            this.table.setVPage(this.model.getPage()[4]);
        }

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.table,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        })
    },

    _onClickHeaderCellFilter: function (dId) {
        var self = this;

        function formatTargetFilter(filter, tId) {
            if (filter.filter_type === BICst.FILTER_TYPE.AND ||
                filter.filter_type === BICst.FILTER_TYPE.OR) {
                BI.each(filter.filter_value, function (i, filter) {
                    formatTargetFilter(filter, tId);
                });
                return;
            }
            filter.target_id = tId;
        }

        BI.Popovers.remove(dId);
        if (BI.Utils.isDimensionByDimensionID(dId)) {
            var popup = BI.createWidget({
                type: "bi.dimension_filter_popup",
                dId: dId
            });
            popup.on(BI.DimensionFilterPopup.EVENT_CHANGE, function (v) {
                var dimensions = BI.Utils.getWidgetDimensionsByID(self.options.wId);
                dimensions[dId].filter_value = v;
                self.fireEvent(BI.GroupTable.EVENT_CHANGE, {dimensions: dimensions});
            });
        } else {
            var popup = BI.createWidget({
                type: "bi.target_summary_filter_popup",
                dId: dId
            });
            popup.on(BI.TargetSummaryFilterPopup.EVENT_CHANGE, function (v) {
                var targetFilter = BI.Utils.getWidgetFilterValueByID(self.options.wId);
                if (BI.isNotNull(v)) {
                    formatTargetFilter(v, dId);
                    targetFilter[dId] = v;
                } else {
                    delete targetFilter[dId];
                }

                self.fireEvent(BI.GroupTable.EVENT_CHANGE, {filter_value: targetFilter});
            });
        }
        BI.Popovers.create(dId, popup).open(dId);
        popup.populate();
    },

    _onClickHeaderSort: function (dId, v) {
        var ob = {};
        if (BI.Utils.isDimensionByDimensionID(dId)) {
            var dimensions = BI.Utils.getWidgetDimensionsByID(this.options.wId);
            dimensions[dId].sort = {sort_target: dId, type: v};
            ob.dimensions = dimensions;
        } else {
            ob.sort = {sort_target: dId, type: v};
        }
        this.fireEvent(BI.GroupTable.EVENT_CHANGE, ob);
    },

    /**
     * 表头上的一系列操作（排序、过滤）
     */
    _onClickHeaderOperator: function (v, dId) {
        switch (v) {
            case BICst.SORT.ASC:
            case BICst.SORT.DESC:
            case BICst.SORT.NONE:
                this._onClickHeaderSort(dId, v);
                break;
            default :
                this._onClickHeaderCellFilter(dId);
                break;
        }
    },

    _isJSONDataValid: function (json) {
        if (BI.isNotNull(json.error)) {
            this.errorPane.setErrorInfo(json.error);
            this.errorPane.setVisible(true);
            return false;
        }
        if (BI.isNull(json.data) || BI.isNull(json.page)) {
            this.errorPane.setErrorInfo("invalid json data!");
            this.errorPane.setVisible(true);
            return false;
        }
        return true;
    },

    _onPageChange: function (callback) {
        var self = this, wId = this.options.wId;
        this.loading();
        BI.Utils.getWidgetDataByID(wId, {
            success: function (jsonData) {
                if (!self._isJSONDataValid(jsonData)) {
                    return;
                }
                self.model.setDataAndPage(jsonData);
                try {
                    self.model.createTableAttrs(BI.bind(self._onClickHeaderOperator, self), BI.bind(self._populateNoDimsChange, self));
                    callback(self.model.getItems(), self.model.getHeader());
                } catch (e) {
                    self.errorPane.setErrorInfo("error happens during populate for table: " + e);
                    self.errorPane.setVisible(true);
                }
                self.fireEvent(BI.GroupTable.EVENT_CHANGE, {
                    _page_: {
                        h: self.table.getHPage(),
                        v: self.table.getVPage()
                    }
                });
            },
            done: function () {
                self.loaded();
            }
        }, this.model.getExtraInfo());
    },

    /**
     * 无维度或指标发生变化时（如：展开节点）
     */
    _populateNoDimsChange: function () {
        var self = this, wId = this.options.wId;
        this.loading();
        BI.Utils.getWidgetDataByID(wId, {
            success: function (jsonData) {
                if (!self._isJSONDataValid(jsonData)) {
                    return;
                }
                self.model.setDataAndPage(jsonData);
                try {
                    self.model.createTableAttrs(BI.bind(self._onClickHeaderOperator, self), BI.bind(self._populateNoDimsChange, self));
                    self._populateTable();
                } catch (e) {
                    self.errorPane.setErrorInfo("error happens during populate table: " + e);
                    self.errorPane.setVisible(true);
                }
            },
            done: function () {
                self.loaded();
            }
        }, this.model.getExtraInfo());
    },

    _refreshAttrs: function () {
        this.table.setWidth(this.element.width());
        this.table.setHeight(this.element.height());
        this.table.attr("isNeedFreeze", true);
        this.table.attr("showSequence", this.model.isShowNumber());
        this.table.attr("freezeCols", this.model.getFreezeCols());
        this.table.attr("mergeCols", this.model.getMergeCols());
        this.table.attr("columnSize", this.model.getColumnSize());
        this.table.attr("minColumnSize", this.model.getMinColumnSize());
        this.table.attr("regionColumnSize", this.model.getStoredRegionColumnSize());
        this.table.attr("headerRowSize", this.model.getHeaderRowSize());
        this.table.attr("rowSize", this.model.getRowSize());
    },

    _populateTable: function () {
        this.errorPane.setVisible(false);
        this._refreshAttrs();
        this.table.populate(this.model.getItems(), this.model.getHeader());
    },

    populate: function () {
        var self = this;
        var widgetId = this.options.wId;
        this.vPage = 1;
        this.hPage = 1;
        this.model.setPageOperator(BICst.TABLE_PAGE_OPERATOR.REFRESH);
        this.model.resetETree();
        this.loading();
        BI.Utils.getWidgetDataByID(widgetId, {
            success: function (jsonData) {
                if (!self._isJSONDataValid(jsonData)) {
                    return;
                }
                self.model.setDataAndPage(jsonData);
                self.model.createTableAttrs(BI.bind(self._onClickHeaderOperator, self), BI.bind(self._populateNoDimsChange, self));
                if (self.model.getTableForm() !== self.tableForm) {
                    self._createTable();
                }
                //回到首页
                self.table.setVPage(1);
                self.table.setHPage(1);
                self._populateTable();
            },
            done: function () {
                self.loaded();
            }
        }, this.model.getExtraInfo());
    },

    resize: function () {
        var self = this;
        BI.nextTick(function () {
            self.table.setWidth(self.element.width());
            self.table.setHeight(self.element.height());
            self.table.populate();
        });
    },

    magnify: function () {

    }
});
BI.GroupTable.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.group_table", BI.GroupTable);