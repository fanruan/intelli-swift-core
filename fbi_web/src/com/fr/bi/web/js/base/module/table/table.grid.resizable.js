/**
 *
 * 可调整列宽的grid表格
 *
 * Created by GUY on 2016/1/12.
 * @class BI.ResizableGridTable
 * @extends BI.Widget
 */
BI.ResizableGridTable = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ResizableGridTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-resizable-grid-table",
            headerRowHeight: 25,
            rowHeight: 25,
            columnSize: [],
            isNeedFreeze: true,
            freezeCols: [],
            header: [],
            items: [],
            regionColumnSize: []
        })
    },

    _init: function () {
        BI.ResizableGridTable.superclass._init.apply(this, arguments);
        var o = this.options;
        this.resizer = BI.createWidget({
            type: "bi.layout",
            invisible: true,
            cls: "bi-resizer"
        });
        this.regionResizerHandler = this._createResizerHandler();
        this.table = BI.createWidget({
            type: "bi.grid_table",
            element: this.element,
            width: o.width,
            height: o.height,
            headerRowHeight: o.headerRowHeight,
            rowHeight: o.rowHeight,
            columnSize: o.columnSize,
            isNeedFreeze: o.isNeedFreeze,
            freezeCols: o.freezeCols,
            header: this._formatHeader(o.header),
            items: o.items,
            regionColumnSize: o.regionColumnSize
        });
        this.resizeLayout = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.regionResizerHandler,
                left: 0,
                top: 0,
                bottom: 0
            }, {
                el: this.resizer,
                left: 0,
                top: 0
            }]
        });
        this._populate();
    },

    _createResizerHandler: function () {
        var self = this, o = this.options;
        var regionResizerHandler = BI.createWidget({
            type: "bi.absolute",
            cls: "resizable-grid-table-region-resizer",
            invisible: true,
            width: 6,
            items: [{
                el: {
                    type: "bi.layout",
                    width: 2,
                    cls: "resizable-grid-table-region-resizer-knob"
                },
                left: 2,
                top: 0,
                bottom: 0
            }]
        });
        var size = 0, offset = 0, defaultSize = 0;
        var mouseMoveTracker = new BI.MouseMoveTracker(function (deltaX, deltaY) {
            offset += deltaX;
            size = BI.clamp(defaultSize + offset, 15, o.width - 15);

            self.resizer.setVisible(true);
            self.resizer.setWidth(size);
            self.resizer.setHeight(o.height);

            var items = self.resizeLayout.attr("items");
            items[0].left = size - 3;
            self.resizeLayout.attr("items", items);
            self.resizeLayout.resize();
        }, function () {
            o.regionColumnSize[0] = BI.clamp(size, 15, o.width - 15);
            self.table.setRegionColumnSize(o.regionColumnSize);
            // self.table.clear();
            self.table.populate();
            self._populate();
            self.resizer.setVisible(false);
            mouseMoveTracker.releaseMouseMoves();
        }, $("body")[0]);
        regionResizerHandler.element.on("mousedown", function (event) {
            defaultSize = size = self.table.getRegionSize();
            offset = 0;
            var items = self.resizeLayout.attr("items");
            items[1].left = 0;
            items[1].top = 0;
            self.resizeLayout.attr("items", items);
            self.resizeLayout.resize();
            mouseMoveTracker.captureMouseMoves(event)
        });
        return regionResizerHandler;
    },

    _getRegionRowSize: function () {
        var o = this.options;
        return [o.header.length * o.headerRowHeight,
            Math.min(o.height - o.header.length * o.headerRowHeight - this.table.getScrollTop(), o.items.length * o.rowHeight)];
    },

    _getFreezeColLength: function () {
        return this.options.isNeedFreeze ? this.options.freezeCols.length : 0;
    },

    _getResizerLeft: function (j) {
        var left = 0;
        var columnSize = this.options.columnSize;
        var freezeColLength = this._getFreezeColLength();
        for (var i = (j >= freezeColLength ? freezeColLength : 0); i < j; i++) {
            left += columnSize[i] || 0;
        }
        if (j >= freezeColLength) {
            left += this.table.getRegionSize();
            left -= this.table.getRightScrollLeft();
        } else {
            left -= this.table.getLeftScrollLeft();
        }
        return left;
    },

    _formatHeader: function (header) {
        var self = this, o = this.options;
        var result = [];
        var start = function (j, event, ui) {
            self.resizer.setVisible(true);
            resize(j, event, ui);
        };
        var resize = function (j, event, ui) {
            var height = ui.size.height + self._getRegionRowSize()[1];
            self.resizer.setWidth(ui.size.width);
            self.resizer.setHeight(height);

            var items = self.resizeLayout.attr("items");
            items[1].left = self._getResizerLeft(j);
            items[1].top = (o.header.length - 1) * o.headerRowHeight;
            self.resizeLayout.attr("items", items);
            self.resizeLayout.resize();
        };
        var stop = function (j, event, ui) {
            self.resizer.setVisible(false);
            o.columnSize[j] = ui.size.width;
            self.table.setColumnSize(o.columnSize);
            self.table.clear();
            self.table.populate();
        };
        BI.each(header, function (i, cols) {
            if (i === header.length - 1) {
                result[i] = [];
                BI.each(cols, function (j, col) {
                    if (j === self._getFreezeColLength() - 1 || j === cols.length - 1) {
                        result[i][j] = col;
                    } else {
                        result[i][j] = {
                            type: "bi.resizable_grid_table_cell",
                            el: col,
                            start: BI.bind(start, null, j),
                            resize: BI.bind(resize, null, j),
                            stop: BI.bind(stop, null, j)
                        }
                    }
                });
            } else {
                result.push(cols);
            }
        });
        return result;
    },

    _populate: function () {
        var o = this.options;
        var regionSize = this.table.getRegionSize();
        if (regionSize > 0) {
            this.regionResizerHandler.setVisible(true);
            var items = this.resizeLayout.attr("items");
            items[0].left = regionSize - 3;
            this.resizeLayout.attr("items", items);
            this.resizeLayout.resize();
        } else {
            this.regionResizerHandler.setVisible(false);
        }
    },

    setWidth: function (width) {
        BI.ResizableGridTable.superclass.setWidth.apply(this, arguments);
        this.table.setWidth(width)
    },

    setHeight: function (height) {
        BI.ResizableGridTable.superclass.setHeight.apply(this, arguments);
        this.table.setHeight(height);
    },

    clear: function () {
        this.table.clear();
    },

    populate: function () {
        this.table.populate();
    }
});

$.shortcut("bi.resizable_grid_table", BI.ResizableGridTable);