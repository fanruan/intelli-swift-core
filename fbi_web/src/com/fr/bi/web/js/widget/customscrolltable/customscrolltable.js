/**
 * 自定义滚动条的表格
 *
 * Created by GUY on 2016/2/15.
 * @class BI.CustomScrollTable
 * @extends BI.Widget
 */
BI.CustomScrollTable = BI.inherit(BI.Widget, {

    _const: {
        minScrollWidth: 150
    },

    _defaultConfig: function () {
        return BI.extend(BI.CustomScrollTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-scroll-table",
            el: {
                type: "bi.adaptive_table"
            },

            hideVerticalScrollChecker: function () {
                return true;
            },

            hideHorizontalScrollChecker: function () {
                return true;
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

        this._adjustScrollBar = BI.debounce(function () {
            if (o.isNeedFreeze === true || o.isNeedFreeze === false) {
                self._assertScroll();
                var isNeedVResize = false, isNeedHResize = false;
                var resizeH = function resizeH() {
                    if (self.hasLeftHorizontalScroll() || self.hasRightHorizontalScroll() || !o.hideHorizontalScrollChecker()) {
                        self.bottomLeftScrollBar && self.bottomLeftScrollBar.setHidden(false);
                        self.bottomRightScrollBar.setHidden(false);
                        var items = self.layout.attr("items");
                        isNeedHResize = isNeedHResize || items[0].bottom !== o.scrollWidth;
                        if (items[0].bottom !== o.scrollWidth || items[1].bottom !== o.scrollWidth) {
                            items[0].bottom = o.scrollWidth;
                            items[1].bottom = o.scrollWidth;
                            self.layout.attr("items", items);
                            self.layout.resize();
                        }
                        return items[0].bottom !== o.scrollWidth;
                    } else {
                        self.bottomLeftScrollBar && self.bottomLeftScrollBar.setHidden(true);
                        self.bottomRightScrollBar.setHidden(true);
                        var items = self.layout.attr("items");
                        isNeedHResize = isNeedHResize || items[0].bottom !== 0;
                        if (items[0].bottom !== 0 || items[1].bottom !== 0) {
                            items[0].bottom = 0;
                            items[1].bottom = 0;
                            self.layout.attr("items", items);
                            self.layout.resize();
                        }
                        return items[0].bottom !== 0;
                    }
                };

                var resizeV = function resizeV() {
                    if (self.hasVerticalScroll() || !o.hideVerticalScrollChecker()) {
                        self.topScrollBar.setHidden(false);
                        var items = self.layout.attr("items");
                        isNeedVResize = isNeedVResize || items[0].right !== o.scrollWidth;
                        if (items[0].right !== o.scrollWidth) {
                            items[0].right = o.scrollWidth;
                            self.layout.attr("items", items);
                            self.layout.resize();
                        }
                        return items[0].right !== o.scrollWidth;
                    } else {
                        self.topScrollBar.setHidden(true);
                        var items = self.layout.attr("items");
                        isNeedVResize = isNeedVResize || items[0].right !== 0;
                        if (items[0].right !== 0) {
                            items[0].right = 0;
                            self.layout.attr("items", items);
                            self.layout.resize();
                        }
                        return items[0].right !== 0;
                    }
                };

                resizeV();
                if (isNeedVResize) {
                    self.table.resize();
                }
                resizeH();

                if (isNeedHResize) {
                    self.table.resize();
                    if (resizeV()) {
                        self.table.resize();
                    }
                }

                if (isNeedHResize || isNeedVResize) {
                    self.table.resize();
                    self._resizeFreezeScroll();
                    self._scrollFreezeScroll();
                    self._resizeScroll();
                    self._scrollScroll();
                    self.table.resize();
                }
            }
        }, 0);

        this.layout = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.table,
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            }]
        });
        this._assertScroll();
        if (o.isNeedFreeze === true || o.isNeedFreeze === false) {
            BI.nextTick(function () {
                self._resizeFreezeScroll();
                self._scrollFreezeScroll();
                self._resizeScroll();
                self._scrollScroll();
                self._adjustScrollBar();
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
            self._resizeFreezeScroll();
            self._scrollFreezeScroll();
            self._resizeScroll();
            self._adjustScrollBar();
            self.fireEvent(BI.Table.EVENT_TABLE_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_SCROLL, function () {
            self._distributeTasks();
            self.fireEvent(BI.Table.EVENT_TABLE_SCROLL, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            self._resizeFreezeScroll();
            self._resizeScroll();
            self._adjustScrollBar();
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
            self._resizeFreezeScroll();
            self._resizeScroll();
            self._adjustScrollBar();
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE);
        });

        //important: 当region区域拖拽过程中有可能会发生横向滚动条翻页事件
        this.table.on(BI.Table.EVENT_TABLE_REGION_RESIZE, function () {
            //self.bottomRightScrollBar.setScrollWidth(0);
        });
    },

    _assertScroll: function () {
        var self = this, o = this.options;
        if (o.isNeedFreeze === true || o.isNeedFreeze === false) {
            if (!this.topScrollBar) {
                this.topScrollBar = BI.createWidget({
                    type: "bi.custom_scroll_table_scroll_bar",
                    width: o.scrollWidth,
                    axis: BI.Axis.Vertical
                });
                this.topScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL, function () {
                    self._distributeTasks(true);
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
                this.layout.addItem({
                    el: this.topScrollBar,
                    right: 0,
                    bottom: o.scrollWidth,
                    top: 0
                })
            }
            if (!this.bottomRightScrollBar) {
                this.bottomRightScrollBar = BI.createWidget({
                    type: "bi.custom_scroll_table_scroll_bar",
                    height: o.scrollWidth,
                    axis: BI.Axis.Horizontal
                });
                this.bottomRightScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL, function () {
                    self._distributeTasks(true);
                });
                this.bottomRightScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL_BEHIND, function () {
                    self.fireEvent(BI.CustomScrollTable.EVENT_SCROLL_TO_RIGHT);
                });
                this.bottomRightScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL, function () {
                    self.fireEvent(BI.CustomScrollTable.EVENT_RIGHT_SCROLL);
                });
                this.bottomRightScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL_FRONT, function () {
                    self.fireEvent(BI.CustomScrollTable.EVENT_SCROLL_TO_LEFT);
                });
                this.layout.addItem({
                    el: this.bottomRightScrollBar,
                    right: o.pageSpace || 0,
                    bottom: 0
                })
            }
        }
        if (o.isNeedFreeze === true) {
            if (!this.bottomLeftScrollBar) {
                this.bottomLeftScrollBar = BI.createWidget({
                    type: "bi.custom_scroll_table_scroll_bar",
                    height: o.scrollWidth,
                    axis: BI.Axis.Horizontal
                });
                this.bottomLeftScrollBar.on(BI.CustomScrollTableScrollBar.EVENT_SCROLL, function () {
                    self._distributeTasks(true);
                    self.fireEvent(BI.CustomScrollTable.EVENT_LEFT_SCROLL);
                });
                var items = this.layout.attr("items");
                items.splice(3);
                this.layout.attr("items", items);
                this.layout.addItem({
                    el: this.bottomLeftScrollBar,
                    left: 0,
                    bottom: 0
                })
            }
        } else if (o.isNeedFreeze === false) {
            if (this.bottomLeftScrollBar) {
                this.bottomLeftScrollBar.destroy();
                this.bottomLeftScrollBar = null;
                var items = this.layout.attr("items");
                items.splice(3);
                this.layout.attr("items", items);
            }
        }
    },

    _distributeTasks: function (isScroll) {
        var o = this.options;
        if (o.isNeedFreeze === true || o.isNeedFreeze === false) {
            if (this._release === true) {
                this._scrolling = !!isScroll;
            }
            if (!this._release && BI.isNotNull(this._scrolling) && !!isScroll !== this._scrolling) {
                this._lock();
                return;
            }
            this._release = false;
            this._lock();
            //如果是滚动条触发的scroll
            if (isScroll === true) {
                this._scrollFreezeRegion();
                this._scrollRegion();
            } else {
                this._scrollFreezeScroll();
                this._scrollScroll();
            }
        }
    },

    _resizeFreezeScroll: function () {
        var o = this.options;
        if (this.options.isNeedFreeze === true) {
            this._assertScroll();
            var regionSize = this.table.getCalculateRegionColumnSize();
            this.bottomLeftScrollBar.element.width(regionSize[0]);
            this.bottomRightScrollBar.element.css("left", regionSize[0] + "px");

            if (regionSize[1] <= this._const.minScrollWidth) {
                var r = 0;
                if (o.pageSpace > 1.05) {
                    r = o.scrollWidth;
                }
                this.bottomRightScrollBar.element.css("right", r + "px");
            } else {
                this.bottomRightScrollBar.element.css("right", (o.pageSpace || 0) + "px");
            }

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
            this._assertScroll();
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
            this._assertScroll();
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
            this.fireEvent(BI.Table.EVENT_TABLE_SCROLL, scrollTop / ratio);
        }
    },

    _resizeScroll: function () {
        var o = this.options;
        if (this.options.isNeedFreeze === false) {
            this._assertScroll();
            this.bottomRightScrollBar.element.css("left", "0px");
            var regionSize = this.table.getCalculateRegionColumnSize();

            if (regionSize[0] <= this._const.minScrollWidth) {
                var r = 0;
                if (o.pageSpace > 1.05) {
                    r = o.scrollWidth;
                }
                this.bottomRightScrollBar.element.css("right", r + "px");
            } else {
                this.bottomRightScrollBar.element.css("right", (o.pageSpace || 0) + "px");
            }

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
            this._assertScroll();
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
            this._assertScroll();
            var regionSize = this.table.getCalculateRegionColumnSize();
            var ratio = this.bottomRightScrollBar.element.width() / regionSize[0];

            var scrollLeft = this.bottomRightScrollBar.getScrollLeft();
            this.table.setLeftHorizontalScroll(scrollLeft / ratio);

            regionSize = this.table.getCalculateRegionRowSize();
            ratio = this.topScrollBar.element.height() / regionSize[0];

            var scrollTop = this.topScrollBar.getScrollTop();
            this.table.setVerticalScroll(scrollTop / ratio);
            this.fireEvent(BI.Table.EVENT_TABLE_SCROLL, scrollTop / ratio);
        }
    },

    _resize: function () {
        var self = this, o = this.options;
        BI.nextTick(function () {
            self._resizeFreezeScroll();
            self._scrollFreezeScroll();
            self._resizeScroll();
            self._scrollScroll();
            self._adjustScrollBar();
        });
    },


    resize: function () {
        var self = this, o = this.options;
        this.table.resize();
        this._resize();
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
        var clients = this.table.getCalculateRegionRowSize();
        var scrolls = this.table.getScrollRegionRowSize();
        return BI.last(scrolls) > BI.last(clients);
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
        return this.topScrollBar && this.topScrollBar.getScrollTop();
        //return this.table.getVerticalScroll();
    },

    hasLeftHorizontalScroll: function () {
        var clients = this.table.getCalculateRegionColumnSize();
        var scrolls = this.table.getScrollRegionColumnSize();
        return scrolls[0] > clients[0];
    },

    hasRightHorizontalScroll: function () {
        var clients = this.table.getCalculateRegionColumnSize();
        var scrolls = this.table.getScrollRegionColumnSize();
        return scrolls[1] > clients[1];
    },

    getLeftHorizontalScroll: function () {
        return this.bottomLeftScrollBar && this.bottomLeftScrollBar.getScrollLeft();
        //return this.table.getLeftHorizontalScroll();
    },

    getRightHorizontalScroll: function () {
        return this.bottomRightScrollBar && this.bottomRightScrollBar.getScrollLeft();
        //return this.table.getRightHorizontalScroll();
    },

    getColumns: function () {
        return this.table.getColumns();
    },

    attr: function () {
        BI.CustomScrollTable.superclass.attr.apply(this, arguments);
        this.table.attr.apply(this.table, arguments);
    },

    populate: function (items) {
        this.table.populate.apply(this.table, arguments);
        this._resize();
    },

    scrollToRight: function () {
        this.bottomRightScrollBar && this.bottomRightScrollBar.scrollToRight();
    },

    scrollToLeft: function () {
        this.bottomRightScrollBar && this.bottomRightScrollBar.scrollToLeft();
    },

    scrollToTop: function () {
        this.topScrollBar && this.topScrollBar.scrollToTop();
    },

    scrollToBottom: function () {
        this.topScrollBar && this.topScrollBar.scrollToBottom();
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