/**
 * GridTable
 *
 * Created by GUY on 2016/1/12.
 * @class BI.GridTable
 * @extends BI.Widget
 */
BI.GridTable = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.GridTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-grid-table",
            headerRowHeight: 25,
            rowHeight: 25,
            columnSize: [],
            isNeedFreeze: true,
            freezeCols: [],
            header: [],
            items: [],
            regionColumnSize: []
        });
    },

    _init: function () {
        BI.GridTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.scrollTop = 0;
        this.leftScrollLeft = 0;
        this.rightScrollLeft = 0;
        this._width = 0;
        this._height = 0;
        this._scrollBarSize = BI.DOM.getScrollWidth();
        var rowHeightGetter = function () {
            return o.rowHeight;
        };
        var columnLeftWidthGetter = function (index) {
            return o.columnSize[index];
        };
        var columnRightWidthGetter = function (index) {
            return o.columnSize[index + self._getFreezeColLength()];
        };
        this.topLeftGrid = BI.createWidget({
            type: "bi.grid_view",
            rowHeightGetter: rowHeightGetter,
            columnWidthGetter: columnLeftWidthGetter,
        });
        this.topLeftGrid.on(BI.Grid.EVENT_SCROLL, function (scroll) {
            self.bottomLeftGrid.setScrollLeft(scroll.scrollLeft);
        });
        this.topRightGrid = BI.createWidget({
            type: "bi.grid_view",
            rowHeightGetter: rowHeightGetter,
            columnWidthGetter: columnRightWidthGetter,
        });
        this.topRightGrid.on(BI.Grid.EVENT_SCROLL, function (scroll) {
            self.bottomRightGrid.setScrollLeft(scroll.scrollLeft);
        });
        this.bottomLeftGrid = BI.createWidget({
            type: "bi.grid_view",
            rowHeightGetter: rowHeightGetter,
            columnWidthGetter: columnLeftWidthGetter,
        });
        this.bottomLeftGrid.on(BI.Grid.EVENT_SCROLL, function (scroll) {
            self.bottomRightGrid.setScrollTop(scroll.scrollTop);
            self.topLeftGrid.setScrollLeft(scroll.scrollLeft);
            self._populateScrollbar();
        });
        this.bottomRightGrid = BI.createWidget({
            type: "bi.grid_view",
            rowHeightGetter: rowHeightGetter,
            columnWidthGetter: columnRightWidthGetter,
        });
        this.bottomRightGrid.on(BI.Grid.EVENT_SCROLL, function (scroll) {
            self.bottomLeftGrid.setScrollTop(scroll.scrollTop);
            self.topRightGrid.setScrollLeft(scroll.scrollLeft);
            self._populateScrollbar();
        });
        this.topLeft = BI.createWidget({
            type: "bi.vertical",
            scrollable: false,
            scrolly: false,
            items: [this.topLeftGrid]
        });
        this.topRight = BI.createWidget({
            type: "bi.vertical",
            scrollable: false,
            scrolly: false,
            items: [this.topRightGrid]
        });
        this.bottomLeft = BI.createWidget({
            type: "bi.vertical",
            scrollable: false,
            scrolly: false,
            items: [this.bottomLeftGrid]
        });
        this.bottomRight = BI.createWidget({
            type: "bi.vertical",
            scrollable: false,
            scrolly: false,
            items: [this.bottomRightGrid]
        });
        this.contextLayout = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.topLeft,
                top: 0,
                left: 0
            }, {
                el: this.topRight,
                top: 0
            }, {
                el: this.bottomLeft,
                left: 0
            }, {
                el: this.bottomRight
            }]
        });

        this.topScrollbar = BI.createWidget({
            type: "bi.grid_table_scrollbar",
            width: BI.GridTableScrollbar.SIZE
        });
        this.topScrollbar.on(BI.GridTableScrollbar.EVENT_SCROLL, function (scrollTop) {
            self.bottomLeftGrid.setScrollTop(scrollTop);
            self.bottomRightGrid.setScrollTop(scrollTop);
        });
        this.leftScrollbar = BI.createWidget({
            type: "bi.grid_table_horizontal_scrollbar",
            height: BI.GridTableScrollbar.SIZE
        });
        this.leftScrollbar.on(BI.GridTableHorizontalScrollbar.EVENT_SCROLL, function (scrollLeft) {
            self.topLeftGrid.setScrollLeft(scrollLeft);
            self.bottomLeftGrid.setScrollLeft(scrollLeft);
        });
        this.rightScrollbar = BI.createWidget({
            type: "bi.grid_table_horizontal_scrollbar",
            height: BI.GridTableScrollbar.SIZE
        });
        this.rightScrollbar.on(BI.GridTableHorizontalScrollbar.EVENT_SCROLL, function (scrollLeft) {
            self.topRightGrid.setScrollLeft(scrollLeft);
            self.bottomRightGrid.setScrollLeft(scrollLeft);
        });
        this.scrollBarLayout = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.topScrollbar,
                right: 0,
                top: 0
            }, {
                el: this.leftScrollbar,
                left: 0
            }, {
                el: this.rightScrollbar,
            }]
        });
        this._width = o.width - BI.GridTableScrollbar.SIZE;
        this._height = o.height - BI.GridTableScrollbar.SIZE;
        this._populate();
    },

    _getFreezeColLength: function () {
        return this.options.isNeedFreeze ? this.options.freezeCols.length : 0;
    },

    _populateScrollbar: function () {
        var o = this.options;
        var regionSize = this.getRegionSize(), totalLeftColumnSize = 0, totalRightColumnSize = 0, totalColumnSize = 0, summaryColumnSizeArray = [], totalRowSize = o.items.length * o.rowHeight;
        var freezeColLength = this._getFreezeColLength();
        BI.each(o.columnSize, function (i, size) {
            if (o.isNeedFreeze === true && o.freezeCols.contains(i)) {
                totalLeftColumnSize += size;
            } else {
                totalRightColumnSize += size;
            }
            totalColumnSize += size;
            if (i === 0) {
                summaryColumnSizeArray[i] = size;
            } else {
                summaryColumnSizeArray[i] = summaryColumnSizeArray[i - 1] + size;
            }
        });
        this.topScrollbar.setContentSize(o.items.length * o.rowHeight);
        this.topScrollbar.setSize(this._height - (o.isNeedFreeze === true ? o.header.length * o.headerRowHeight : 0));
        this.topScrollbar.setPosition(Math.min(this.bottomLeftGrid.getScrollTop(), this.bottomRightGrid.getScrollTop()));
        this.topScrollbar.populate();

        this.leftScrollbar.setContentSize(totalLeftColumnSize);
        this.leftScrollbar.setSize(regionSize);
        this.leftScrollbar.setPosition(this.bottomLeftGrid.getScrollLeft());
        this.leftScrollbar.populate();

        this.rightScrollbar.setContentSize(totalRightColumnSize);
        this.rightScrollbar.setSize(this._width - regionSize);
        this.rightScrollbar.setPosition(this.bottomRightGrid.getScrollLeft());
        this.rightScrollbar.populate();

        var items = this.scrollBarLayout.attr("items");
        items[0].top = o.header.length * o.headerRowHeight;
        items[1].top = this._height;
        items[2].top = this._height;
        items[2].left = regionSize;
        this.scrollBarLayout.attr("items", items);
        this.scrollBarLayout.resize();
    },

    _populateTable: function () {
        var self = this, o = this.options;
        var regionSize = this.getRegionSize(), totalLeftColumnSize = 0, totalRightColumnSize = 0, totalColumnSize = 0, summaryColumnSizeArray = [], totalRowSize = o.items.length * o.rowHeight;
        var freezeColLength = this._getFreezeColLength();
        BI.each(o.columnSize, function (i, size) {
            if (o.isNeedFreeze === true && o.freezeCols.contains(i)) {
                totalLeftColumnSize += size;
            } else {
                totalRightColumnSize += size;
            }
            totalColumnSize += size;
            if (i === 0) {
                summaryColumnSizeArray[i] = size;
            } else {
                summaryColumnSizeArray[i] = summaryColumnSizeArray[i - 1] + size;
            }
        });
        var tlw = regionSize;
        var tlh = regionSize >= summaryColumnSizeArray[o.freezeCols.length - 1] ? (o.header.length * o.headerRowHeight) : (o.header.length * o.headerRowHeight + this._scrollBarSize);
        var trw = this._width - regionSize;
        var trh = (this._width - regionSize >= totalColumnSize - (summaryColumnSizeArray[o.freezeCols.length - 1] || 0)) ? (o.header.length * o.headerRowHeight) : (o.header.length * o.headerRowHeight + this._scrollBarSize);
        var blw = (this._height - o.header.length * o.headerRowHeight >= totalRowSize) ? regionSize : (regionSize + this._scrollBarSize);
        var blh = (regionSize >= (summaryColumnSizeArray[o.freezeCols.length - 1] || 0)) ? (this._height - o.header.length * o.headerRowHeight) : (this._height - o.header.length * o.headerRowHeight + this._scrollBarSize);
        var brw = (this._height - o.header.length * o.headerRowHeight >= totalRowSize) ? (this._width - regionSize) : (this._width - regionSize + this._scrollBarSize);
        var brh = (this._width - regionSize >= totalColumnSize - (summaryColumnSizeArray[this._getFreezeColLength() - 1] || 0)) ? (this._height - o.header.length * o.headerRowHeight) : (this._height - o.header.length * o.headerRowHeight + this._scrollBarSize);

        this.topLeft.setWidth(regionSize);
        this.topLeft.setHeight(o.header.length * o.headerRowHeight);
        this.topRight.setWidth(this._width - regionSize);
        this.topRight.setHeight(o.header.length * o.headerRowHeight);
        this.bottomLeft.setWidth(regionSize);
        this.bottomLeft.setHeight(this._height - o.header.length * o.headerRowHeight);
        this.bottomRight.setWidth(this._width - regionSize);
        this.bottomRight.setHeight(this._height - o.header.length * o.headerRowHeight);

        this.topLeftGrid.setWidth(tlw);
        this.topLeftGrid.setHeight(tlh);
        this.topRightGrid.setWidth(trw);
        this.topRightGrid.setHeight(trh);
        this.bottomLeftGrid.setWidth(blw);
        this.bottomLeftGrid.setHeight(blh);
        this.bottomRightGrid.setWidth(brw);
        this.bottomRightGrid.setHeight(brh);

        this.topLeftGrid.setEstimatedColumnSize(freezeColLength > 0 ? totalLeftColumnSize / freezeColLength : 0);
        this.topLeftGrid.setEstimatedRowSize(o.headerRowHeight);
        this.topRightGrid.setEstimatedColumnSize(totalRightColumnSize / (o.columnSize.length - freezeColLength));
        this.topRightGrid.setEstimatedRowSize(o.headerRowHeight);
        this.bottomLeftGrid.setEstimatedColumnSize(freezeColLength > 0 ? totalLeftColumnSize / freezeColLength : 0);
        this.bottomLeftGrid.setEstimatedRowSize(o.rowHeight);
        this.bottomRightGrid.setEstimatedColumnSize(totalRightColumnSize / (o.columnSize.length - freezeColLength));
        this.bottomRightGrid.setEstimatedRowSize(o.rowHeight);

        var items = this.contextLayout.attr("items");
        items[1].left = regionSize;
        items[2].top = o.header.length * o.headerRowHeight;
        items[3].left = regionSize;
        items[3].top = o.header.length * o.headerRowHeight;
        this.contextLayout.attr("items", items);
        this.contextLayout.resize();

        var leftHeader = [], rightHeader = [], leftItems = [], rightItems = [];
        BI.each(o.header, function (i, cols) {
            leftHeader[i] = [];
            rightHeader[i] = [];
            BI.each(cols, function (j, col) {
                var cell = {
                    type: "bi.grid_table_cell",
                    el: col
                };
                if (j < freezeColLength) {
                    leftHeader[i].push(cell);
                } else {
                    rightHeader[i].push(cell);
                }
            });
        });
        BI.each(o.items, function (i, cols) {
            leftItems[i] = [];
            rightItems[i] = [];
            BI.each(cols, function (j, col) {
                var cell = {
                    type: "bi.grid_table_cell",
                    el: col
                };
                if (j < freezeColLength) {
                    leftItems[i].push(cell);
                } else {
                    rightItems[i].push(cell);
                }
            });
        });
        this.topLeftGrid.populate(leftHeader);
        this.topRightGrid.populate(rightHeader);
        this.bottomLeftGrid.populate(leftItems);
        this.bottomRightGrid.populate(rightItems);
    },

    _populate: function () {
        if (this._width <= 0 || this._height <= 0) {
            return;
        }
        this._populateTable();
        this._populateScrollbar();
    },

    getRegionSize: function () {
        var o = this.options;
        var regionSize = o.regionColumnSize[0] || 0;
        if (o.isNeedFreeze === false || o.freezeCols.length === 0) {
            return 0;
        }
        if (!regionSize) {
            BI.each(o.freezeCols, function (i, col) {
                regionSize += o.columnSize[col];
            });
        }
        return regionSize;
    },

    getScrollTop: function () {
        return this.bottomRightGrid.getScrollTop();
    },

    getLeftScrollLeft: function () {
        return this.bottomLeftGrid.getScrollLeft();
    },

    getRightScrollLeft: function () {
        return this.bottomRightGrid.getScrollLeft();
    },


    setWidth: function (width) {
        BI.GridTable.superclass.setWidth.apply(this, arguments);
        this._width = this.options.width - BI.GridTableScrollbar.SIZE;
    },

    setHeight: function (height) {
        BI.GridTable.superclass.setHeight.apply(this, arguments);
        this._height = this.options.height - BI.GridTableScrollbar.SIZE;
    },

    setColumnSize: function (columnSize) {
        this.options.columnSize = columnSize;
    },

    setRegionColumnSize: function (regionColumnSize) {
        this.options.regionColumnSize = regionColumnSize;
    },

    populate: function (items, header) {
        if (items) {
            this.options.items = items;
        }
        if (header) {
            this.options.header = header;
        }
        this._populate();
    },

    clear: function () {
        this.topLeftGrid.clear();
        this.topRightGrid.clear();
        this.bottomLeftGrid.clear();
        this.bottomRightGrid.clear();
    }
});
BI.GridTable.EVENT_VALUE_CHANGE = "EVENT_VALUE_CHANGE";
$.shortcut('bi.grid_table', BI.GridTable);