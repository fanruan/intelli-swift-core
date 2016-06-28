/**
 * @class BI.DetailTable
 * @extends BI.Pane
 * 明细表的表格
 */
BI.DetailTable = BI.inherit(BI.Pane, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table",
            wId: ""
        })
    },

    _init: function () {
        BI.DetailTable.superclass._init.apply(this, arguments);
        var self = this;
        var o = this.options;

        this.pager = BI.createWidget({
            type: "bi.all_pager",
            cls: "page-table-pager",
            height: 18
        });

        this.table = BI.createWidget({
            type: "bi.style_table",
            element: this.element,
            el: {
                type: "bi.page_table",
                el: {
                    el: {
                        el: {
                            el: {
                                type: "bi.table_view"
                            }
                        }
                    },
                    sequence: {
                        type: "bi.sequence_table_list_number",
                        pageSize: 100
                    }
                },
                itemsCreator: function (op, populate) {
                    var vPage = op.vpage;
                    self._onPageChange(vPage, function (items, header, crossItems, crossHeader) {
                        populate.apply(self.table, arguments);
                    })
                },
                pager: this.pager
            }
        });

        this.table.on(BI.StyleTable.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            self.fireEvent(BI.DetailTable.EVENT_CHANGE, {settings: BI.extend(BI.Utils.getWidgetSettingsByID(o.wId), {column_size: self.table.getColumnSize()})});
        });
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

    _onPageChange: function (vPage, callback) {
        var self = this;
        var widgetId = this.options.wId;
        this.loading();
        this.errorPane.setVisible(false);
        this.data = [];
        var hyperLinkExpressions = [];
        var isUseHyperLinkDimension = [];
        var dimensions = BI.Utils.getAllDimensionIDs(widgetId);
        if (BI.isEmpty(dimensions)) {
            this.loaded();
            callback([], [], [], []);
            self.pager.setAllPages(1);
            self.pager.setValue(1);
            return;

        }
        this.pageOperator = vPage || BICst.TABLE_PAGE_OPERATOR.REFRESH;

        var ob = {};
        ob.page = this.pageOperator;
        BI.Utils.getWidgetDataByID(widgetId, function (jsonData) {
            self.loaded();
            if (BI.isNotNull(jsonData.error)) {
                self.errorPane.setErrorInfo(jsonData.error);
                self.errorPane.setVisible(true);
                return;
            }
            try {
                var json = jsonData.data, row = jsonData.row, size = jsonData.size;
                if (BI.isNull(json) || BI.isNull(row)) {
                    callback([], [], [], []);
                    return;
                }
                var header = [], view = BI.Utils.getWidgetViewByID(widgetId);
                BI.each(view[BICst.REGION.DIMENSION1], function (i, dId) {
                    var hyperlink = BI.Utils.getDimensionHyperLinkByID(dId) || {};
                    isUseHyperLinkDimension.push(hyperlink.used || false);
                    hyperLinkExpressions.push(hyperlink.expression || "");
                    BI.isNotNull(dId) &&
                    BI.Utils.isDimensionUsable(dId) === true &&
                    header.push({
                        type: "bi.detail_table_header",
                        dId: dId,
                        text: BI.Utils.getDimensionNameByID(dId),
                        sortFilterChange: function (v) {
                            self.pageOperator = BICst.TABLE_PAGE_OPERATOR.REFRESH;
                            self._headerOperatorChange(v, dId);
                        }
                    });
                });
                var items = self._createTableItems(json.value);

                self.pager.setAllPages(Math.ceil(row / size));
                self.pager.setValue(vPage);
                callback(items, [header]);
            } catch (e) {
                self.errorPane.setErrorInfo("error happens during populate chart: " + e);
                self.errorPane.setVisible(true);
                return;
            }
            //显示序号
            if (BI.Utils.getWSShowNumberByID(widgetId)) {
                self.table.showSequence();
            } else {
                self.table.hideSequence();
            }

            //设置样式和颜色
            self.table.setStyleAndColor(BI.Utils.getWSTableStyleByID(widgetId), BI.Utils.getWSThemeColorByID(widgetId));
        }, ob);
    },


    _getColumnSize: function (header) {
        var columnSize = BI.Utils.getWidgetSettingsByID(this.options.wId).column_size;
        if (BI.isNull(columnSize)) {
            columnSize = BI.makeArray(header.length, "");
        }
        return columnSize;
    },


    _headerOperatorChange: function (v, dId) {
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

    _onClickHeaderSort: function (dId, v) {
        var ob = {};
        var dimensions = BI.Utils.getWidgetDimensionsByID(this.options.wId);
        dimensions[dId].sort = {sort_target: dId, type: v};
        ob.dimensions = dimensions;
        var sortSequence = BI.Utils.getWidgetSortSequenceByID(this.options.wId);
        switch (v) {
            case BICst.SORT.ASC:
            case BICst.SORT.DESC:
                BI.remove(sortSequence, dId);
                sortSequence.push(dId);
                break;
            case BICst.SORT.NONE:
                BI.remove(sortSequence, dId);
        }
        ob.sort_sequence = sortSequence;
        this.fireEvent(BI.DetailTable.EVENT_CHANGE, ob);
    },

    _onClickHeaderCellFilter: function (dId) {
        var self = this;
        BI.Popovers.remove(dId);
        var popup = BI.createWidget({
            type: "bi.detail_table_filter_popup",
            dId: dId
        });
        popup.on(BI.DetailTableFilterPopup.EVENT_CHANGE, function (v) {
            var filterValue = BI.Utils.getWidgetFilterValueByID(self.options.wId);
            if (BI.isNotNull(v)) {
                filterValue[dId] = v;
            } else {
                delete filterValue[dId];
            }
            self.fireEvent(BI.DetailTable.EVENT_CHANGE, {filter_value: filterValue});
        });
        BI.Popovers.create(dId, popup).open(dId);
        popup.populate();
    },

    _createTableItems: function (values) {
        var tableItems = [], self = this;
        BI.each(values, function (i, row) {
            tableItems.push(self._createRowItem(row));
        });
        return tableItems
    },

    _createRowItem: function (rowValues, dId) {
        var dimensionIds = BI.Utils.getWidgetViewByID(this.options.wId)[BICst.REGION.DIMENSION1];
        var rowItems = [];
        BI.each(rowValues, function (i, rowValue) {
            if (BI.Utils.isDimensionUsable(dimensionIds[i])) {
                rowItems.push({
                    text: BI.isNull(rowValue) ? "" : rowValue,
                    type: "bi.detail_table_cell",
                    dId: dimensionIds[i]
                })
            }
        });
        return rowItems;
    },
    _getFreezeCols: function () {
        var wId = this.options.wId;
        return BI.Utils.getWSFreezeFirstColumnById(wId) ? [0] : [];

    },

    _isNeedFreeze: function () {
        var wId = this.options.wId;
        return BI.Utils.getWSFreezeFirstColumnById(wId);
    },


    populate: function () {
        var self = this;
        this._onPageChange(BICst.TABLE_PAGE_OPERATOR.REFRESH, function (items, header) {
            self.table.attr("columnSize", self._getColumnSize(header));
            self.table.attr("isNeedFreeze", self._isNeedFreeze());
            self.table.attr("freezeCols", self._getFreezeCols());
            self.table.populate(items, header);
        });
    },

    resize: function () {
        this.table.resize();
    }

});
BI.DetailTable.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table", BI.DetailTable);