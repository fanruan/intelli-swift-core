/**
 * 带有序号的表格
 *
 * Created by GUY on 2016/5/26.
 * @class BI.StyleTable
 * @extends BI.Widget
 */
BI.StyleTable = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.StyleTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-style1-table",
            el: {
                type: "bi.sequence_table"
            },

            color: null,
            style: null,

            isNeedResize: true,
            isResizeAdapt: false,

            isNeedFreeze: false,//是否需要冻结单元格
            freezeCols: [], //冻结的列号,从0开始,isNeedFreeze为true时生效

            isNeedMerge: false,//是否需要合并单元格
            mergeCols: [], //合并的单元格列号
            mergeRule: function (row1, row2) { //合并规则, 默认相等时合并
                return BI.isEqual(row1, row2);
            },

            columnSize: [],
            headerRowSize: 37,
            footerRowSize: 37,
            rowSize: 30,

            regionColumnSize: false,

            header: [],
            footer: false,
            items: [], //二维数组

            //交叉表头
            crossHeader: [],
            crossItems: []
        });
    },

    _init: function () {
        BI.StyleTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.table = BI.createWidget(o.el, {
            type: "bi.sequence_table",
            element: this.element,
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

            header: o.header,
            footer: o.footer,
            items: o.items,
            //交叉表头
            crossHeader: o.crossHeader,
            crossItems: o.crossItems
        });

        this.table.on(BI.SequenceTable.EVENT_TABLE_AFTER_INIT, function () {
            self.fireEvent(BI.StyleTable.EVENT_TABLE_AFTER_INIT);
        });
        this.table.on(BI.SequenceTable.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
            self.fireEvent(BI.StyleTable.EVENT_TABLE_AFTER_REGION_RESIZE);
        });
        this.table.on(BI.SequenceTable.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            self.fireEvent(BI.StyleTable.EVENT_TABLE_AFTER_COLUMN_RESIZE);
        });

        if (BI.isKey(o.color)) {
            this.setColor(o.color);
        }
        if (BI.isKey(o.style)) {
            this.setStyle(o.style);
        }
    },

    resize: function () {
        this.table.resize();
    },

    setColumnSize: function (size) {
        this.table.setColumnSize(size);
    },

    getColumnSize: function () {
        return this.table.getColumnSize();
    },

    getCalculateColumnSize: function () {
        return this.table.getCalculateColumnSize();
    },

    getCalculateRegionColumnSize: function () {
        return this.table.getCalculateRegionColumnSize();
    },

    setVPage: function (v) {
        this.table.setVPage(v);
    },

    getVPage: function () {
        return this.table.getVPage();
    },

    attr: function () {
        BI.StyleTable.superclass.attr.apply(this, arguments);
        this.table.attr.apply(this.table, arguments);
    },

    showSequence: function () {
        this.table.showSequence();
    },

    hideSequence: function () {
        this.table.hideSequence();
    },

    _parseHEXAlpha2HEX: function (hex, alpha) {
        var rgb = BI.DOM.hex2rgb(hex);
        var rgbJSON = BI.DOM.rgb2json(rgb);
        rgbJSON.a = alpha;
        //return BI.DOM.json2rgba(rgbJSON);
        return BI.DOM.rgba2rgb(BI.DOM.json2rgba(rgbJSON));
    },

    setColor: function (color) {
        this.options.color = color;
        this.setStyleAndColor(this.options.style, color);
    },

    populate: function (items) {
        this.table.populate.apply(this.table, arguments);
        this.setColor(this.options.color);
    },

    destroy: function () {
        this.table.destroy();
        BI.StyleTable.superclass.destroy.apply(this, arguments);
    },

    setStyleAndColor: function (style, color) {
        var $table = this.table.element;
        switch (style) {
            case BI.StyleTable.STYLE1:
                $table.find(".scroll-top-left .table").css("background", color).css("color", "white");
                $table.find(".scroll-top-right .table").css("background", color).css("color", "white");
                $table.find(".scroll-bottom-right .table > thead > tr").css("background", color).css("color", "white");

                var oddColor = this._parseHEXAlpha2HEX(color, 0.2),
                    evenColor = this._parseHEXAlpha2HEX(color, 0.05);
                $table.find(".scroll-bottom-left .table > tbody tr.odd,.scroll-bottom-right .table > tbody tr.odd").css("background", oddColor);
                $table.find(".scroll-bottom-left .table > tbody tr.even,.scroll-bottom-right .table > tbody tr.even").css("background", evenColor);

                $table.find(".sequence-table-title").css("background", color).css("color", "white");
                $table.find(".sequence-table-number.odd").css("background", this._parseHEXAlpha2HEX(color, 0.2));
                $table.find(".sequence-table-number.even").css("background", this._parseHEXAlpha2HEX(color, 0.05));

                var summaryColor = this._parseHEXAlpha2HEX(color, 0.4);
                $table.find(".scroll-bottom-left .table .summary-cell,.scroll-bottom-right .table .summary-cell").css({
                    "background": summaryColor,
                    color: "#1a1a1a",
                    fontWeight: "bold"
                });
                $table.find(".scroll-bottom-left .table .summary-cell.last,.scroll-bottom-right .table .summary-cell.last").css({
                    "background": color,
                    color: "#ffffff",
                    fontWeight: "bold"
                });
                $table.find(".sequence-table-summary").css({
                    "background": summaryColor,
                    color: "#1a1a1a",
                    fontWeight: "bold"
                });
                $table.find(".sequence-table-summary.last").css({
                    "background": color,
                    color: "#ffffff",
                    fontWeight: "bold"
                });
                break;
            case BI.StyleTable.STYLE2:
                $table.find(".scroll-top-left .table").css("background", color).css("color", "white");
                $table.find(".scroll-top-right .table").css("background", color).css("color", "white");
                $table.find(".scroll-bottom-right .table > thead > tr").css("background", color).css("color", "white");
                $table.find(".sequence-table-title").css("background", color).css("color", "white");
                $table.find(".sequence-table-title").css("background", color).css("color", "white");

                $table.find(".scroll-bottom-left .table .summary-cell,.scroll-bottom-right .table .summary-cell").css({
                    "background": "white",
                    color: "#1a1a1a",
                    fontWeight: "bold"
                });
                $table.find(".sequence-table-summary").css({
                    "background": "white",
                    color: "#1a1a1a",
                    fontWeight: "bold"
                });

                $table.find(".sequence-table-summary.last").css({
                    "background": "white",
                    color: color,
                    fontWeight: "bold"
                });
                $table.find(".scroll-bottom-left .table .summary-cell.last,.scroll-bottom-right .table .summary-cell.last").css({
                    "background": "white",
                    color: color,
                    fontWeight: "bold"
                });
                break;
            case BI.StyleTable.STYLE3:
                $table.find(".scroll-top-left .table").css("background", "white").css("color", "#808080");
                $table.find(".scroll-top-right .table").css("background", "white").css("color", "#808080");
                $table.find(".scroll-bottom-right .table > thead > tr").css("background", "white").css("color", "#808080");
                $table.find(".sequence-table-title").css("background", "white").css("color", "#808080");
                $table.find(".sequence-table-title").css("background", "white").css("color", "#808080");
                $table.find(".scroll-bottom-left .table .summary-cell,.scroll-bottom-right .table .summary-cell").css({
                    "background": "white",
                    color: "#1a1a1a",
                    fontWeight: "bold"
                });
                $table.find(".sequence-table-summary").css({
                    "background": "white",
                    color: "#1a1a1a",
                    fontWeight: "bold"
                });
                $table.find(".sequence-table-summary.last").css({
                    "background": "white",
                    color: color,
                    fontWeight: "bold"
                });
                $table.find(".scroll-bottom-left .table .summary-cell.last,.scroll-bottom-right .table .summary-cell.last").css({
                    "background": "white",
                    color: color,
                    fontWeight: "bold"
                });
                break;
        }
    },

    setStyle: function (style) {
        this.options.style = style;
        this.setStyleAndColor(style, this.options.color);
    }
});
BI.extend(BI.StyleTable, {
    STYLE1: BICst.TABLE_STYLE.STYLE1,
    STYLE2: BICst.TABLE_STYLE.STYLE2,
    STYLE3: BICst.TABLE_STYLE.STYLE3
});
BI.StyleTable.EVENT_CHANGE = "StyleTable.EVENT_CHANGE";
BI.StyleTable.EVENT_TABLE_AFTER_INIT = "EVENT_TABLE_AFTER_INIT";
BI.StyleTable.EVENT_TABLE_AFTER_COLUMN_RESIZE = "StyleTable.EVENT_TABLE_AFTER_COLUMN_RESIZE";
BI.StyleTable.EVENT_TABLE_AFTER_REGION_RESIZE = "StyleTable.EVENT_TABLE_AFTER_REGION_RESIZE";
$.shortcut('bi.style_table', BI.StyleTable);