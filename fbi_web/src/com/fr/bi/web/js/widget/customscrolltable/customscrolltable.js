/**
 * 自定义滚动条的表格
 *
 * Created by GUY on 2016/2/15.
 * @class BI.CustomScrollTable
 * @extends BI.Widget
 */
BI.CustomScrollTable = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CustomScrollTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-scroll-table",
            el: {
                type: "bi.adaptive_table"
            },

            //留给分页控件的空间
            pageSpace: 0,
            scrollWidth: 18,

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
            rowSize: 37,

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
        BI.CustomScrollTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.table = BI.createWidget(o.el, {
            type: "bi.adaptive_table",
            isNeedResize: true,
            isResizeAdapt: false,

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
        this._initScroll();
        if (o.isNeedFreeze === true) {
            BI.defer(function () {
                self._resizeFreezeScroll();
            });
        } else if (o.isNeedFreeze === false) {
            BI.defer(function () {
                self._resizeScroll();
            });
        }

        //事件锁，防止反跳
        this._lock = BI.debounce(function () {
            self._release = true;
            self._scrolling = null;
        }, 100);
        this._release = true;
        this._scrolling = null;

        this.table.on(BI.Table.EVENT_TABLE_AFTER_INIT, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_INIT, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_SCROLL, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_SCROLL);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE);
        });
    },

    _initScroll: function () {
        var self = this, o = this.options;
        if (this.bottomLeftScrollBar) {
            this.bottomLeftScrollBar.destroy();
        }
        if (this.bottomRightScrollBar) {
            this.bottomRightScrollBar.destroy();
        }
        if (this.topScrollBar) {
            this.topScrollBar.destroy();
        }
        if (o.isNeedFreeze === true) {
            this.bottomLeftScrollBar = BI.createWidget({
                type: "bi.custom_scroll_table_scroll_bar",
                height: o.scrollWidth,
                axis: BI.Axis.Horizontal
            });
            this.bottomLeftScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL, function () {
                self._distributeTasks(true);
                self.fireEvent(BI.CustomScrollTable.EVENT_LEFT_SCROLL);
            });
            this.bottomRightScrollBar = BI.createWidget({
                type: "bi.custom_scroll_table_scroll_bar",
                height: o.scrollWidth,
                axis: BI.Axis.Horizontal
            });
            this.bottomRightScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL, function () {
                self._distributeTasks(true);
            });
            this.topScrollBar = BI.createWidget({
                type: "bi.custom_scroll_table_scroll_bar",
                width: o.scrollWidth,
                axis: BI.Axis.Vertical
            });
            this.topScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL, function () {
                self._distributeTasks(true);
            });
            BI.createWidget({
                type: "bi.absolute",
                element: this.element,
                items: [{
                    el: this.table,
                    left: 0,
                    top: 0,
                    right: o.scrollWidth,
                    bottom: o.scrollWidth
                }, {
                    el: this.bottomLeftScrollBar,
                    left: 0,
                    bottom: 0
                }, {
                    el: this.bottomRightScrollBar,
                    right: o.pageSpace || 0,
                    bottom: 0
                }, {
                    el: this.topScrollBar,
                    right: 0,
                    bottom: o.scrollWidth,
                    top: 0
                }]
            });

            this.table.on(BI.Table.EVENT_TABLE_SCROLL, function () {
                self._distributeTasks();
            });

            //important: 当region区域拖拽过程中有可能会发生横向滚动条翻页事件
            this.table.on(BI.Table.EVENT_TABLE_REGION_RESIZE, function () {
                self.bottomRightScrollBar.setScrollWidth(0);
            });

            this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
                self._resizeFreezeScroll();
            });
            this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
                self._resizeFreezeScroll();
            });

            BI.Resizers.remove(this.getName()).add(this.getName(), function (e) {
                if (o.isNeedFreeze === true) {
                    self._resizeFreezeScroll();
                    self._scrollFreezeScroll();
                }
            });
        } else if (o.isNeedFreeze === false) {
            this.bottomRightScrollBar = BI.createWidget({
                type: "bi.custom_scroll_table_scroll_bar",
                height: o.scrollWidth,
                axis: BI.Axis.Horizontal
            });
            this.bottomRightScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL, function () {
                self._distributeTasks(true);
            });
            this.topScrollBar = BI.createWidget({
                type: "bi.custom_scroll_table_scroll_bar",
                width: o.scrollWidth,
                axis: BI.Axis.Vertical
            });
            this.topScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL, function () {
                self._distributeTasks(true);
            });
            BI.createWidget({
                type: "bi.absolute",
                element: this.element,
                items: [{
                    el: this.table,
                    left: 0,
                    top: 0,
                    right: o.scrollWidth,
                    bottom: o.scrollWidth
                }, {
                    el: this.bottomRightScrollBar,
                    left: 0,
                    right: o.pageSpace,
                    bottom: 0
                }, {
                    el: this.topScrollBar,
                    right: 0,
                    bottom: o.scrollWidth,
                    top: 0
                }]
            });

            this.table.on(BI.Table.EVENT_TABLE_RESIZE, function () {
                self._resizeScroll();
            });
            this.table.on(BI.Table.EVENT_TABLE_SCROLL, function () {
                self._distributeTasks();
            });

            this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
                self._resizeScroll();
            });
            this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
                self._resizeScroll();
            });
        }
        if (o.isNeedFreeze === true || o.isNeedFreeze === false) {
            this.bottomRightScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL_BEHIND, function () {
                self.fireEvent(BI.CustomScrollTable.EVENT_SCROLL_TO_RIGHT);
            });
            this.bottomRightScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL, function () {
                self.fireEvent(BI.CustomScrollTable.EVENT_RIGHT_SCROLL);
            });
            this.bottomRightScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL_FRONT, function () {
                self.fireEvent(BI.CustomScrollTable.EVENT_SCROLL_TO_LEFT);
            });
            this.topScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL_BEHIND, function () {
                self.fireEvent(BI.CustomScrollTable.EVENT_SCROLL_TO_BOTTOM);
            });
            this.topScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL_FRONT, function () {
                self.fireEvent(BI.CustomScrollTable.EVENT_SCROLL_TO_TOP);
            });
            this.topScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL, function () {
                self.fireEvent(BI.CustomScrollTable.EVENT_VERTICAL_SCROLL);
            });
        }
    },

    _distributeTasks: function (isScroll) {
        var o = this.options;
        if (this._release === true) {
            this._scrolling = !!isScroll;
        }
        if (!this._release && BI.isNotNull(this._scrolling) && !!isScroll !== this._scrolling) {
            this._lock();
            return;
        }
        this._release = false;
        this._lock();
        if (o.isNeedFreeze === true) {
            //如果是滚动条触发的scroll
            if (isScroll === true) {
                this._scrollFreezeRegion();
            } else {
                this._scrollFreezeScroll();
            }
        } else if (o.isNeedFreeze === false) {
            if (isScroll === true) {
                this._scrollRegion();
            } else {
                this._scrollScroll();
            }
        }
    },

    _resizeFreezeScroll: function () {
        if (this.options.isNeedFreeze === true) {
            var regionSize = this.table.getCalculateRegionColumnSize();
            this.bottomLeftScrollBar.element.width(regionSize[0]);
            this.bottomRightScrollBar.element.css("left", regionSize[0]);

            var ratio = this.bottomRightScrollBar.element.width() / regionSize[1];

            var columnSize = this.table.getScrollRegionColumnSize();
            this.bottomLeftScrollBar.setScrollWidth(columnSize[0]);
            this.bottomRightScrollBar.setScrollWidth(columnSize[1] * ratio);

            regionSize = this.table.getCalculateRegionRowSize();
            ratio = this.topScrollBar.element.height() / regionSize[1];

            var rowSize = this.table.getScrollRegionRowSize();
            this.topScrollBar.setScrollHeight(rowSize[1] * ratio);
        }
    },

    _scrollFreezeScroll: function () {
        if (this.options.isNeedFreeze === true) {
            var regionSize = this.table.getCalculateRegionColumnSize();
            var ratio = this.bottomRightScrollBar.element.width() / regionSize[1];

            var scrollLeft = this.table.getLeftHorizontalScroll();
            this.bottomLeftScrollBar.setScrollLeft(scrollLeft);

            scrollLeft = this.table.getRightHorizontalScroll();
            this.bottomRightScrollBar.setScrollLeft(scrollLeft * ratio);


            regionSize = this.table.getCalculateRegionRowSize();
            ratio = this.topScrollBar.element.height() / regionSize[1];

            var scrollTop = this.table.getVerticalScroll();
            this.topScrollBar.setScrollTop(scrollTop * ratio);
        }
    },

    _scrollFreezeRegion: function () {
        if (this.options.isNeedFreeze === true) {
            var regionSize = this.table.getCalculateRegionColumnSize();
            var ratio = this.bottomRightScrollBar.element.width() / regionSize[1];

            var scrollLeft = this.bottomLeftScrollBar.getScrollLeft();
            this.table.setLeftHorizontalScroll(scrollLeft);

            scrollLeft = this.bottomRightScrollBar.getScrollLeft();
            this.table.setRightHorizontalScroll(scrollLeft / ratio);


            regionSize = this.table.getCalculateRegionRowSize();
            ratio = this.topScrollBar.element.height() / regionSize[1];

            var scrollTop = this.topScrollBar.getScrollTop();
            this.table.setVerticalScroll(scrollTop / ratio);
        }
    },

    _resizeScroll: function () {
        if (this.options.isNeedFreeze === false) {
            var regionSize = this.table.getCalculateRegionColumnSize();
            var ratio = this.bottomRightScrollBar.element.width() / regionSize[0];

            var columnSize = this.table.getScrollRegionColumnSize();
            this.bottomRightScrollBar.setScrollWidth(columnSize[0] * ratio);

            regionSize = this.table.getCalculateRegionRowSize();
            ratio = this.topScrollBar.element.height() / regionSize[0];

            var rowSize = this.table.getScrollRegionRowSize();
            this.topScrollBar.setScrollHeight(rowSize[0] * ratio);
        }
    },

    _scrollScroll: function () {
        if (this.options.isNeedFreeze === false) {
            var regionSize = this.table.getCalculateRegionColumnSize();
            var ratio = this.bottomRightScrollBar.element.width() / regionSize[0];

            var scrollLeft = this.table.getLeftHorizontalScroll();
            this.bottomRightScrollBar.setScrollLeft(scrollLeft / ratio);


            regionSize = this.table.getCalculateRegionRowSize();
            ratio = this.topScrollBar.element.height() / regionSize[0];

            var scrollTop = this.table.getVerticalScroll();
            this.topScrollBar.setScrollTop(scrollTop * ratio);
        }
    },

    _scrollRegion: function () {
        if (this.options.isNeedFreeze === false) {
            var regionSize = this.table.getCalculateRegionColumnSize();
            var ratio = this.bottomRightScrollBar.element.width() / regionSize[0];

            var scrollLeft = this.bottomRightScrollBar.getScrollLeft();
            this.table.setLeftHorizontalScroll(scrollLeft / ratio);

            regionSize = this.table.getCalculateRegionRowSize();
            ratio = this.topScrollBar.element.height() / regionSize[0];

            var scrollTop = this.topScrollBar.getScrollTop();
            this.table.setVerticalScroll(scrollTop / ratio);
        }
    },


    resize: function () {
        this.table.resize();
    },

    setColumnSize: function (columnSize) {
        this.table.setColumnSize(columnSize);
    },

    getColumnSize: function () {
        return this.table.getColumnSize();
    },

    getCalculateColumnSize: function () {
        return this.table.getCalculateColumnSize();
    },

    setHeaderColumnSize: function (columnSize) {
        this.table.setHeaderColumnSize(columnSize);
    },

    setRegionColumnSize: function (columnSize) {
        this.table.setRegionColumnSize(columnSize);
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
        return this.table.getScrollRegionColumnSize();
    },

    getScrollRegionRowSize: function () {
        return this.table.getScrollRegionRowSize();
    },

    hasVerticalScroll: function () {
        return this.table.hasVerticalScroll();
    },

    setVerticalScroll: function (scrollTop) {
        this.table.setVerticalScroll(scrollTop);
    },

    setLeftHorizontalScroll: function (scrollLeft) {
        this.table.setLeftHorizontalScroll(scrollLeft);
    },

    setRightHorizontalScroll: function (scrollLeft) {
        this.table.setRightHorizontalScroll(scrollLeft);
    },

    getVerticalScroll: function () {
        return this.topScrollBar.getScrollTop();
        //return this.table.getVerticalScroll();
    },

    getLeftHorizontalScroll: function () {
        return this.bottomLeftScrollBar.getScrollLeft();
        //return this.table.getLeftHorizontalScroll();
    },

    getRightHorizontalScroll: function () {
        return this.bottomRightScrollBar.getScrollLeft();
        //return this.table.getRightHorizontalScroll();
    },

    getColumns: function () {
        return this.table.getColumns();
    },

    attr: function () {
        var o = this.options;
        var isNeedFreeze = o.isNeedFreeze;
        BI.CustomScrollTable.superclass.attr.apply(this, arguments);
        if (isNeedFreeze !== o.isNeedFreeze) {
            this._initScroll();
        }
        this.table.attr.apply(this.table, arguments);
    },

    populate: function (items) {
        var self = this, o = this.options;
        this.table.populate.apply(this.table, arguments);
        if (o.isNeedFreeze === true) {
            BI.defer(function () {
                self._resizeFreezeScroll();
                self._scrollFreezeScroll();
            });
        } else if (o.isNeedFreeze === false) {
            BI.defer(function () {
                self._resizeScroll();
                self._scrollScroll();
            });
        }
    },

    scrollToRight: function () {
        this.bottomRightScrollBar.scrollToRight();
    },

    scrollToLeft: function () {
        this.bottomRightScrollBar.scrollToLeft();
    },

    scrollToTop: function () {
        this.topScrollBar.scrollToTop();
    },

    scrollToBottom: function () {
        this.topScrollBar.scrollToBottom();
    },

    destroy: function () {
        this.table.destroy();
        BI.CustomScrollTable.superclass.destroy.apply(this, arguments);
    }
});
BI.CustomScrollTable.EVENT_LEFT_SCROLL = "CustomScrollTable.EVENT_LEFT_SCROLL";
BI.CustomScrollTable.EVENT_RIGHT_SCROLL = "CustomScrollTable.EVENT_RIGHT_SCROLL";
BI.CustomScrollTable.EVENT_VERTICAL_SCROLL = "CustomScrollTable.EVENT_VERTICAL_SCROLL";

BI.CustomScrollTable.EVENT_SCROLL_TO_RIGHT = "CustomScrollTable.EVENT_SCROLL_TO_RIGHT";
BI.CustomScrollTable.EVENT_SCROLL_TO_LEFT = "CustomScrollTable.EVENT_SCROLL_TO_LEFT";
BI.CustomScrollTable.EVENT_SCROLL_TO_TOP = "CustomScrollTable.EVENT_SCROLL_TO_TOP";
BI.CustomScrollTable.EVENT_SCROLL_TO_BOTTOM = "CustomScrollTable.EVENT_SCROLL_TO_BOTTOM";
$.shortcut('bi.custom_scroll_table', BI.CustomScrollTable);