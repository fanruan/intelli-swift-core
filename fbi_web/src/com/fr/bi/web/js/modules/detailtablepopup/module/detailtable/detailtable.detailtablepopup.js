/**
 * 指标弹出明细表表格
 *
 * Created by GUY on 2016/5/18.
 * @class BI.DetailTablePopupDetailTable
 * @extends BI.Pane
 */
BI.DetailTablePopupDetailTable = BI.inherit(BI.Pane, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopupDetailTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-popup-detail-table"
        })
    },

    _init: function () {
        BI.DetailTablePopupDetailTable.superclass._init.apply(this, arguments);
        var self = this;

        this.model = new BI.DetailTablePopupDetailTableModel({});

        this.pager = BI.createWidget({
            type: "bi.all_pager",
            cls: "page-table-pager",
            height: 18
        });

        this.table =BI.createWidget({
            type: "bi.style1_table",
            color: "#0088cc",
            el: {
                el: {
                    type: "bi.page_table",
                    itemsCreator: function (op, populate) {
                        var vPage = op.vpage;
                        self._onPageChange(vPage, function (items, header) {
                            populate.apply(self.table, arguments);
                        })
                    },
                    pager: this.pager
                }
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

    _onPageChange: function (vPage, callback) {
        var self = this;
        this.loading();
        var dimensions = this.model.getAllDimensionIDs();
        if (BI.isEmpty(dimensions)) {
            this.loaded();
            callback([], [], [], []);
            self.pager.setAllPages(0);
            self.pager.setValue(0);
            return;
        }
        this.model.getData(vPage, function (jsonData) {
            self.loaded();
            var json = jsonData.data, row = jsonData.row, size = jsonData.size;
            if (BI.isNull(json) || BI.isNull(row)) {
                callback([], [], [], []);
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
            self.pager.setAllPages(Math.ceil(row / size));
            self.pager.setValue(vPage);
            callback(items, [header])
        });
    },

    populate: function () {
        var self = this;
        this._onPageChange(BICst.TABLE_PAGE_OPERATOR.REFRESH, function (items, header) {
            var columnSize = BI.makeArray(header[0].length, "");
            self.table.attr("columnSize", columnSize);
            self.table.populate(items, header);
        });
    }
});
BI.DetailTablePopupDetailTable.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table_popup_detail_table", BI.DetailTablePopupDetailTable);