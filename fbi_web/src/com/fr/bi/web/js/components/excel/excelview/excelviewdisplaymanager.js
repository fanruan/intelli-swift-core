/**
 * Created by zcf on 2016/11/24.
 */
BI.ExcelViewDisplayManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewDisplayManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "",
            excelId: "",
            formatItems: function (items) {
                var map = {};
                return BI.map(items, function (i, row) {
                    map[i] = {};
                    return BI.map(row, function (j, cell) {
                        map[i][j] = BI.createWidget({
                            type: "bi.label",
                            text: cell,
                            height: 18
                        });
                        return map[i][j];
                    });
                });
            }
        });
    },

    _init: function () {
        BI.ExcelViewDisplayManager.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.model = new BI.ExcelViewDisplayModel({
            excelId: o.excelId
        });
        this.excel = BI.createWidget({
            type: "bi.excel_table",
            element: this.element,
            isNeedMerge: true,
            mergeRule: function (row1, row2) {
                var o1 = row1.options;
                var o2 = row2.options;
                if (BI.isNull(o1) || BI.isNull(o2)) {
                    return false;
                } else {
                    return self._checkIsMerge(o1.column, o1.row, o2.column, o2.row);
                }
            }
        })
    },

    _checkIsMerge: function (column1, row1, column2, row2) {
        var flag = false;
        var mergeInfos = this.mergeInfos;
        if (BI.isNotNull(mergeInfos[0])) {
            BI.some(mergeInfos, function (i, mergeInfo) {
                var start = mergeInfo[0];
                var end = mergeInfo[1];
                var w = BI.parseInt(end[0]) - BI.parseInt(start[0]);
                var h = BI.parseInt(end[1]) - BI.parseInt(start[1]);
                var region = new BI.Region(BI.parseInt(start[0]), BI.parseInt(start[1]), w, h);
                return flag = region.isPointInside(column1, row1) && region.isPointInside(column2, row2);
            });
        }
        return flag
    },

    _formatItems: function (items) {
        var map = {};
        return BI.map(items, function (i, row) {
            map[i] = {};
            return BI.map(row, function (j, cell) {
                cell.attr("row", i);
                cell.attr("column", j);
                map[i][j] = cell;
                return map[i][j];
            });
        });
    },

    _populateExcel: function () {
        var o = this.options;
        var items = this.model.getItems();
        this.mergeInfos = this.model.getMergeInfos();
        this.excel.attr("columnSize", BI.makeArray(items[0].length, ""));
        this.excel.attr("mergeCols", BI.makeArray(items[0].length));
        this.excel.populate(this._formatItems(o.formatItems(items)));
    },

    getExcelId: function () {
        return this.model.getExcelId();
    },

    setExcelId: function (excelId) {
        this.model.setExcelId(excelId);
    },

    getFileName: function () {
        return this.model.getFileName();
    },

    getExcelData: function () {
        return this.model.getItems();
    },

    getMergeInfos: function () {
        return this.model.getMergeInfos();
    },

    populate: function () {
        var self = this, o = this.options;
        this.model.populate(function () {
            self._populateExcel();
        });

    }
});
$.shortcut("bi.excel_view_display_manager", BI.ExcelViewDisplayManager);