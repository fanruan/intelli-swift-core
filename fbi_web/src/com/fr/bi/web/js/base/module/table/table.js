/**
 *
 * 表格
 *
 * 能处理静态宽度以及动态宽度的表， 百分比宽度的表请使用PreviewTable
 *
 * Created by GUY on 2015/9/22.
 * @class BI.Table
 * @extends BI.Widget
 */
BI.Table = BI.inherit(BI.Widget, {

    _const: {
        delta: 120
    },

    _defaultConfig: function () {
        return BI.extend(BI.Table.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table",
            logic: { //冻结的页面布局逻辑
                dynamic: false
            },

            isNeedResize: false,//是否需要调整列宽
            isResizeAdapt: true,//是否需要在调整列宽或区域宽度的时候它们自适应变化

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
            items: [] //二维数组
        })
    },

    _calculateWidth: function (width) {
        if (!width || width === "0") {
            return "";
        }
        width = BI.parseFloat(width);
        return width > 1.01 ? width : (width * 100 + "%");
    },

    _calculateHeight: function (height) {
        return height ? height : "";
    },

    _isRightFreeze: function () {
        return BI.isNotEmptyArray(this.options.freezeCols) && BI.first(this.options.freezeCols) !== 0;
    },

    _createTopLeft: function () {
        var o = this.options, isRight = this._isRightFreeze();
        this.topLeftColGroupTds = {};
        this.topLeftBodyTds = {};
        this.topLeftBodyItems = {};
        var table = this._table();
        var colgroup = this._createColGroup(this.columnLeft, this.topLeftColGroupTds);
        var body = this._body();
        body.element.append(this._createHeaderCells(this.topLeftItems, this.columnLeft, this.mergeLeft, this.topLeftBodyTds, this.topLeftBodyItems));
        BI.createWidget({
            type: "bi.adaptive",
            element: table,
            items: [colgroup, body]
        });
        if (isRight) {
            var w = 0;
            BI.each(o.columnSize, function (i, col) {
                if (!o.freezeCols.contains(i)) {
                    w += col;
                }
            });
            if (BI.isNumeric(w) && w > 1) {
                w = BI.parseFloat(w) + o.columnSize.length - o.freezeCols.length;
            }
        }
        return (this.topLeftContainer = BI.createWidget({
            type: "bi.adaptive",
            width: this._calculateWidth(w),
            items: [table]
        }));
    },

    _createTopRight: function () {
        var o = this.options, isRight = this._isRightFreeze();
        this.topRightColGroupTds = {};
        this.topRightBodyTds = {};
        this.topRightBodyItems = {};
        var table = this._table();
        var colgroup = this._createColGroup(this.columnRight, this.topRightColGroupTds);
        var body = this._body();
        body.element.append(this._createHeaderCells(this.topRightItems, this.columnRight, this.mergeRight, this.topRightBodyTds, this.topRightBodyItems, this.columnLeft.length));
        BI.createWidget({
            type: "bi.adaptive",
            element: table,
            items: [colgroup, body]
        });
        if (!isRight) {
            var w = 0;
            BI.each(o.columnSize, function (i, col) {
                if (!o.freezeCols.contains(i)) {
                    w += col;
                }
            });
            if (BI.isNumeric(w)) {
                w = BI.parseFloat(w) + o.columnSize.length - o.freezeCols.length;
            }
        }
        return (this.topRightContainer = BI.createWidget({
            type: "bi.adaptive",
            width: w || undefined,
            items: [table]
        }));
    },

    _createBottomLeft: function () {
        var o = this.options, isRight = this._isRightFreeze();
        this.bottomLeftColGroupTds = {};
        this.bottomLeftBodyTds = {};
        this.bottomLeftBodyItems = {};
        var table = this._table();
        var colgroup = this._createColGroup(this.columnLeft, this.bottomLeftColGroupTds);
        var body = this._body();
        body.element.append(this._createCells(this.bottomLeftItems, this.columnLeft, this.mergeLeft, this.bottomLeftBodyTds, this.bottomLeftBodyItems));
        BI.createWidget({
            type: "bi.adaptive",
            element: table,
            items: [colgroup, body]
        });
        if (isRight) {
            var w = 0;
            BI.each(o.columnSize, function (i, col) {
                if (!o.freezeCols.contains(i)) {
                    w += col;
                }
            });
            if (BI.isNumeric(w) && w > 1) {
                w = BI.parseFloat(w) + o.columnSize.length - o.freezeCols.length;
            }
        }
        return (this.bottomLeftContainer = BI.createWidget({
            type: "bi.adaptive",
            width: this._calculateWidth(w),
            items: [table]
        }));
    },

    _createBottomRight: function () {
        var o = this.options, isRight = this._isRightFreeze();
        this.bottomRightColGroupTds = {};
        this.bottomRightBodyTds = {};
        this.bottomRightBodyItems = {};
        var table = this._table();
        var colgroup = this._createColGroup(this.columnRight, this.bottomRightColGroupTds);
        var body = this._body();
        body.element.append(this._createCells(this.bottomRightItems, this.columnRight, this.mergeRight, this.bottomRightBodyTds, this.bottomRightBodyItems, this.columnLeft.length));
        BI.createWidget({
            type: "bi.adaptive",
            element: table,
            items: [colgroup, body]
        });
        if (!isRight) {
            var w = 0;
            BI.each(o.columnSize, function (i, col) {
                if (!o.freezeCols.contains(i)) {
                    w += col;
                }
            });
            if (BI.isNumeric(w) && w > 1) {
                w = BI.parseFloat(w) + o.columnSize.length - o.freezeCols.length;
            }
        }
        return (this.bottomRightContainer = BI.createWidget({
            type: "bi.adaptive",
            width: this._calculateWidth(w),
            items: [table]
        }));
    },

    _createFreezeTable: function () {
        var self = this, o = this.options;
        var isRight = this._isRightFreeze();
        var split = this._split(o.header);
        this.topLeftItems = split.left;
        this.topRightItems = split.right;
        split = this._split(o.items);
        this.bottomLeftItems = split.left;
        this.bottomRightItems = split.right;

        this.columnLeft = [];
        this.columnRight = [];
        BI.each(o.columnSize, function (i, size) {
            if (o.freezeCols.contains(i)) {
                self[isRight ? "columnRight" : "columnLeft"].push(size);
            } else {
                self[isRight ? "columnLeft" : "columnRight"].push(size);
            }
        });
        this.mergeLeft = [];
        this.mergeRight = [];
        BI.each(o.mergeCols, function (i, col) {
            if (o.freezeCols.contains(col)) {
                self[isRight ? "mergeRight" : "mergeLeft"].push(col);
            } else {
                self[isRight ? "mergeLeft" : "mergeRight"].push(col);
            }
        });

        var topLeft = this._createTopLeft();
        var topRight = this._createTopRight();
        var bottomLeft = this._createBottomLeft();
        var bottomRight = this._createBottomRight();

        this.scrollTopLeft = BI.createWidget({
            type: "bi.adaptive",
            cls: "scroll-top-left",
            width: "100%",
            height: "100%",
            scrollable: false,
            items: [topLeft]
        });
        this.scrollTopRight = BI.createWidget({
            type: "bi.adaptive",
            cls: "scroll-top-right",
            width: "100%",
            height: "100%",
            scrollable: false,
            items: [topRight]
        });
        this.scrollBottomLeft = BI.createWidget({
            type: "bi.adaptive",
            cls: "scroll-bottom-left",
            width: "100%",
            height: "100%",
            scrollable: isRight || null,
            scrollx: !isRight,
            items: [bottomLeft]
        });
        this.scrollBottomRight = BI.createWidget({
            type: "bi.adaptive",
            cls: "scroll-bottom-right",
            width: "100%",
            height: "100%",
            scrollable: !isRight || null,
            scrollx: isRight,
            items: [bottomRight]
        });
        this.topLeft = BI.createWidget({
            type: "bi.adaptive",
            cls: "top-left",
            scrollable: false,
            items: [this.scrollTopLeft]
        });
        this.topRight = BI.createWidget({
            type: "bi.adaptive",
            cls: "top-right",
            scrollable: false,
            items: [this.scrollTopRight]
        });
        this.bottomLeft = BI.createWidget({
            type: "bi.adaptive",
            cls: "bottom-left",
            //scrollable: false,
            items: [this.scrollBottomLeft]
        });
        this.bottomRight = BI.createWidget({
            type: "bi.adaptive",
            cls: "bottom-right",
            //scrollable: false,
            items: [this.scrollBottomRight]
        });

        var headerHeight = o.header.length * ((o.headerRowSize || o.rowSize) + 1);
        var leftWidth = BI.sum(o.freezeCols, function (i, col) {
            return o.columnSize[col] > 1 ? o.columnSize[col] + 1 : o.columnSize[col];
        });

        if (o.isNeedResize) {
            var handle;
            if (isRight) {
                var options = {
                    handles: "w",
                    minWidth: 15,
                    start: function (event, ui) {
                        self.fireEvent(BI.Table.EVENT_TABLE_BEFORE_REGION_RESIZE);
                    },
                    resize: function (e, ui) {
                        if (o.isResizeAdapt) {
                            var increment = ui.size.width - (BI.sum(self.columnRight) + self.columnRight.length);
                            o.columnSize[self.columnLeft.length] += increment;
                        } else {
                            self.setRegionColumnSize(["fill", ui.size.width]);
                        }
                        self._resize();
                        ui.element.css("left", "");
                        self.fireEvent(BI.Table.EVENT_TABLE_REGION_RESIZE);
                        e.stopPropagation();
                        return false;
                    },
                    stop: function (e, ui) {
                        self.fireEvent(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE);
                    }
                };
                self.bottomRight.element.resizable(options);
                handle = $(".ui-resizable-handle", this.bottomRight.element).css("top", -1 * headerHeight);
            } else {
                var options = {
                    handles: "e",
                    minWidth: 15,
                    start: function (event, ui) {
                        self.fireEvent(BI.Table.EVENT_TABLE_BEFORE_REGION_RESIZE);
                    },
                    resize: function (e, ui) {
                        if (o.isResizeAdapt) {
                            var increment = ui.size.width - (BI.sum(self.columnLeft) + self.columnLeft.length);
                            o.columnSize[self.columnLeft.length - 1] += increment;
                        } else {
                            self.setRegionColumnSize([ui.size.width, "fill"]);
                        }
                        self._resize();
                        self.fireEvent(BI.Table.EVENT_TABLE_REGION_RESIZE);
                        e.stopPropagation();
                        return false;
                    },
                    stop: function (e, ui) {
                        self.fireEvent(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE);
                    }
                };
                self.bottomLeft.element.resizable(options);
                handle = $(".ui-resizable-handle", this.bottomLeft.element).css("top", -1 * headerHeight);
            }
        }

        this.partitions = BI.createWidget(BI.extend({
            element: this.element
        }, BI.LogicFactory.createLogic("table", BI.extend({}, o.logic, {
            rows: 2,
            columns: 2,
            columnSize: o.regionColumnSize || (isRight ? ['fill', leftWidth] : [leftWidth, 'fill']),
            rowSize: [headerHeight, 'fill'],
            items: [[{
                el: this.topLeft
            }, {
                el: this.topRight
            }], [{
                el: this.bottomLeft
            }, {
                el: this.bottomRight
            }]]
        }))));

        //var scrollElement = isRight ? scrollBottomLeft.element : scrollBottomRight.element;
        //var scrollTopElement = isRight ? scrollTopLeft.element : scrollTopRight.element;
        //var otherElement = isRight ? scrollBottomRight.element : scrollBottomLeft.element;

        scroll(this.scrollBottomRight.element, this.scrollTopRight.element, this.scrollBottomLeft.element);
        scroll(this.scrollBottomLeft.element, this.scrollTopLeft.element, this.scrollBottomRight.element);
        function scroll(scrollElement, scrollTopElement, otherElement) {
            var fn = function (event, delta, deltaX, deltaY) {
                if (deltaY === -1 || deltaY === 1) {
                    var old = scrollElement[0].scrollTop;
                    var scrollTop = otherElement[0].scrollTop - delta * self._const.delta;
                    otherElement[0].scrollTop = scrollTop;
                    scrollElement[0].scrollTop = scrollTop;
                    self.fireEvent(BI.Table.EVENT_TABLE_SCROLL);
                    if (Math.abs(old - scrollElement[0].scrollTop) > 0.1) {
                        event.stopPropagation();
                        return false;
                    }
                }
            };
            otherElement.mousewheel(fn);
            var scrollTop = 0, scrollLeft = 0;
            scrollElement.scroll(function (e) {
                var change = false;
                if (scrollElement.scrollTop() != scrollTop) {
                    var old = otherElement.scrollTop();
                    otherElement.scrollTop(scrollElement.scrollTop());
                    scrollTop = scrollElement.scrollTop();
                    if (Math.abs(old - otherElement[0].scrollTop) > 0.1) {
                        e.stopPropagation();
                        change = true;
                    }
                }
                if (scrollElement.scrollLeft() != scrollLeft) {
                    var old = scrollTopElement.scrollLeft();
                    scrollTopElement.scrollLeft(scrollElement.scrollLeft());
                    scrollLeft = scrollElement.scrollLeft();
                    if (Math.abs(old - scrollTopElement[0].scrollLeft) > 0.1) {
                        e.stopPropagation();
                        change = true;
                    }
                }
                self.fireEvent(BI.Table.EVENT_TABLE_SCROLL);
                if (change === true) {
                    e.stopPropagation();
                    return false;
                }
            });
        }

        this._resize = function () {
            if (self.scrollBottomLeft.element.is(":visible")) {
                self.scrollBottomLeft.element.css({"overflow-x": "auto"});
                self.scrollBottomRight.element.css({"overflow-x": "auto"});
                self.setColumnSize(o.columnSize);
                if (isRight) {
                    self.scrollBottomLeft.element.css({"overflow-y": "auto"});
                } else {
                    self.scrollBottomRight.element.css({"overflow-y": "auto"});
                }
                if (self.scrollBottomLeft.element.hasHorizonScroll() || self.scrollBottomRight.element.hasHorizonScroll()) {
                    self.scrollBottomLeft.element.css("overflow-x", "scroll");
                    self.scrollBottomRight.element.css("overflow-x", "scroll");
                }
                if (self.scrollBottomRight.element.hasVerticalScroll()) {
                    self.scrollTopRight.element.css("overflow-y", "scroll");
                } else {
                    self.scrollTopRight.element.css("overflow-y", "hidden");
                }
                if (self.scrollBottomLeft.element.hasVerticalScroll()) {
                    self.scrollTopLeft.element.css("overflow-y", "scroll");
                } else {
                    self.scrollTopLeft.element.css("overflow-y", "hidden");
                }
                //调整拖拽handle的高度
                if (o.isNeedResize) {
                    handle && handle.css("height", self.bottomLeft.element.height() + headerHeight);
                }
            }
        };

        BI.defer(function () {
            if (self.element.is(":visible")) {
                self._resize();
                self.fireEvent(BI.Table.EVENT_TABLE_AFTER_INIT);
            }
        });
        BI.Resizers.add(this.getName(), function () {
            if (self.element.is(":visible")) {
                self._resize();
                self.fireEvent(BI.Table.EVENT_TABLE_RESIZE);
            }
        });
    },

    resize: function () {
        this._resize();
    },

    _createCells: function (items, columnSize, mergeCols, store, widgets, start) {
        var self = this, o = this.options, preCol = {}, preRow = {}, preW = {}, map = {};
        columnSize = columnSize || o.columnSize;
        mergeCols = mergeCols || o.mergeCols;
        store = store || {};
        widgets = widgets || {};
        start = start || 0;
        var frag = document.createDocumentFragment();
        BI.each(items, function (i, rows) {
            var tr = $("<tr>").addClass((i & 1) === 0 ? "odd" : "even");
            BI.each(rows, function (j, row) {
                if (!map[i]) {
                    map[i] = {};
                }
                if (!store[i]) {
                    store[i] = {};
                }
                if (!widgets[i]) {
                    widgets[i] = {};
                }
                map[i][j] = row;
                //优先合并行,若行能合并则不考虑列的合并
                if (!mergeCols.contains(j) || j === 0 || !o.isNeedMerge || !o.mergeRule(map[i][j], map[i][j - 1])) {
                    if (!mergeCols.contains(j) || i === 0 || !o.isNeedMerge || !o.mergeRule(map[i][j], map[i - 1][j])) {
                        var width = self._calculateWidth(columnSize[j]);
                        var height = self._calculateHeight(o.rowSize);
                        var td = $("<td>").attr("height", height)
                            .attr("width", width).css({"width": width, "height": height, "position": "relative"})
                            .addClass((j & 1) === 0 ? "odd" : "even");
                        var w = BI.createWidget(row, {
                            type: "bi.table_cell",
                            width: BI.isNumeric(width) ? width : "",
                            height: BI.isNumeric(height) ? height : "",
                            _row: i,
                            _col: j + start
                        });
                        td.append(w.element);
                        tr.append(td);
                        preCol[j] = td;
                        preRow[i] = td;
                        preRow[i].__mergeCols = [j];
                        preW[i] = w;
                        store[i][j] = td;
                        widgets[i][j] = w;
                    } else {//需要合并列
                        if (o.isNeedFreeze) {
                            var height = (preCol[j].attr("height") | 0) + o.rowSize + 1;
                            preCol[j].attr("height", height).css("height", height);
                        }
                        var rowspan = ((preCol[j].attr("rowspan") || 1) | 0) + 1;
                        preCol[j].attr("rowspan", rowspan);
                    }
                } else {//需要合并行
                    if (columnSize[j]) {
                        var width = preRow[i].attr("width") | 0;
                        if (width > 0 && columnSize[j]) {
                            width = width + columnSize[j] + 1;
                        } else {
                            width = width + columnSize[j]
                        }
                        width = self._calculateWidth(width);
                        preRow[i].attr("width", width).css("width", width);
                        preW[i].element.width(width);
                    }
                    store[i][j] = preRow[i];
                    store[i][j].__mergeCols = store[i][j].__mergeCols.concat(j);
                    widgets[i][j] = preW[i];
                    var colspan = ((preRow[i].attr("colspan") || 1) | 0) + 1;
                    preRow[i].attr("colspan", colspan);
                }

            });
            frag.appendChild(tr[0]);
        });
        return frag;
    },

    _createColGroupCells: function (columnSize, store) {
        var self = this, o = this.options;
        columnSize = columnSize || o.columnSize;
        store = store || {};
        var frag = document.createDocumentFragment();
        BI.each(columnSize, function (i, size) {
            var width = self._calculateWidth(size);
            var col = $("<col>").attr("width", width).css("width", width);
            store[i] = col;
            frag.appendChild(col[0]);
        });
        return frag;
    },

    _createHeaderCells: function (items, columnSize, mergeCols, store, widgets, start) {
        var self = this, o = this.options, preCol = {}, preRow = {}, preW = {}, map = {};
        var isRight = this._isRightFreeze();
        columnSize = columnSize || o.columnSize;
        mergeCols = mergeCols || o.mergeCols;
        store = store || {};
        widgets = widgets || {};
        start = start || 0;
        var frag = document.createDocumentFragment();
        BI.each(items, function (i, rows) {
            var tr = $("<tr>").addClass((i & 1) === 0 ? "odd" : "even");
            BI.each(rows, function (j, row) {
                if (!map[i]) {
                    map[i] = {};
                }
                if (!store[i]) {
                    store[i] = {};
                }
                if (!widgets[i]) {
                    widgets[i] = {};
                }
                map[i][j] = row;
                if (j === 0 || !o.isNeedMerge || !o.mergeRule(map[i][j], map[i][j - 1])) {
                    if (i === 0 || !o.isNeedMerge || !o.mergeRule(map[i][j], map[i - 1][j])) {
                        var width = self._calculateWidth(columnSize[j]);
                        var height = self._calculateHeight(o.headerRowSize || o.rowSize);
                        var td = $("<td>").attr("height", height)
                            .attr("width", width).css({"width": width, "height": height, "position": "relative"})
                            .addClass((j & 1) === 0 ? "odd" : "even");
                        //只有header的最后一行去掉最后一列才可以调整列宽
                        if (o.isNeedResize && i === items.length - 1 && j < rows.length - 1) {
                            td.resizable({
                                handles: "e",
                                minWidth: 15,
                                start: function (event, ui) {
                                    self.fireEvent(BI.Table.EVENT_TABLE_BEFORE_COLUMN_RESIZE);
                                },
                                resize: function (e, ui) {
                                    o.columnSize[start + j] = ui.size.width;
                                    self.setColumnSize(o.columnSize);
                                    self.fireEvent(BI.Table.EVENT_TABLE_COLUMN_RESIZE);
                                    e.stopPropagation();
                                    return false;
                                },
                                stop: function (e, ui) {
                                    self.fireEvent(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE);
                                }
                            })
                        }
                        var w = BI.createWidget(row, {
                            type: "bi.table_header_cell",
                            width: BI.isNumeric(width) ? width : "",
                            height: BI.isNumeric(height) ? height : "",
                            _row: i,
                            _col: j + start
                        });
                        td.append(w.element);
                        tr.append(td);
                        preCol[j] = td;
                        preRow[i] = td;
                        preRow[i].__mergeCols = [j];
                        preW[i] = w;
                        store[i][j] = td;
                        widgets[i][j] = w;
                    } else {//需要合并列
                        if (o.isNeedFreeze) {
                            var height = (preCol[j].attr("height") | 0) + (o.headerRowSize || o.rowSize) + 1;
                            preCol[j].attr("height", height).css("height", height);
                        }
                        var rowspan = ((preCol[j].attr("rowspan") || 1) | 0) + 1;
                        preCol[j].attr("rowspan", rowspan);
                    }
                } else {//需要合并行
                    if (columnSize[j]) {
                        var width = preRow[i].attr("width") | 0;
                        if (width > 0) {
                            width = width + columnSize[j] + 1;
                        } else {
                            width = width + columnSize[j]
                        }
                        width = self._calculateWidth(width);
                        preRow[i].attr("width", width).css("width", width);
                        preW[i].element.width(width);
                    }
                    store[i][j] = preRow[i];
                    store[i][j].__mergeCols = store[i][j].__mergeCols.concat(j);
                    widgets[i][j] = preW[i];
                    var colspan = ((preRow[i].attr("colspan") || 1) | 0) + 1;
                    preRow[i].attr("colspan", colspan);
                }
            });
            frag.appendChild(tr[0]);
        });
        return frag;
    },

    _createFooterCells: function (items, columnSize, store, widgets) {
        var o = this.options, frag = document.createDocumentFragment();
        columnSize = columnSize || o.columnSize;
        store = store || {};
        widgets = widgets || {};
        BI.each(items, function (i, rows) {
            var tr = $("<tr>").addClass((i & 1) === 0 ? "odd" : "even");
            if (!store[i]) {
                store[i] = {};
            }
            if (!widgets[i]) {
                widgets[i] = {};
            }
            BI.each(rows, function (j, row) {
                var width = self._calculateWidth(columnSize[j]);
                var td = $("<td>").height(o.footerRowSize || o.rowSize)
                    .attr("width", width).css("width", width)
                    .addClass((j & 1) === 0 ? "odd" : "even");
                var w = BI.createWidget(row, {
                    type: "bi.table_footer_cell",
                    _row: i,
                    _col: j
                });
                td.append(w.element);
                tr.append(td);
                store[i][j] = td;
                widgets[i][j] = w;
            });
            frag.appendChild(tr[0]);
        });
        return frag;
    },

    _createColGroup: function (columnSize, store, widgets) {
        var self = this, o = this.options;
        this.colgroup = this._colgroup();
        this.colgroup.element.append(this._createColGroupCells(columnSize, store, widgets));
        return this.colgroup;
    },

    _createHeader: function (columnSize, mergeCols, store, widgets) {
        var self = this, o = this.options;
        if (o.header === false) {
            return;
        }
        this.header = this._header();
        this.header.element.append(this._createHeaderCells(o.header, columnSize, mergeCols, store, widgets));
        return this.header;
    },

    _createFooter: function (columnSize, store, widgets) {
        var self = this, o = this.options;
        if (o.footer === false) {
            return;
        }
        this.footer = this._footer();
        this.footer.element.append(this._createFooterCells(o.footer, columnSize, store, widgets));
        return this.footer;
    },

    _createBody: function (columnSize, mergeCols, store, widgets) {
        var self = this, o = this.options;
        this.body = this._body();
        this.body.element.append(this._createCells(o.items, columnSize, mergeCols, store, widgets));
        return this.body;
    },

    _createNormalTable: function () {
        var self = this, o = this.options, table = this._table();
        this.colgroupTds = {};
        this.headerTds = {};
        this.footerTds = {};
        this.bodyTds = {};

        this.headerItems = {};
        this.footerItems = {};
        this.bodyItems = {};
        var colgroup = this._createColGroup(null, this.colgroupTds);
        var header = this._createHeader(null, null, this.headerTds, this.headerItems);
        var footer = this._createFooter(null, this.footerTds, this.footerItems);
        var body = this._createBody(null, null, this.bodyTds, this.bodyItems);

        BI.createWidget({
            type: "bi.adaptive",
            element: table,
            items: [colgroup, header, footer, body]
        });

        var w = BI.sum(this.options.columnSize) || undefined;
        w = this._calculateWidth(w);
        if (BI.isNumeric(w) && w > 1) {
            w += o.columnSize.length;
        }
        this.tableContainer = BI.createWidget({
            type: "bi.adaptive",
            width: this._calculateWidth(w),
            items: [table]
        });

        this.scrollContainer = BI.createWidget({
            type: "bi.adaptive",
            width: "100%",
            height: "100%",
            cls: "scroll-table",
            scrollable: true,
            items: [this.tableContainer]
        });

        BI.createWidget({
            type: "bi.adaptive",
            element: this.element,
            scrollable: false,
            items: [this.scrollContainer]
        });
        this.scrollContainer.element.mousewheel(function (event, delta, deltaX, deltaY) {
            if (deltaY === -1 || deltaY === 1) {
                var old = self.scrollContainer.element[0].scrollTop;
                self.scrollContainer.element[0].scrollTop = self.scrollContainer.element[0].scrollTop - delta * self._const.delta;
                self.fireEvent(BI.Table.EVENT_TABLE_SCROLL);
                if (Math.abs(old - self.scrollContainer.element[0].scrollTop) > 0.1) {
                    event.stopPropagation();
                    return false;
                }
            }
        });
        this.scrollContainer.element.scroll(function () {
            self.fireEvent(BI.Table.EVENT_TABLE_SCROLL);
        });
        this._resize = function () {
            if (self.element.is(":visible")) {
                self.setColumnSize(o.columnSize);
            }
        };
        BI.Resizers.add(this.getName(), function () {
            if (self.element.is(":visible")) {
                self._resize();
                self.fireEvent(BI.Table.EVENT_TABLE_RESIZE);
            }
        });
        BI.defer(function () {
            if (self.element.is(":visible")) {
                self.fireEvent(BI.Table.EVENT_TABLE_AFTER_INIT);
            }
        });
    },

    _split: function (items) {
        var o = this.options, left = [], right = [], isRight = this._isRightFreeze();
        BI.each(items, function (i, rows) {
            left.push([]);
            right.push([]);
            BI.each(rows, function (j, cell) {
                if (o.freezeCols.contains(j)) {
                    (isRight ? right : left)[i].push(cell);
                } else {
                    (isRight ? left : right)[i].push(cell);
                }
            })
        });
        return {
            left: left,
            right: right
        }
    },

    _table: function () {
        return BI.createWidget({
            type: "bi.layout",
            tagName: "table",
            cls: "table",
            attribute: {"cellspacing": 0, "cellpadding": 0}
        });
    },

    _header: function () {
        return BI.createWidget({
            type: "bi.layout",
            cls: "header",
            tagName: "thead"
        });
    },

    _footer: function () {
        return BI.createWidget({
            type: "bi.layout",
            cls: "footer",
            tagName: "tfoot"
        });
    },

    _body: function () {
        return BI.createWidget({
            type: "bi.layout",
            tagName: "tbody",
            cls: "body"
        });
    },

    _colgroup: function () {
        return BI.createWidget({
            type: "bi.layout",
            tagName: "colgroup"
        });
    },

    _init: function () {
        BI.Table.superclass._init.apply(this, arguments);

        this.populate(this.options.items);
    },

    setColumnSize: function (columnSize) {
        var self = this, o = this.options;
        var isRight = this._isRightFreeze();
        o.columnSize = columnSize || [];
        if (o.isNeedFreeze) {
            var columnLeft = [];
            var columnRight = [];
            BI.each(o.columnSize, function (i, size) {
                if (o.freezeCols.contains(i)) {
                    isRight ? columnRight.push(size) : columnLeft.push(size);
                } else {
                    isRight ? columnLeft.push(size) : columnRight.push(size);
                }
            });
            var topleft = 0, topright = 1, bottomleft = 2, bottomright = 3;
            var run = function (direction) {
                var colgroupTds, bodyTds, bodyItems, sizes;
                switch (direction) {
                    case topleft:
                        colgroupTds = self.topLeftColGroupTds;
                        bodyTds = self.topLeftBodyTds;
                        bodyItems = self.topLeftBodyItems;
                        sizes = columnLeft;
                        break;
                    case topright:
                        colgroupTds = self.topRightColGroupTds;
                        bodyTds = self.topRightBodyTds;
                        bodyItems = self.topRightBodyItems;
                        sizes = columnRight;
                        break;
                    case bottomleft:
                        colgroupTds = self.bottomLeftColGroupTds;
                        bodyTds = self.bottomLeftBodyTds;
                        bodyItems = self.bottomLeftBodyItems;
                        sizes = columnLeft;
                        break;
                    case bottomright:
                        colgroupTds = self.bottomRightColGroupTds;
                        bodyTds = self.bottomRightBodyTds;
                        bodyItems = self.bottomRightBodyItems;
                        sizes = columnRight;
                        break;
                }
                BI.each(colgroupTds, function (i, colgroup) {
                    var width = colgroup.attr("width") | 0;
                    if (width !== sizes[i]) {
                        var w = self._calculateWidth(sizes[i]);
                        colgroup.attr("width", w).css("width", w);
                        BI.each(bodyTds, function (j, items) {
                            if (items[i]) {
                                if (items[i].__mergeCols.length > 1) {
                                    var wid = 0;
                                    BI.each(sizes, function (t, s) {
                                        if (items[i].__mergeCols.contains(t)) {
                                            wid += s;
                                        }
                                    });
                                    wid = self._calculateWidth(wid);
                                    if (wid > 1) {
                                        wid += items[i].__mergeCols.length - 1;
                                    }
                                    if (BI.isNumeric(wid)) {
                                        items[i].attr("width", wid).css("width", wid);
                                    } else {
                                        items[i].attr("width", "").css("width", "");
                                    }
                                } else {
                                    items[i].attr("width", w).css("width", w);
                                }
                            }
                        });
                        BI.each(bodyItems, function (j, items) {
                            if (items[i]) {
                                if (bodyTds[j][i].__mergeCols.length > 1) {
                                    var wid = 0;
                                    BI.each(sizes, function (t, s) {
                                        if (bodyTds[j][i].__mergeCols.contains(t)) {
                                            wid += s;
                                        }
                                    });
                                    wid = self._calculateWidth(wid);
                                    if (wid > 1) {
                                        wid += bodyTds[j][i].__mergeCols.length - 1;
                                    }
                                    if (BI.isNumeric(wid)) {
                                        items[i].element.attr("width", wid).css("width", wid);
                                    } else {
                                        items[i].element.attr("width", "").css("width", "");
                                    }
                                } else {
                                    if (BI.isNumeric(w)) {
                                        items[i].element.attr("width", w).css("width", w);
                                    } else {
                                        items[i].element.attr("width", "").css("width", "");
                                    }
                                }
                            }
                        });
                    }
                })
            };
            run(topleft);
            run(topright);
            run(bottomleft);
            run(bottomright);

            var lw = 0, rw = 0;
            this.columnLeft = [];
            this.columnRight = [];
            BI.each(o.columnSize, function (i, size) {
                if (o.freezeCols.contains(i)) {
                    lw += size;
                    self[isRight ? "columnRight" : "columnLeft"].push(size);
                } else {
                    rw += size;
                    self[isRight ? "columnLeft" : "columnRight"].push(size);
                }
            });
            lw = this._calculateWidth(lw);
            rw = this._calculateWidth(rw);

            if (BI.isNumeric(lw)) {
                lw = BI.parseFloat(lw) + o.freezeCols.length;
            }
            if (BI.isNumeric(rw)) {
                rw = BI.parseFloat(rw) + o.columnSize.length - o.freezeCols.length;
            }
            this.topLeftContainer.element.width(isRight ? rw : lw);
            this.bottomLeftContainer.element.width(isRight ? rw : lw);
            this.topRightContainer.element.width(isRight ? lw : rw);
            this.bottomRightContainer.element.width(isRight ? lw : rw);
            if (o.isNeedResize && o.isResizeAdapt) {
                var leftWidth = BI.sum(o.freezeCols, function (i, col) {
                    return o.columnSize[col] > 1 ? o.columnSize[col] + 1 : o.columnSize[col];
                });

                this.partitions.attr("columnSize", isRight ? ['fill', leftWidth] : [leftWidth, 'fill']);
                this.partitions.resize();
            }
        } else {
            BI.each(this.colgroupTds, function (i, colgroup) {
                var width = colgroup.attr("width") | 0;
                if (width !== o.columnSize[i]) {
                    var w = self._calculateWidth(o.columnSize[i]);
                    colgroup.attr("width", w).css("width", w);
                    BI.each(self.bodyTds, function (j, items) {
                        if (items[i]) {
                            if (items[i].__mergeCols.length > 1) {
                                var wid = 0;
                                BI.each(o.columnSize, function (t, s) {
                                    if (items[i].__mergeCols.contains(t)) {
                                        wid += s;
                                    }
                                });
                                wid = self._calculateWidth(wid);
                                if (wid > 1) {
                                    wid += items[i].__mergeCols.length - 1;
                                }
                                if (BI.isNumeric(wid)) {
                                    items[i].attr("width", wid).css("width", wid);
                                } else {
                                    items[i].attr("width", "").css("width", "");
                                }
                            } else {
                                items[i].attr("width", w).css("width", w);
                            }
                        }
                    });
                    BI.each(self.headerTds, function (j, items) {
                        if (items[i]) {
                            if (items[i].__mergeCols.length > 1) {
                                var wid = 0;
                                BI.each(o.columnSize, function (t, s) {
                                    if (items[i].__mergeCols.contains(t)) {
                                        wid += s;
                                    }
                                });
                                wid = self._calculateWidth(wid);
                                if (wid > 1) {
                                    wid += items[i].__mergeCols.length - 1;
                                }
                                if (BI.isNumeric(wid)) {
                                    items[i].attr("width", wid).css("width", wid);
                                } else {
                                    items[i].attr("width", "").css("width", "");
                                }
                            } else {
                                items[i].attr("width", w).css("width", w);
                            }
                        }
                    });
                    BI.each(self.footerTds, function (j, items) {
                        if (items[i]) {
                            if (items[i].__mergeCols.length > 1) {
                                var wid = 0;
                                BI.each(o.columnSize, function (t, s) {
                                    if (items[i].__mergeCols.contains(t)) {
                                        wid += s;
                                    }
                                });
                                wid = self._calculateWidth(wid);
                                if (wid > 1) {
                                    wid += items[i].__mergeCols.length - 1;
                                }
                                if (BI.isNumeric(wid)) {
                                    items[i].attr("width", wid).css("width", wid);
                                } else {
                                    items[i].attr("width", "").css("width", "");
                                }
                            } else {
                                items[i].attr("width", w).css("width", w);
                            }
                        }
                    });
                    BI.each(self.bodyItems, function (j, items) {
                        if (items[i]) {
                            if (self.bodyTds[j][i].__mergeCols.length > 1) {
                                var wid = 0;
                                BI.each(o.columnSize, function (t, s) {
                                    if (self.bodyTds[j][i].__mergeCols.contains(t)) {
                                        wid += s;
                                    }
                                });
                                wid = self._calculateWidth(wid);
                                if (wid > 1) {
                                    wid += self.bodyTds[j][i].__mergeCols.length - 1;
                                }
                                if (BI.isNumeric(wid)) {
                                    items[i].element.attr("width", wid).css("width", wid);
                                } else {
                                    items[i].element.attr("width", "").css("width", "");
                                }
                            } else {
                                if (BI.isNumeric(w)) {
                                    items[i].element.attr("width", w).css("width", w);
                                } else {
                                    items[i].element.attr("width", "").css("width", "");
                                }
                            }
                        }
                    });
                    BI.each(self.headerItems, function (j, items) {
                        if (items[i]) {
                            if (self.headerTds[j][i].__mergeCols.length > 1) {
                                var wid = 0;
                                BI.each(o.columnSize, function (t, s) {
                                    if (self.headerTds[j][i].__mergeCols.contains(t)) {
                                        wid += s;
                                    }
                                });
                                wid = self._calculateWidth(wid);
                                if (wid > 1) {
                                    wid += self.headerTds[j][i].__mergeCols.length - 1;
                                }
                                if (BI.isNumeric(wid)) {
                                    items[i].element.attr("width", wid).css("width", wid);
                                } else {
                                    items[i].element.attr("width", "").css("width", "");
                                }
                            } else {
                                if (BI.isNumeric(w)) {
                                    items[i].element.attr("width", w).css("width", w);
                                } else {
                                    items[i].element.attr("width", "").css("width", "");
                                }
                            }
                        }
                    });
                    BI.each(self.footerItems, function (j, items) {
                        if (items[i]) {
                            if (self.footerTds[j][i].__mergeCols.length > 1) {
                                var wid = 0;
                                BI.each(o.columnSize, function (t, s) {
                                    if (self.footerTds[j][i].__mergeCols.contains(t)) {
                                        wid += s;
                                    }
                                });
                                wid = self._calculateWidth(wid);
                                if (wid > 1) {
                                    wid += self.footerTds[j][i].__mergeCols.length - 1;
                                }
                                if (BI.isNumeric(wid)) {
                                    items[i].element.attr("width", wid).css("width", wid);
                                } else {
                                    items[i].element.attr("width", "").css("width", "");
                                }
                            } else {
                                if (BI.isNumeric(w)) {
                                    items[i].element.attr("width", w).css("width", w);
                                } else {
                                    items[i].element.attr("width", "").css("width", "");
                                }
                            }
                        }
                    });
                }
            });
            var w = this._calculateWidth(BI.sum(o.columnSize));
            if (w > 1) {
                w += o.columnSize.length;
            }
            this.tableContainer.element.width(w);
        }
    },

    getColumnSize: function () {
        return this.options.columnSize;
    },

    getCalculateColumnSize: function () {
        var o = this.options;
        var columnSize = [];
        if (o.isNeedFreeze) {
            if (!BI.any(this.bottomLeftBodyTds, function (i, tds) {
                    if (!BI.any(tds, function (i, item) {
                            if (item.__mergeCols.length > 1) {
                                return true;
                            }
                        })) {
                        BI.each(tds, function (i, item) {
                            columnSize.push(item.width() / item.__mergeCols.length);
                        });
                        return true;
                    }
                })) {
                BI.each(this.bottomLeftBodyTds[0], function (i, item) {
                    columnSize.push(item.width() / item.__mergeCols.length);
                });
            }
            if (!BI.any(this.bottomRightBodyTds, function (i, tds) {
                    if (!BI.any(tds, function (i, item) {
                            if (item.__mergeCols.length > 1) {
                                return true;
                            }
                        })) {
                        BI.each(tds, function (i, item) {
                            columnSize.push(item.width() / item.__mergeCols.length);
                        });
                        return true;
                    }
                })) {
                BI.each(this.bottomRightBodyTds[0], function (i, item) {
                    columnSize.push(item.width() / item.__mergeCols.length);
                });
            }
        } else {
            BI.each(this.headerTds[BI.size(this.headerTds) - 1], function (i, item) {
                columnSize.push(item.width() / item.__mergeCols.length);
            });
        }
        return columnSize;
    },

    setHeaderColumnSize: function (columnSize) {
        var self = this, o = this.options;
        var isRight = this._isRightFreeze();
        if (o.isNeedFreeze) {
            var columnLeft = [];
            var columnRight = [];
            BI.each(columnSize, function (i, size) {
                if (o.freezeCols.contains(i)) {
                    isRight ? columnRight.push(size) : columnLeft.push(size);
                } else {
                    isRight ? columnLeft.push(size) : columnRight.push(size);
                }
            });
            var topleft = 0, topright = 1;
            var run = function (direction) {
                var colgroupTds, bodyTds, bodyItems, sizes;
                switch (direction) {
                    case topleft:
                        colgroupTds = self.topLeftColGroupTds;
                        bodyTds = self.topLeftBodyTds;
                        bodyItems = self.topLeftBodyItems;
                        sizes = columnLeft;
                        break;
                    case topright:
                        colgroupTds = self.topRightColGroupTds;
                        bodyTds = self.topRightBodyTds;
                        bodyItems = self.topRightBodyItems;
                        sizes = columnRight;
                        break;
                }
                BI.each(colgroupTds, function (i, colgroup) {
                    var width = colgroup.attr("width") | 0;
                    if (width !== sizes[i]) {
                        var w = self._calculateWidth(sizes[i]);
                        colgroup.attr("width", w).css("width", w);
                        BI.each(bodyTds, function (j, items) {
                            if (items[i]) {
                                if (items[i].__mergeCols.length > 1) {
                                    var wid = 0;
                                    BI.each(sizes, function (t, s) {
                                        if (items[i].__mergeCols.contains(t)) {
                                            wid += s;
                                        }
                                    });
                                    wid = self._calculateWidth(wid);
                                    if (wid > 1) {
                                        wid += items[i].__mergeCols.length - 1;
                                    }
                                    if (BI.isNumeric(wid)) {
                                        items[i].attr("width", wid).css("width", wid);
                                    } else {
                                        items[i].attr("width", "").css("width", "");
                                    }
                                } else {
                                    items[i].attr("width", w).css("width", w);
                                }
                            }
                        });
                        BI.each(bodyItems, function (j, items) {
                            if (items[i]) {
                                if (bodyTds[j][i].__mergeCols.length > 1) {
                                    var wid = 0;
                                    BI.each(sizes, function (t, s) {
                                        if (bodyTds[j][i].__mergeCols.contains(t)) {
                                            wid += s;
                                        }
                                    });
                                    wid = self._calculateWidth(wid);
                                    if (wid > 1) {
                                        wid += bodyTds[j][i].__mergeCols.length - 1;
                                    }
                                    if (BI.isNumeric(wid)) {
                                        items[i].element.attr("width", wid).css("width", wid);
                                    } else {
                                        items[i].element.attr("width", "").css("width", "");
                                    }
                                } else {
                                    if (BI.isNumeric(w)) {
                                        items[i].element.attr("width", w).css("width", w);
                                    } else {
                                        items[i].element.attr("width", "").css("width", "");
                                    }
                                }
                            }
                        });
                    }
                })
            };
            run(topleft);
            run(topright);

            var lw = 0, rw = 0;
            BI.each(o.columnSize, function (i, size) {
                if (o.freezeCols.contains(i)) {
                    lw += size;
                } else {
                    rw += size;
                }
            });
            lw = this._calculateWidth(lw);
            rw = this._calculateWidth(rw);

            if (BI.isNumeric(lw)) {
                lw = BI.parseFloat(lw) + o.freezeCols.length;
            }
            if (BI.isNumeric(rw)) {
                rw = BI.parseFloat(rw) + o.columnSize.length - o.freezeCols.length;
            }
            this.topLeftContainer.element.width(isRight ? rw : lw);
            this.topRightContainer.element.width(isRight ? lw : rw);
            if (o.isNeedResize && o.isResizeAdapt) {
                var leftWidth = BI.sum(o.freezeCols, function (i, col) {
                    return o.columnSize[col] > 1 ? o.columnSize[col] + 1 : o.columnSize[col];
                });

                this.partitions.attr("columnSize", isRight ? ['fill', leftWidth] : [leftWidth, 'fill']);
                this.partitions.resize();
            }
        } else {
            o.columnSize = columnSize || [];
            BI.each(this.colgroupTds, function (i, colgroup) {
                var width = colgroup.attr("width") | 0;
                if (width !== o.columnSize[i]) {
                    var w = self._calculateWidth(o.columnSize[i]);
                    colgroup.attr("width", w).css("width", w);
                    BI.each(self.headerTds, function (j, items) {
                        if (items[i]) {
                            if (items[i].__mergeCols.length > 1) {
                                var wid = 0;
                                BI.each(o.columnSize, function (t, s) {
                                    if (items[i].__mergeCols.contains(t)) {
                                        wid += s;
                                    }
                                });
                                wid = self._calculateWidth(wid);
                                if (wid > 1) {
                                    wid += items[i].__mergeCols.length - 1;
                                }
                                if (BI.isNumeric(wid)) {
                                    items[i].attr("width", wid).css("width", wid);
                                } else {
                                    items[i].attr("width", "").css("width", "");
                                }
                            } else {
                                items[i].attr("width", w).css("width", w);
                            }
                        }
                    });
                    BI.each(self.headerItems, function (j, items) {
                        if (items[i]) {
                            if (self.headerTds[j][i].__mergeCols.length > 1) {
                                var wid = 0;
                                BI.each(o.columnSize, function (t, s) {
                                    if (self.headerTds[j][i].__mergeCols.contains(t)) {
                                        wid += s;
                                    }
                                });
                                wid = self._calculateWidth(wid);
                                if (wid > 1) {
                                    wid += self.headerTds[j][i].__mergeCols.length - 1;
                                }
                                if (BI.isNumeric(wid)) {
                                    items[i].element.attr("width", wid).css("width", wid);
                                } else {
                                    items[i].element.attr("width", "").css("width", "");
                                }
                            } else {
                                if (BI.isNumeric(w)) {
                                    items[i].element.attr("width", w).css("width", w);
                                } else {
                                    items[i].element.attr("width", "").css("width", "");
                                }
                            }
                        }
                    });
                }
            });
            this.tableContainer.element.width(this._calculateWidth(BI.sum(o.columnSize) + o.columnSize.length));
        }
    },

    setRegionColumnSize: function (columnSize) {
        var self = this, o = this.options;
        o.regionColumnSize = columnSize;
        if (o.isNeedFreeze) {
            this.partitions.attr("columnSize", columnSize);
            this.partitions.resize();
        } else {
            this.tableContainer.element.width(columnSize[0]);
        }
    },

    getRegionColumnSize: function () {
        return this.options.regionColumnSize;
    },

    getCalculateRegionColumnSize: function () {
        var o = this.options;
        if (o.isNeedFreeze) {
            return [this.scrollBottomLeft.element.width(), this.scrollBottomRight.element.width()];
        }
        return [this.scrollContainer.element.width()];
    },

    getCalculateRegionRowSize: function () {
        var o = this.options;
        if (o.isNeedFreeze) {
            return [this.scrollTopRight.element.height(), this.scrollBottomRight.element.height()];
        }
        return [this.scrollContainer.element.height()];
    },

    getClientRegionColumnSize: function () {
        var o = this.options;
        if (o.isNeedFreeze) {
            return [this.scrollBottomLeft.element[0].clientWidth, this.scrollBottomRight.element[0].clientWidth];
        }
        return [this.scrollContainer.element[0].clientWidth];
    },

    getScrollRegionColumnSize: function () {
        var o = this.options;
        if (o.isNeedFreeze) {
            return [this.scrollBottomLeft.element[0].scrollWidth, this.scrollBottomRight.element[0].scrollWidth];
        }
        return [this.scrollContainer.element[0].scrollWidth];
    },

    getScrollRegionRowSize: function () {
        var o = this.options;
        if (o.isNeedFreeze) {
            return [this.scrollTopRight.element[0].scrollHeight, this.scrollBottomRight.element[0].scrollHeight];
        }
        return [this.scrollContainer.element[0].scrollHeight];
    },

    hasVerticalScroll: function () {
        var o = this.options;
        if (o.isNeedFreeze) {
            if (this._isRightFreeze()) {
                return this.scrollBottomLeft.element.hasVerticalScroll();
            }
            return this.scrollBottomRight.element.hasVerticalScroll();
        }
        return this.scrollContainer.element.hasVerticalScroll();
    },

    setVerticalScroll: function (scrollTop) {
        var o = this.options;
        if (o.isNeedFreeze) {
            if (this.scrollBottomRight.element[0].scrollTop !== scrollTop) {
                this.scrollBottomRight.element[0].scrollTop = scrollTop;
            }
        } else {
            if (this.scrollContainer.element[0].scrollTop !== scrollTop) {
                this.scrollContainer.element[0].scrollTop = scrollTop;
            }
        }
    },

    setLeftHorizontalScroll: function (scrollLeft) {
        var o = this.options;
        if (o.isNeedFreeze) {
            if (this.scrollBottomLeft.element[0].scrollLeft !== scrollLeft) {
                this.scrollBottomLeft.element[0].scrollLeft = scrollLeft;
            }
        } else {
            if (this.scrollContainer.element[0].scrollLeft !== scrollLeft) {
                this.scrollContainer.element[0].scrollLeft = scrollLeft;
            }
        }
    },

    setRightHorizontalScroll: function (scrollLeft) {
        var o = this.options;
        if (o.isNeedFreeze) {
            if (this.scrollBottomRight.element[0].scrollLeft !== scrollLeft) {
                this.scrollBottomRight.element[0].scrollLeft = scrollLeft;
            }
        } else {
            if (this.scrollContainer.element[0].scrollLeft !== scrollLeft) {
                this.scrollContainer.element[0].scrollLeft = scrollLeft;
            }
        }
    },

    getVerticalScroll: function () {
        var o = this.options;
        if (o.isNeedFreeze) {
            return this.scrollBottomRight.element[0].scrollTop;
        }
        return this.scrollContainer.element[0].scrollTop;
    },

    getLeftHorizontalScroll: function () {
        var o = this.options;
        if (o.isNeedFreeze) {
            return this.scrollBottomLeft.element[0].scrollLeft;
        }
        return this.scrollContainer.element[0].scrollLeft;
    },

    getRightHorizontalScroll: function () {
        var o = this.options;
        if (o.isNeedFreeze) {
            return this.scrollBottomRight.element[0].scrollLeft;
        }
        return this.scrollContainer.element[0].scrollLeft;
    },

    getColumns: function () {
        var o = this.options;
        if (o.isNeedFreeze) {
            return {
                topLeft: this.topLeftBodyItems,
                topRight: this.topRightBodyItems,
                bottomLeft: this.bottomLeftBodyItems,
                bottomRight: this.bottomRightBodyItems
            }
        } else {
            return {
                header: this.headerItems,
                body: this.bodyItems,
                footer: this.footerItems
            }
        }
    },

    populate: function (items, header) {
        this.options.items = items || [];
        if (header) {
            this.options.header = header;
        }
        this.empty();
        if (this.options.isNeedFreeze) {
            this._createFreezeTable();
        } else {
            this._createNormalTable();
        }
    },

    destroy: function () {
        BI.Resizers.remove(this.getName());
        BI.Table.superclass.destroy.apply(this, arguments);
    }
})
;
BI.Table.EVENT_TABLE_AFTER_INIT = "EVENT_TABLE_AFTER_INIT";
BI.Table.EVENT_TABLE_RESIZE = "EVENT_TABLE_RESIZE";
BI.Table.EVENT_TABLE_SCROLL = "EVENT_TABLE_SCROLL";
BI.Table.EVENT_TABLE_BEFORE_COLUMN_RESIZE = "EVENT_TABLE_BEFORE_COLUMN_RESIZE";
BI.Table.EVENT_TABLE_COLUMN_RESIZE = "EVENT_TABLE_COLUMN_RESIZE";
BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE = "EVENT_TABLE_AFTER_COLUMN_RESIZE";

BI.Table.EVENT_TABLE_BEFORE_REGION_RESIZE = "EVENT_TABLE_BEFORE_REGION_RESIZE";
BI.Table.EVENT_TABLE_REGION_RESIZE = "EVENT_TABLE_REGION_RESIZE";
BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE = "EVENT_TABLE_AFTER_REGION_RESIZE";
$.shortcut("bi.table_view", BI.Table);