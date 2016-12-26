/**
 * 汇总表（分组表、交叉表）
 * @class BI.SummaryTableReact
 * @extends BI.Pane
 */
BI.SummaryTableReact = BI.inherit(BI.Pane, {
    _defaultConfig: function () {
        return BI.extend(BI.SummaryTableReact.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-summary-table-react",
            overlap: false
        })
    },

    _init: function () {
        BI.SummaryTableReact.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.SummaryTableModel({
            wId: o.wId,
            status: o.status
        });
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
        })
    },

    _createTable: function () {
        var self = this, o = this.options;
        this.vPage = 1;
        this.hPage = 1;

        var columnSizeChange = function (data) {
            var columnSize = BI.clone(data.columnSize);
            var regionColumnSize = BI.clone(data.regionColumnSize);
            self.model.setStoredRegionColumnSize(regionColumnSize[0]);
            self.fireEvent(BI.DetailTableReact.EVENT_CHANGE, {settings: BI.extend(BI.Utils.getWidgetSettingsByID(o.wId), {column_size: columnSize})});
        };
        var pageChange = function (vPage, hPage) {
            var pageOperator = BICst.TABLE_PAGE_OPERATOR.COLUMN_NEXT;
            if (vPage > self.vPage) {
                pageOperator = BICst.TABLE_PAGE_OPERATOR.ROW_NEXT;
            }
            if (vPage < self.vPage) {
                pageOperator = BICst.TABLE_PAGE_OPERATOR.ROW_PRE;
            }
            if (hPage > self.hPage) {
                pageOperator = BICst.TABLE_PAGE_OPERATOR.COLUMN_NEXT;
            }
            if (hPage < self.hPage) {
                pageOperator = BICst.TABLE_PAGE_OPERATOR.COLUMN_PRE;
            }
            self.hPage = hPage;
            self.vPage = vPage;
            self.model.setPageOperator(pageOperator);
            self._onPageChange(function (op) {
                self._refreshTable(op);
            })
        };

        this.table = BI.createWidget({
            type: "bi.summary_table_component",

            onColumnResizeEnd: columnSizeChange,
            onRegionColumnResizeEnd: columnSizeChange,

            vCurr: 1,
            hCurr: 1,
            hasVNext: function (vCurr) {
                return self.model.getPage()[1] === 1;
            },

            hasHNext: function (hCurr) {
                return self.model.getPage()[3] === 1;
            },

            onVPrev: function (vCurr) {
                pageChange(vCurr, self.hPage);
            },

            onVNext: function (vCurr) {
                pageChange(vCurr, self.hPage);
            },

            onHPrev: function (vCurr) {
                pageChange(self.vPage, vCurr);
            },

            onHNext: function (vCurr) {
                pageChange(self.vPage, vCurr);
            }
        });

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

    /**
     * 无维度或指标发生变化时（如：展开节点）
     * @private
     */
    _populateNoDimsChange: function () {
        var self = this, wId = this.options.wId;
        this.loading();
        BI.Utils.getWidgetDataByID(wId, {
            success: function (jsonData) {
                if (BI.isNotNull(jsonData.error)) {
                    self.errorPane.setErrorInfo(jsonData.error);
                    self.errorPane.setVisible(true);
                    return;
                }
                if (BI.isNull(jsonData.data) || BI.isNull(jsonData.page)) {
                    self.errorPane.setErrorInfo("invalid json data!");
                    self.errorPane.setVisible(true);
                    return;
                }
                self.model.setDataAndPage(jsonData);
                var widgetType = BI.Utils.getWidgetTypeByID(wId);
                try {
                    switch (widgetType) {
                        case BICst.WIDGET.TABLE:
                            self._prepareData4GroupTable();
                            break;
                        case BICst.WIDGET.CROSS_TABLE:
                            //如果没有列表头，还是以分组表展示——后台传这样的数据
                            if (BI.isNotNull(self.model.getData().t)) {
                                self._prepareData4CrossTable();
                            } else {
                                self._prepareData4GroupTable();
                            }
                            break;
                        case BICst.WIDGET.COMPLEX_TABLE:
                            self._populateComplexTable();
                            break;
                    }
                    self._refreshTable();
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

    _onPageChange: function (callback) {
        var self = this, wId = this.options.wId;
        this.loading();
        BI.Utils.getWidgetDataByID(wId, {
            success: function (jsonData) {
                if (BI.isNotNull(jsonData.error)) {
                    self.errorPane.setErrorInfo(jsonData.error);
                    self.errorPane.setVisible(true);
                    return;
                }
                if (BI.isNull(jsonData.data) || BI.isNull(jsonData.page)) {
                    self.errorPane.setErrorInfo("invalid json data!");
                    self.errorPane.setVisible(true);
                    return;
                }
                self.model.setDataAndPage(jsonData);
                var widgetType = BI.Utils.getWidgetTypeByID(wId);
                try {
                    switch (widgetType) {
                        case BICst.WIDGET.TABLE:
                            self.model.createGroupTableAttrs(BI.bind(self._onClickHeaderOperator, self), BI.bind(self._populateNoDimsChange, self), BI.bind(self._onClickBodyCellOperator, self));
                            break;
                        case BICst.WIDGET.CROSS_TABLE:
                            if (BI.isNotNull(self.model.getData().t)) {
                                self.model.createCrossTableAttrs(BI.bind(self._onClickHeaderOperator, self), BI.bind(self._populateNoDimsChange, self), BI.bind(self._onClickBodyCellOperator, self));
                            } else {
                                self.model.createGroupTableAttrs(BI.bind(self._onClickHeaderOperator, self), BI.bind(self._populateNoDimsChange, self), BI.bind(self._onClickBodyCellOperator, self));
                            }
                            break;
                    }
                    callback();
                } catch (e) {
                    self.errorPane.setErrorInfo("error happens during populate for table: " + e);
                    self.errorPane.setVisible(true);
                }
                self.fireEvent(BI.SummaryTableReact.EVENT_CHANGE, {
                    _page_: {
                        h: self.hPage,
                        v: self.vPage
                    }
                });
            },
            done: function () {
                self.loaded();
            }
        }, this.model.getExtraInfo());
    },

    /**
     * 分组表
     * @private
     */
    _prepareData4GroupTable: function () {
        //创建表格的各种属性——回调各种点击事件
        this.model.createGroupTableAttrs(BI.bind(this._onClickHeaderOperator, this), BI.bind(this._populateNoDimsChange, this), BI.bind(this._onClickBodyCellOperator, this));
    },

    /**
     * 交叉表
     * @private
     */
    _prepareData4CrossTable: function () {
        this.model.createCrossTableAttrs(BI.bind(this._onClickHeaderOperator, this), BI.bind(this._populateNoDimsChange, this), BI.bind(this._onClickBodyCellOperator, this));
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

    /**
     * 表上的操作（上钻、下钻）
     * @private
     */
    _onClickBodyCellOperator: function (clicked) {
        this.fireEvent(BI.SummaryTableReact.EVENT_CHANGE, {clicked: clicked});
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
                self.fireEvent(BI.SummaryTableReact.EVENT_CHANGE, {dimensions: dimensions});
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

                self.fireEvent(BI.SummaryTableReact.EVENT_CHANGE, {filter_value: targetFilter});
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
        this.fireEvent(BI.SummaryTableReact.EVENT_CHANGE, ob);
    },

    _populateComplexTable: function () {

    },

    _formatItems: function (items) {
        function track(node) {
            if (node.children) {
                node.childs = node.children;
                BI.each(node.children, function (i, child) {
                    track(child);
                });
                delete node.children;
            }
        }

        BI.each(items, function (i, node) {
            track(node);
        });
        return items;
    },

    _refreshTable: function (op) {
        this.errorPane.setVisible(false);
        this.table.populate(BI.extend({
            isNeedFreeze: true,
            freezeCols: this.model.getFreezeCols(),
            mergeCols: this.model.getMergeCols() || [],
            columnSize: this.model.getColumnSize(),
            header: this.model.getHeader(),
            items: this._formatItems(this.model.getItems()),
            crossHeader: this.model.getCrossHeader(),
            crossItems: this._formatItems(this.model.getCrossItems()),
            showSequence: this.model.isShowNumber(),
            styleType: this.model.getTableStyle(),
            color: this.model.getThemeColor(),
            tableStyle: this.model.getTableForm(),
            vCurr: this.vPage,
            hCurr: this.hPage
        }, op));
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
                if (BI.isNotNull(jsonData.error)) {
                    self.errorPane.setErrorInfo(jsonData.error);
                    self.errorPane.setVisible(true);
                    return;
                }
                if (BI.isNull(jsonData.data) || BI.isNull(jsonData.page)) {
                    self.errorPane.setErrorInfo("invalid json data!");
                    self.errorPane.setVisible(true);
                    return;
                }
                self.model.setDataAndPage(jsonData);
                var widgetType = BI.Utils.getWidgetTypeByID(widgetId);
                // try {
                switch (widgetType) {
                    case BICst.WIDGET.TABLE:
                        self._prepareData4GroupTable();
                        break;
                    case BICst.WIDGET.CROSS_TABLE:
                        //如果没有列表头，还是以分组表展示——后台传这样的数据
                        if (BI.isNotNull(self.model.getData().t)) {
                            self._prepareData4CrossTable();
                        } else {
                            self._prepareData4GroupTable();
                        }
                        break;
                    case BICst.WIDGET.COMPLEX_TABLE:
                        self._populateComplexTable();
                        break;
                }
                self._refreshTable({
                    vCurr: 1
                });
                // } catch (e) {
                //     self.errorPane.setErrorInfo("error happens during populate table: " + e);
                //     console.error(e);
                //     self.errorPane.setVisible(true);
                // }
            },
            done: function () {
                self.loaded();
            }
        }, this.model.getExtraInfo());
    },

    resize: function () {
        this.table.resize();
    },

    magnify: function () {

    }
});
BI.SummaryTableReact.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.summary_table_react", BI.SummaryTableReact);
