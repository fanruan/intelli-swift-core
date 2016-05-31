/**
 * 带有序号的表格
 *
 * Created by GUY on 2016/5/26.
 * @class BI.SequenceTable
 * @extends BI.Widget
 */
BI.SequenceTable = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.SequenceTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-sequence-table",
            el: {
                type: "bi.custom_scroll_table"
            },

            sequence: {},

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
            crossItems: [],

            startSequence: 1//开始的序号
        });
    },

    _init: function () {
        BI.SequenceTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.sequence = BI.createWidget(o.sequence, {
            type: "bi.sequence_table_list_number",
            startSequence: o.startSequence,
            isNeedFreeze: o.isNeedFreeze,
            header: o.header,
            footer: o.footer,
            items: o.items,
            crossHeader: o.crossHeader,
            crossItems: o.crossItems,
            headerRowSize: o.headerRowSize,
            footerRowSize: o.footerRowSize,
            rowSize: o.rowSize,
            width: 60
        });
        this.table = BI.createWidget(o.el, {
            type: "bi.custom_scroll_table",

            pageSpace: 95,
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
            self.fireEvent(BI.SequenceTable.EVENT_TABLE_AFTER_INIT);
        });
        this.table.on(BI.Table.EVENT_TABLE_RESIZE, function () {

        });

        this.table.on(BI.Table.EVENT_TABLE_SCROLL, function (scrollTop) {
            if (BI.isNotNull(scrollTop)) {
                self.sequence.setVerticalScroll(scrollTop);
            }
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
            self.fireEvent(BI.SequenceTable.EVENT_TABLE_AFTER_REGION_RESIZE);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            self.fireEvent(BI.SequenceTable.EVENT_TABLE_AFTER_COLUMN_RESIZE);
        });

        this.htape = BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.vtape",
                    cls: "sequence-table-wrapper",
                    items: [{
                        el: this.sequence
                    }, {
                        el: {
                            type: "bi.layout",
                            cls: "sequence-table-scroll"
                        },
                        height: 18
                    }]
                },
                width: 0
            }, {
                el: this.table
            }]
        })
    },

    resize: function () {
        this.table.resize();
        this.sequence.setVerticalScroll(this.table.getVerticalScroll());
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

    getVerticalScroll: function () {
        return this.table.getVerticalScroll();
    },

    setVPage: function (v) {
        this.table.setVPage && this.table.setVPage(v);
        this.sequence.setVPage(v);
    },

    getVPage: function () {
        return this.table.getVPage();
    },

    attr: function () {
        BI.SequenceTable.superclass.attr.apply(this, arguments);
        this.table.attr.apply(this.table, arguments);
        this.sequence.attr.apply(this.sequence, arguments);
    },

    showSequence: function () {
        var items = this.htape.attr("items");
        items[0].width = 60;
        this.htape.attr("items", items);
        this.htape.resize();
        this.table.resize();
    },

    hideSequence: function () {
        var items = this.htape.attr("items");
        items[0].width = 0;
        this.htape.attr("items", items);
        this.htape.resize();
        this.table.resize();
    },

    populate: function (items) {
        this.table.populate.apply(this.table, arguments);
        this.sequence.populate.apply(this.sequence, arguments);
        this.sequence.setVerticalScroll(this.table.getVerticalScroll());
    },

    destroy: function () {
        this.table.destroy();
        BI.SequenceTable.superclass.destroy.apply(this, arguments);
    }
});
BI.SequenceTable.EVENT_CHANGE = "SequenceTable.EVENT_CHANGE";
BI.SequenceTable.EVENT_TABLE_AFTER_INIT = "EVENT_TABLE_AFTER_INIT";
BI.SequenceTable.EVENT_TABLE_AFTER_COLUMN_RESIZE = "SequenceTable.EVENT_TABLE_AFTER_COLUMN_RESIZE";
BI.SequenceTable.EVENT_TABLE_AFTER_REGION_RESIZE = "SequenceTable.EVENT_TABLE_AFTER_REGION_RESIZE";
$.shortcut('bi.sequence_table', BI.SequenceTable);