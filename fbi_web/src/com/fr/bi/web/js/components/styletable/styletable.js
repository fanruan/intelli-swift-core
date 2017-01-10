/**
 * 带有序号的表格
 *
 * Created by GUY on 2016/5/26.
 * @class BI.StyleTable
 * @extends BI.Widget
 */
BI.StyleTable = BI.inherit(BI.Widget, {
    _const: {
        light: "#ffffff",
        dark: "#1a1a1a"
    },

    _defaultConfig: function () {
        return BI.extend(BI.StyleTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-style-table",
            el: {
                type: "bi.page_table"
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
            headerRowSize: 25,
            footerRowSize: 25,
            rowSize: 25,

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
            type: "bi.page_table",
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
            self.fireEvent(BI.StyleTable.EVENT_TABLE_AFTER_INIT);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
            self.fireEvent(BI.StyleTable.EVENT_TABLE_AFTER_REGION_RESIZE);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
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

    setHPage: function (v) {
        this.table.setHPage(v);
    },

    getHPage: function () {
        return this.table.getHPage();
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

    refresh: function () {
        this.table.refresh.apply(this.table, arguments);
        this.setStyleAndColor(this.options.style, this.options.color);
    },

    populate: function (items) {
        this.table.populate.apply(this.table, arguments);
        this.setStyleAndColor(this.options.style, this.options.color);
    },

    destroy: function () {
        this.table.destroy();
        BI.StyleTable.superclass.destroy.apply(this, arguments);
    },

    setStyleAndColor: function (style, color) {
        if (BI.isNull(style) || BI.isNull(color)) {
            return;
        }
        var $table = this.table.element;
        var $topLeft = $table.find(".scroll-top-left .table"), $topRight = $table.find(".scroll-top-right .table"),
            $bottomLeft = $table.find(".scroll-bottom-left .table"), $bottomRight = $table.find(".scroll-bottom-right .table"),
            $sequenceHeader = $table.find(".sequence-table-title"), $oddSequence = $table.find(".sequence-table-number.odd"),
            $evenSequence = $table.find(".sequence-table-number.even");
        var $bottomLeftSum = $bottomLeft.find(".summary-cell"), $bottomRightSum = $bottomRight.find(">tbody .summary-cell"),
            $bottomLeftSumLast = $bottomLeft.find(".summary-cell.last"), $bottomRightSumLast = $bottomRight.find(">tbody .summary-cell.last"),
            $sequenceSum = $table.find(".sequence-table-summary"), $sequenceSumLast = $table.find(".sequence-table-summary.last");

        var isDark = BI.DOM.isDarkColor(color);

        var fontColor = isDark ? this._const.light : this._const.dark;

        var $rowHeader = $table.find(".layer-tree-table-title");
        switch (style) {
            case BI.StyleTable.STYLE1:
                var oddColor = this._parseHEXAlpha2HEX(color, 0.2),
                    evenColor = this._parseHEXAlpha2HEX(color, 0.05),
                    summaryColor = this._parseHEXAlpha2HEX(color, 0.4);
                var oddFontColor = BI.DOM.isDarkColor(BI.DOM.rgb2hex(oddColor)) ? this._const.light : this._const.dark,
                    evenFontColor = BI.DOM.isDarkColor(BI.DOM.rgb2hex(evenColor)) ? this._const.light : this._const.dark,
                    summaryFontColor = BI.DOM.isDarkColor(BI.DOM.rgb2hex(summaryColor)) ? this._const.light : this._const.dark;

                //background
                $topLeft.css({"background": color, "color": fontColor});
                $topRight.css({"background": color, "color": fontColor});
                $bottomRight.find(">thead > tr").css({"background": color, "color": fontColor});
                $bottomLeft.find("> tbody tr.odd").css({"background": oddColor, "color": oddFontColor});
                $bottomRight.find("> tbody tr.odd").css({"background": oddColor, "color": oddFontColor});
                $bottomLeft.find("> tbody tr.even").css({"background": evenColor, "color": evenFontColor});
                $bottomRight.find("> tbody tr.even").css({"background": evenColor, "color": evenFontColor});
                $sequenceHeader.css({"background": color, "color": fontColor});
                $oddSequence.css({"background": oddColor, "color": oddFontColor});
                $evenSequence.css({"background": evenColor, "color": evenFontColor});
                $bottomLeftSum.css({"background": summaryColor, "color": summaryFontColor});
                $bottomRightSum.css({"background": summaryColor, "color": summaryFontColor});
                $sequenceSum.css({"background": summaryColor, "color": summaryFontColor});
                $bottomLeftSumLast.css({"background": color, "color": fontColor});
                $bottomRightSumLast.css({"background": color, "color": fontColor});
                $sequenceSumLast.css({"background": color, "color": fontColor});

                //color
                // $topLeft.css("color", fontColor);
                // $topRight.css("color", fontColor);
                // $bottomRight.find("thead > tr").css("color", fontColor);
                // $sequenceHeader.css("color", fontColor);
                // $bottomLeftSum.css("color", this._const.dark);
                // $bottomRightSum.css("color", this._const.dark);
                // $sequenceSum.css("color", this._const.dark);
                // $bottomLeftSumLast.css("color", fontColor);
                // $bottomRightSumLast.css("color", fontColor);
                // $sequenceSumLast.css("color", fontColor);

                //font weight
                $bottomLeftSum.css("fontWeight", "bold");
                $bottomRightSum.css("fontWeight", "bold");
                $sequenceSum.css("fontWeight", "bold");
                $bottomLeftSumLast.css("fontWeight", "bold");
                $bottomRightSumLast.css("fontWeight", "bold");
                $sequenceSumLast.css("fontWeight", "bold");
                $rowHeader.css("fontWeight", "bold");

                break;
            case BI.StyleTable.STYLE2:
                //background
                $topLeft.css({"background": color, "color": fontColor});
                $topRight.css({"background": color, "color": fontColor});
                $bottomRight.find(">thead > tr").css({"background": color, "color": fontColor});
                $sequenceHeader.css({"background": color, "color": fontColor});
                // $bottomLeftSum.css({"background": this._const.light});
                // $bottomRightSum.css({"background": this._const.light});
                // $sequenceSum.css({"background": this._const.light});
                // $bottomLeftSumLast.css({"background": this._const.light});
                // $bottomRightSumLast.css({"background": this._const.light});
                // $sequenceSumLast.css({"background": this._const.light});

                //color
                // $topLeft.css("color", fontColor);
                // $topRight.css("color", fontColor);
                // $bottomRight.find("thead > tr").css("color", fontColor);
                // $sequenceHeader.css("color", fontColor);
                // $bottomLeftSum.css("color", this._const.dark);
                // $bottomRightSum.css("color", this._const.dark);
                // $sequenceSum.css("color", this._const.dark);
                // $bottomLeftSumLast.css("color", color);
                // $bottomRightSumLast.css("color", color);
                // $sequenceSumLast.css("color", color);

                //font weight
                $bottomLeftSum.css("fontWeight", "bold");
                $bottomRightSum.css("fontWeight", "bold");
                $sequenceSum.css("fontWeight", "bold");
                $bottomLeftSumLast.css("fontWeight", "bold");
                $bottomRightSumLast.css("fontWeight", "bold");
                $sequenceSumLast.css("fontWeight", "bold");
                $rowHeader.css("fontWeight", "bold");

                break;
            case BI.StyleTable.STYLE3:
                //background
                // $topLeft.css("background", this._const.light);
                // $topRight.css("background", this._const.light);
                // $bottomRight.find(">thead > tr").css("background", this._const.light);
                // $sequenceHeader.css("background", this._const.light);
                // $bottomLeftSum.css("background", this._const.light);
                // $bottomRightSum.css("background", this._const.light);
                // $sequenceSum.css("background", this._const.light);
                $bottomLeftSumLast.css({"background": color, "color": fontColor});
                $bottomRightSumLast.css({"background": color, "color": fontColor});
                $sequenceSumLast.css({"background": color, "color": fontColor});
                $sequenceHeader.css({"background": "none", "color": "inherit"});

                //color
                // $topLeft.css("color", this._const.dark);
                // $topRight.css("color", this._const.dark);
                // $bottomRight.find("thead > tr").css("color", this._const.dark);
                // $sequenceHeader.css("color", this._const.dark);
                // $bottomLeftSum.css("color", this._const.dark);
                // $bottomRightSum.css("color", this._const.dark);
                // $sequenceSum.css("color", this._const.dark);
                // $bottomLeftSumLast.css("color", fontColor);
                // $bottomRightSumLast.css("color", fontColor);
                // $sequenceSumLast.css("color", fontColor);

                //font weight
                $bottomLeftSum.css("fontWeight", "bold");
                $bottomRightSum.css("fontWeight", "bold");
                $sequenceSum.css("fontWeight", "bold");
                $bottomLeftSumLast.css("fontWeight", "bold");
                $bottomRightSumLast.css("fontWeight", "bold");
                $sequenceSumLast.css("fontWeight", "bold");
                $rowHeader.css("fontWeight", "bold");

                break;
        }

        //表头
        $table.find(".scroll-bottom-right .table > thead > tr,.sequence-table-title").css({
            fontWeight: "bold"
        });
        $table.find(".scroll-top-left .table .header-cell-text").css({
            fontWeight: "bold"
        });
        $table.find(".scroll-top-right .table").find(" .header-cell-text, .cross-table-target-header, .cross-item-cell, .summary-cell").css({
            fontWeight: "bold"
        });
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