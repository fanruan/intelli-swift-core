/**
 * 预览表 表格预处理
 *
 * Created by GUY on 2016/11/24.
 * @class BI.PretreatedTable
 * @extends BI.Widget
 */
BI.PretreatedTable = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.PretreatedTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-pretreated-table",
            isNeedFreeze: false,
            freezeCols: [],
            rowSize: null,
            columnSize: [],
            headerRowSize: 30,
            header: [],
            items: []
        });
    },

    _init: function () {
        BI.PretreatedTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this._initGroups();
        this.table = BI.createWidget({
            type: "bi.preview_table",
            element: this.element,

            isNeedFreeze: o.isNeedFreeze,
            freezeCols: o.freezeCols,

            rowSize: o.rowSize,
            columnSize: o.columnSize,
            headerRowSize: o.headerRowSize,

            header: [this._formatHeader(o.header)],
            items: this._formatItems(o.items)
        });
    },

    _initGroups: function () {
        var self = this, o = this.options;
        this.groups = [];
        BI.each(o.header, function (i, item) {
            self.groups[i] = {};
            switch (item.fieldType) {
                case BICst.COLUMN.DATE:
                    self.groups[i].titleFormatter = function (title) {
                        switch (item.group) {
                            case BICst.GROUP.Y:
                                return title + "(" + BI.i18nText("BI-Year_Fen") + ")";
                            case BICst.GROUP.S:
                                return title + "(" + BI.i18nText("BI-Quarter") + ")";
                            case BICst.GROUP.M:
                                return title + "(" + BI.i18nText("BI-Multi_Date_Month") + ")";
                                break;
                            case BICst.GROUP.W:
                                return title + "(" + BI.i18nText("BI-Week_XingQi") + ")";
                                break;
                            case BICst.GROUP.YMD:
                                return title + "(" + BI.i18nText("BI-Date") + ")";
                                break;
                            case BICst.GROUP.YMDHMS:
                                return title + "(" + BI.i18nText("BI-Time_ShiKe") + ")";
                                break;
                        }
                        return title;
                    };
                    self.groups[i].valueFormatter = function (value) {
                        if (BI.isNull(value)) {
                            return value;
                        }
                        var d = new Date(value);
                        switch (item.group) {
                            case BICst.GROUP.Y:
                                return d.getFullYear();
                            case BICst.GROUP.S:
                                return Date._QN[getSeason(d.getMonth()) + 1];
                            case BICst.GROUP.M:
                                return d.print("%B");
                                break;
                            case BICst.GROUP.W:
                                return d.print("%A");
                                break;
                            case BICst.GROUP.YMD:
                                return d.print("%Y-%X-%d");
                                break;
                            case BICst.GROUP.YMDHMS:
                                return d.print("%Y-%X-%d %H:%M:%S");
                                break;
                        }
                        return value;
                        function getSeason(month) {
                            return Math.floor(month / 3);
                        }
                    };
                    break;
                default:
                    self.groups[i].titleFormatter = function (title) {
                        return title
                    };
                    self.groups[i].valueFormatter = function (value) {
                        return value
                    };
                    break;
            }
        })
    },

    _formatHeader: function (header) {
        var self = this, o = this.options;
        BI.each(header, function (i, item) {
            item.text = self.groups[i].titleFormatter(item.text || item.value);
        });
        return header;
    },

    _formatItems: function (items) {
        var self = this, o = this.options;
        BI.each(items, function (i, row) {
            BI.each(row, function (j, item) {
                item.text = self.groups[j].valueFormatter(item.text || item.value);
            })
        });
        return items;
    },

    setColumnSize: function (columnSize) {
        return this.table.setColumnSize(columnSize);
    },

    getColumnSize: function () {
        return this.table.getColumnSize();
    },

    getCalculateColumnSize: function () {
        return this.table.getCalculateColumnSize();
    },

    setHeaderColumnSize: function (columnSize) {
        return this.table.setHeaderColumnSize(columnSize);
    },

    setRegionColumnSize: function (columnSize) {
        return this.table.setRegionColumnSize(columnSize);
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
        return this.table.getScrollRegionColumnSize()
    },

    getScrollRegionRowSize: function () {
        return this.table.getScrollRegionRowSize()
    },

    hasVerticalScroll: function () {
        return this.table.hasVerticalScroll();
    },

    setVerticalScroll: function (scrollTop) {
        return this.table.setVerticalScroll(scrollTop);
    },

    setLeftHorizontalScroll: function (scrollLeft) {
        return this.table.setLeftHorizontalScroll(scrollLeft)
    },

    setRightHorizontalScroll: function (scrollLeft) {
        return this.table.setRightHorizontalScroll(scrollLeft);
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

    populate: function (items, header) {
        var o = this.options;
        o.items = items || [];
        o.header = header || [];
        this._initGroups();
        this.table.populate(this._formatItems(o.items), [this._formatHeader(o.header)]);
    },

    destroy: function () {
        this.table.destroy();
        BI.PretreatedTable.superclass.destroy.apply(this, arguments);
    }
});
$.shortcut('bi.pretreated_table', BI.PretreatedTable);