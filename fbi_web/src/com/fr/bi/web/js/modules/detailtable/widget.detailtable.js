/**
 * @class BI.DetailTable
 * @extend BI.Widget
 * 明细表的表格
 */
BI.DetailTable = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table"
        })
    },

    _init: function () {
        BI.DetailTable.superclass._init.apply(this, arguments);
        var self = this;
        this.pager = BI.createWidget({
            type: "bi.all_pager"
        });
        this.pager.on(BI.AllPagger.EVENT_CHANGE, function () {
            self.populate(self.pager.getCurrentPage())
        });

        this.tabler = BI.createWidget({
            type: "bi.tabler",
            element: this.element,
            pager: this.pager,
            layout: [{
                type: "bi.right"
            }]
        });


    },

    populate: function (page) {
        var self = this;
        var widgetId = this.options.wId;
        this.data = [];
        var dimensions = BI.Utils.getAllDimensionIDs(widgetId);
        if (BI.isEmpty(dimensions)) {
            this.tabler.populate({
                items: [],
                header: [],
                columnSize: []
            });
            self.pager.setAllPages(0);
            self.pager.setValue(0);
            return;

        }
        this.pageOperator = page || BICst.TABLE_PAGE_OPERATOR.REFRESH;
        function formatTree(tree) {
            var result = [];
            BI.each(tree, function (i, t) {
                var item = {};
                item.name = t.node.name;
                if (BI.isNotNull(t.children)) {
                    item.children = formatTree(t.children);
                }
                result.push(item);
            });
            return result;
        }

        var ob = {};
        ob.page = this.pageOperator;
        BI.Utils.getWidgetDataByID(widgetId, function (jsonData) {
            var json = jsonData.data, row = jsonData.row, page = jsonData.page, size = jsonData.size;
            if (BI.isNull(json) || BI.isNull(row)) {
                return;
            }
            var header = [], view = BI.Utils.getWidgetViewByID(widgetId);
            BI.each(view[BICst.REGION.DIMENSION1], function (i, dId) {
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
            var items = [], values = json.value;
            BI.each(values, function (i, row) {
                var rowItems = [];
                BI.each(row, function (j, v) {
                    var item = {};
                    item.text = v;
                    rowItems.push(item);
                });
                items.push({
                    values: rowItems
                });
            });
            var columnSize = [];
            BI.each(header, function (i, item) {
                columnSize.push("");
            });
            self.pager.setAllPages(Math.ceil(row / size));
            self.pager.setValue(page);
            self.tabler.populate({
                items: items,
                header: header,
                columnSize: columnSize
            })
        }, ob);
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
    }
});
BI.DetailTable.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table", BI.DetailTable);