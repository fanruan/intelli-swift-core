/**
 * 带有序号的表格
 *
 * Created by GUY on 2016/5/26.
 * @class BI.Style1Table
 * @extends BI.Widget
 */
BI.Style1Table = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.Style1Table.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-style1-table",
            el: {
                type: "bi.sequence_table"
            },

            color: "#000000",

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
        BI.Style1Table.superclass._init.apply(this, arguments);
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

        this.table.on(BI.Table.EVENT_TABLE_AFTER_INIT, function () {
            self.fireEvent(BI.Style1Table.EVENT_TABLE_AFTER_INIT);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
            self.fireEvent(BI.Style1Table.EVENT_TABLE_AFTER_REGION_RESIZE);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            self.fireEvent(BI.Style1Table.EVENT_TABLE_AFTER_COLUMN_RESIZE);
        });

        if (BI.isKey(o.color)) {
            this.setColor(o.color);
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

    attr: function () {
        BI.Style1Table.superclass.attr.apply(this, arguments);
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
        var $table = this.table.element;
        $table.find(".scroll-top-left .table").css("background", color).css("color", "white");
        $table.find(".scroll-top-right .table").css("background", color).css("color", "white");
        $table.find(".scroll-bottom-right .table > thead > tr").css("background", color).css("color", "white");

        var oddColor = this._parseHEXAlpha2HEX(color, 0.2),
            evenColor = this._parseHEXAlpha2HEX(color, 0.05);
        $table.find(".scroll-bottom-left .table tr.odd,.scroll-bottom-right .table tr.odd").css("background", oddColor);
        $table.find(".scroll-bottom-left .table tr.even,.scroll-bottom-right .table tr.even").css("background", evenColor);

        $table.find(".sequence-table-title").css("background", color).css("color", "white");
        $table.find(".sequence-table-number.odd").css("background", this._parseHEXAlpha2HEX(color, 0.2));
        $table.find(".sequence-table-number.even").css("background", this._parseHEXAlpha2HEX(color, 0.05));

        var summaryColor = this._parseHEXAlpha2HEX(color, 0.4);
        $table.find(".scroll-bottom-left .table .summary-cell,.scroll-bottom-right .table .summary-cell").css("background", summaryColor);
        $table.find(".sequence-table-summary").css("background", summaryColor);
    },

    populate: function (items) {
        this.table.populate.apply(this.table, arguments);
        this.setColor(this.options.color);
    },

    destroy: function () {
        this.table.destroy();
        BI.Style1Table.superclass.destroy.apply(this, arguments);
    }
});
BI.Style1Table.EVENT_CHANGE = "Style1Table.EVENT_CHANGE";
BI.Style1Table.EVENT_TABLE_AFTER_INIT = "EVENT_TABLE_AFTER_INIT";
BI.Style1Table.EVENT_TABLE_AFTER_COLUMN_RESIZE = "Style1Table.EVENT_TABLE_AFTER_COLUMN_RESIZE";
BI.Style1Table.EVENT_TABLE_AFTER_REGION_RESIZE = "Style1Table.EVENT_TABLE_AFTER_REGION_RESIZE";
$.shortcut('bi.style1_table', BI.Style1Table);