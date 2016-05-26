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
                type: "bi.page_table"
            },

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

        this.table = BI.createWidget(o.el, {
            type: "bi.page_table",
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

        this.table.on(BI.CustomScrollTable.EVENT_RIGHT_SCROLL, function () {
            var dot = self.table.getRightHorizontalScroll();
            self.dots.push(dot);
            self.lock();
            if (dot < 50 && dot >= 0) {
                //显示页码
                self._showCurrentColumn();
            } else {
                self._hideCurrentColumn();
            }
        });

        this.table.on(BI.Table.EVENT_TABLE_AFTER_INIT, function () {
            self.fireEvent(BI.SequenceTable.EVENT_TABLE_AFTER_INIT);
        });
        this.table.on(BI.Table.EVENT_TABLE_RESIZE, function () {

        });

        this.table.on(BI.Table.EVENT_TABLE_SCROLL, function () {

        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
            self.fireEvent(BI.SequenceTable.EVENT_TABLE_AFTER_REGION_RESIZE);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            self.fireEvent(BI.SequenceTable.EVENT_TABLE_AFTER_COLUMN_RESIZE);
        });

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: this.pager,
                right: 0,
                bottom: 0
            }]
        })
    },

    _loading: function () {
        if (!this.loading) {
            this.loading = BI.Maskers.make(this.getName(), this);
            BI.createWidget({
                type: "bi.absolute",
                element: this.loading,
                items: [{
                    el: {
                        type: "bi.label",
                        cls: "page-table-loading-text",
                        text: BI.i18nText("BI-Loading"),
                        whiteSpace: "normal",
                        width: this._const.scrollWidth
                    },
                    right: 0,
                    top: 0,
                    bottom: this._const.scrollWidth
                }]
            })
        }
        BI.Maskers.show(this.getName());
    },

    _loaded: function () {
        BI.Maskers.hide(this.getName());
        this._hideCurrentColumn();
    },

    _showCurrentColumn: function () {
        var self = this, o = this.options;
        this._hideCurrentColumn();
        /**
         * 暂时不用显示分页信息
         */
        //this._currentColumn = BI.createWidget({
        //    type: "bi.text_button",
        //    cls: "page-table-current-column",
        //    text: BI.i18nText("BI-Di_A_Col", ((this.hpage - 1) * 20 + 1)),
        //    hgap: 15,
        //    height: 20,
        //    handler: function () {
        //        self._hideCurrentColumn();
        //    }
        //});
        //if (BI.isNotNull(o.isNeedFreeze)) {
        //    var regionSize = this.table.getRegionColumnSize();
        //    BI.createWidget({
        //        type: "bi.absolute",
        //        element: this.element,
        //        items: [{
        //            el: this._currentColumn,
        //            left: regionSize[0] + 2,
        //            bottom: this._const.scrollWidth + 2
        //        }]
        //    })
        //} else {
        //    BI.createWidget({
        //        type: "bi.absolute",
        //        element: this.element,
        //        items: [{
        //            el: this._currentColumn,
        //            left: 2,
        //            bottom: this._const.scrollWidth + 2
        //        }]
        //    })
        //}
    },

    _hideCurrentColumn: function () {
        this._currentColumn && this._currentColumn.destroy();
    },

    setHPage: function (v) {
        this.hpage = v;
    },

    setVPage: function (v) {
        this.pager.setValue(v);
    },

    getHPage: function () {
        return this.hpage;
    },

    getVPage: function () {
        return this.pager.getCurrentPage();
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
        BI.SequenceTable.superclass.attr.apply(this, arguments);
        this._hideCurrentColumn();
        this.table.attr.apply(this.table, arguments);
    },

    populate: function (items) {
        this.table.populate.apply(this.table, arguments);
        this._hideCurrentColumn();
        this.pager.populate();
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