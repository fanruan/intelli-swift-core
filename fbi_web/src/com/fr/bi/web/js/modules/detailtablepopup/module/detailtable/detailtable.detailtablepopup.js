/**
 * 指标弹出明细表表格
 *
 * Created by GUY on 2016/5/18.
 * @class BI.DetailTablePopupDetailTable
 * @extends BI.Widget
 */
BI.DetailTablePopupDetailTable = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopupDetailTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-popup-detail-table"
        })
    },

    _init: function () {
        BI.DetailTablePopupDetailTable.superclass._init.apply(this, arguments);
        var self = this;

        this.model = new BI.DetailTablePopupDetailTableModel({});
        this.table = BI.createWidget({
            type: "bi.table_view",
            element: this.element
        });
    },

    populate: function () {
        var self = this;
        this.model.getData(function (jsonData) {
            var json = jsonData.data, row = jsonData.row, size = jsonData.size;
            if (BI.isNull(json) || BI.isNull(row)) {
                return;
            }
            var header = [], view = self.model.getView();
            BI.each(view[BICst.REGION.DIMENSION1], function (i, dId) {
                self.model.isDimensionUsable(dId) === true &&
                header.push({
                    type: "bi.detail_table_popup_detail_table_header",
                    dId: dId,
                    text: self.model.getDimensionNameByID(dId),
                    sortFilterChange: function (v) {

                    }
                });
            });
            var items = [], values = json.value;
            BI.each(values, function (i, row) {
                var rowItems = [];
                BI.each(row, function (j, v) {
                    var item = {
                        type: "bi.detail_table_popup_detail_table_cell",
                        text: v
                    };
                    rowItems.push(item);
                });
                items.push(rowItems);
            });
            var columnSize = BI.makeArray(header.length, "");
            self.table.setColumnSize(columnSize);
            self.table.populate(items, [header]);
        });
    },

    _headerOperatorChange: function (v, dId) {
        switch (v) {
            case BICst.SORT.ASC:
            case BICst.SORT.DESC:
            case BICst.SORT.NONE:
                this._onClickHeaderSort(dId, v);
                break;
            default :
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
        this.fireEvent(BI.DetailTablePopupDetailTable.EVENT_CHANGE, ob);
    }
});
BI.DetailTablePopupDetailTable.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table_popup_detail_table", BI.DetailTablePopupDetailTable);