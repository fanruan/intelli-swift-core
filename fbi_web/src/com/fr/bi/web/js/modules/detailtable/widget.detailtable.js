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
            color: BICst.DEFAULT_CHART_SETTING.theme_color,
            style: BI.StyleTable.STYLE1,
            el: {
                el: {
                    type: "bi.page_table",
                    el: {
                        el: {
                            el: {
                                type: "bi.table_tree"
                            }
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
            }
        });

        this.table.on(BI.StyleTable.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            self.fireEvent(BI.DetailTable.EVENT_CHANGE, {settings: BI.extend(BI.Utils.getWidgetSettingsByID(o.wId), {column_size: self.table.getColumnSize()})});
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.table,
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
            var items = [{
                children: self._createTableItems(json.value)
            }];

            self.pager.setAllPages(Math.ceil(row / size));
            self.pager.setValue(vPage);
            callback(items, header, [], [])
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
                if (!sortSequence.contains(dId)) {
                    sortSequence.push(dId);
                }
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
            var filterValue = BI.Utils.getWidgetFilterValueByID(self.options.wId) || {};
            filterValue[dId] = v;
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
        var rowItem = {};
        var dimensionIds = BI.Utils.getWidgetViewByID(this.options.wId)[BICst.REGION.DIMENSION1];
        BI.each(rowValues, function (i, rowValue) {
            if (i === 0) {
                rowItem.text = rowValue;
                rowItem.type = "bi.detail_table_cell";
                rowItem.dId = dimensionIds[i];
                rowItem.values = [];
            } else {
                rowItem.values.push({
                    type: "bi.detail_table_cell",
                    dId: dId,
                    text: rowValue
                })
            }
        });
        return rowItem;
    },

    populate: function () {
        var self = this;
        this._onPageChange(BICst.TABLE_PAGE_OPERATOR.REFRESH, function (items, header) {
            self.table.attr("columnSize", self._getColumnSize(header));
            self.table.populate(items, header, [], []);
        });
    }

});
BI.DetailTable.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table", BI.DetailTable);