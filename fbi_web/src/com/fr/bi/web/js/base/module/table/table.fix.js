/**
 *
 * 表格
 *
 * Created by GUY on 2016/9/9.
 * @class BI.FixTable
 * @extends BI.Table
 */
BI.FixTable = BI.inherit(BI.Table, {

    _defaultConfig: function () {
        return BI.extend(BI.FixTable.superclass._defaultConfig.apply(this, arguments), {
            afterScroll: BI.emptyFn
        })
    },

    _createFreezeFixTable: function () {
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

        var headerHeight = o.header.length * ((o.headerRowSize || o.rowSize) + 1) + 1;
        var leftWidth = BI.sum(o.freezeCols, function (i, col) {
            return o.columnSize[col] > 1 ? o.columnSize[col] + 1 : o.columnSize[col];
        });

        if (o.isNeedResize) {
            var resizer;
            var createResizer = function (size, position) {
                var rowSize = self.getCalculateRegionRowSize();
                resizer = BI.createWidget({
                    type: "bi.layout",
                    cls: "bi-resizer",
                    width: size.width,
                    height: rowSize[0] + rowSize[1]
                });
                BI.createWidget({
                    type: "bi.absolute",
                    element: "body",
                    items: [{
                        el: resizer,
                        left: position.left,
                        top: position.top - rowSize[0]
                    }]
                });
            };
            var resizeResizer = function (size, position) {
                var rowSize = self.getCalculateRegionRowSize();
                var columnSize = self.getCalculateRegionColumnSize();
                var height = rowSize[0] + rowSize[1];
                var sumSize = columnSize[0] + columnSize[1];
                if (size.width > sumSize / 5 * 4) {
                    size.width = sumSize / 5 * 4;
                }
                if (size.width < sumSize / 5) {
                    size.width = sumSize / 5;
                }
                resizer.element.css({
                    "left": position.left + "px",
                    "width": size.width + "px",
                    "height": height + "px"
                });
            };
            var stopResizer = function () {
                resizer && resizer.destroy();
                resizer = null;
            };
            var handle;
            if (o.freezeCols.length > 0 && o.freezeCols.length < o.columnSize.length) {
                if (isRight) {
                    var options = {
                        handles: "w",
                        minWidth: 15,
                        helper: "clone",
                        start: function (event, ui) {
                            createResizer(ui.size, ui.position);
                            self.fireEvent(BI.FixTable.EVENT_TABLE_BEFORE_REGION_RESIZE);
                        },
                        resize: function (e, ui) {
                            resizeResizer(ui.size, ui.position);
                            self.fireEvent(BI.FixTable.EVENT_TABLE_REGION_RESIZE);
                            e.stopPropagation();
                            //return false;
                        },
                        stop: function (e, ui) {
                            stopResizer();
                            if (o.isResizeAdapt) {
                                var increment = ui.size.width - (BI.sum(self.columnRight) + self.columnRight.length);
                                o.columnSize[self.columnLeft.length] += increment;
                            } else {
                                self.setRegionColumnSize(["fill", ui.size.width]);
                            }
                            self._resize();
                            ui.element.css("left", "");
                            self.fireEvent(BI.FixTable.EVENT_TABLE_AFTER_REGION_RESIZE);
                        }
                    };
                    self.bottomRight.element.resizable(options);
                    handle = $(".ui-resizable-handle", this.bottomRight.element).css("top", -1 * headerHeight);
                } else {
                    var options = {
                        handles: "e",
                        minWidth: 15,
                        helper: "clone",
                        start: function (event, ui) {
                            createResizer(ui.size, ui.position);
                            self.fireEvent(BI.FixTable.EVENT_TABLE_BEFORE_REGION_RESIZE);
                        },
                        resize: function (e, ui) {
                            resizeResizer(ui.size, ui.position);
                            self.fireEvent(BI.FixTable.EVENT_TABLE_REGION_RESIZE);
                            e.stopPropagation();
                            //return false;
                        },
                        stop: function (e, ui) {
                            stopResizer();
                            if (o.isResizeAdapt) {
                                var increment = ui.size.width - (BI.sum(self.columnLeft) + self.columnLeft.length);
                                o.columnSize[self.columnLeft.length - 1] += increment;
                            } else {
                                self.setRegionColumnSize([ui.size.width, "fill"]);
                            }
                            self._resize();
                            self.fireEvent(BI.FixTable.EVENT_TABLE_AFTER_REGION_RESIZE);
                        }
                    };
                    self.bottomLeft.element.resizable(options);
                    handle = $(".ui-resizable-handle", this.bottomLeft.element).css("top", -1 * headerHeight);
                }
            }
        }

        var regionColumnSize = o.regionColumnSize;
        if (o.freezeCols.length === 0) {
            regionColumnSize = isRight ? ['fill', 0] : [0, 'fill'];
        } else if (o.freezeCols.length >= o.columnSize.length) {
            regionColumnSize = isRight ? [0, 'fill'] : ['fill', 0];
        }
        this.partitions = BI.createWidget(BI.extend({
            element: this.element
        }, BI.LogicFactory.createLogic("table", BI.extend({}, o.logic, {
            rows: 2,
            columns: 2,
            columnSize: regionColumnSize || (isRight ? ['fill', leftWidth] : [leftWidth, 'fill']),
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
            var scrolling, scrollingX;
            var fn = function (event, delta, deltaX, deltaY) {
                var inf = self._getScrollOffsetAndDur(event);
                if (deltaY < 0 || deltaY > 0) {
                    if (scrolling) {
                        scrollElement[0].scrollTop = scrolling;
                    }
                    scrolling = scrollElement[0].scrollTop - delta * inf.offset;
                    var stopPropagation = false;
                    var st = scrollElement[0].scrollTop;
                    scrollElement[0].scrollTop = scrolling;
                    if (scrollElement[0].scrollTop !== st) {
                        stopPropagation = true;
                    }
                    scrollElement[0].scrollTop = st;
                    self._animateScrollTo(scrollElement, scrollElement[0].scrollTop, scrolling, inf.dur, "linear", {
                        onStart: function () {
                        },
                        onUpdate: function (top) {
                            otherElement[0].scrollTop = top;
                            self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, top);
                        },
                        onComplete: function () {
                            self._scrollBounce(scrolling);
                            self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, scrolling);
                            scrolling = null;
                        }
                    });


                    //otherElement[0].scrollTop = scrollTop;
                    //scrollElement[0].scrollTop = scrollTop;
                    //self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, scrollTop);
                    if (stopPropagation === true) {
                        event.stopPropagation();
                        return false;
                    }
                    return;
                }
                //if (deltaX < 0 || deltaX > 0) {
                //    if (scrollingX) {
                //        scrollElement[0].scrollLeft = scrollingX;
                //    }
                //    scrollingX = scrollElement[0].scrollLeft + delta * inf.offset;
                //    var stopPropagation = false;
                //    var sl = scrollElement[0].scrollLeft;
                //    scrollElement[0].scrollLeft = scrollingX;
                //    if (scrollElement[0].scrollLeft !== sl) {
                //        stopPropagation = true;
                //    }
                //    scrollElement[0].scrollLeft = sl;
                //    self._animateScrollTo(scrollElement, scrollElement[0].scrollLeft, scrollingX, inf.dur, "linear", {
                //        direction: "left",
                //        onStart: function () {
                //        },
                //        onUpdate: function (left) {
                //            scrollTopElement[0].scrollLeft = left;
                //            self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, null, left);
                //        },
                //        onComplete: function () {
                //            self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, null, scrollingX);
                //            scrollingX = null;
                //        }
                //    });
                //
                //
                //    //otherElement[0].scrollTop = scrollTop;
                //    //scrollElement[0].scrollTop = scrollTop;
                //    //self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, scrollTop);
                //    if (stopPropagation === true) {
                //        event.stopPropagation();
                //        return false;
                //    }
                //}
            };
            scrollElement.mousewheel(fn);
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
                // self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL);
                if (change === true) {
                    e.stopPropagation();
                    //return false;
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
                self.scrollTopLeft.element[0].scrollLeft = self.scrollBottomLeft.element[0].scrollLeft;
                self.scrollTopRight.element[0].scrollLeft = self.scrollBottomRight.element[0].scrollLeft;
                self.scrollBottomLeft.element[0].scrollTop = self.scrollBottomRight.element[0].scrollTop;
                //调整拖拽handle的高度
                if (o.isNeedResize) {
                    handle && handle.css("height", self.bottomLeft.element.height() + headerHeight);
                }
            }
        };

        BI.nextTick(function () {
            if (self.element.is(":visible")) {
                self._resize();
                self.fireEvent(BI.FixTable.EVENT_TABLE_AFTER_INIT);
            }
        });
        BI.Resizers.add(this.getName(), function (e) {
            if (BI.isWindow(e.target) && self.element.is(":visible")) {
                self._resize();
                self.fireEvent(BI.FixTable.EVENT_TABLE_RESIZE);
            }
        });
    },

    _scroll: function (scrollTop) {
        if (this._isNeedFix) {
            var self = this, o = this.options;
            var pos = this._helper.scrollTo(scrollTop);
            this._rowBuffer.getRows(pos.index || 0, pos.offset || 0);
            this.bottomLeftBody.element.html(this._createBodyCells(this.bottomLeftItems, this.columnLeft, this.mergeLeft, this.bottomLeftBodyTds, this.bottomLeftBodyItems, 0, null, pos.index, pos.offset));
            this.bottomRightBody.element.html(this._createBodyCells(this.bottomRightItems, this.columnRight, this.mergeRight, this.bottomRightBodyTds, this.bottomRightBodyItems, this.columnLeft.length, null, pos.index, pos.offset));
            o.afterScroll();
        }
    },

    _scrollBounce: function (top) {
        if (this._isNeedFix) {
            var self = this, o = this.options;
            if (!this.__scrollBounce) {
                this.__scrollBounce = BI.debounce(BI.bind(this._scroll, this), 100);
            }
            this.__scrollBounce(top);
        }
    },

    resize: function () {
        this._initScroller();
        this._resize && this._resize();
    },

    _createBodyCells: function (items, columnSize, mergeCols, TDs, Ws, start, rowSize) {
        if (!this._isNeedFix) {
            return this._createCells.apply(this, arguments);
        }
        var self = this, o = this.options, preCol = {}, preRow = {}, preRW = {}, preCW = {}, map = {};
        columnSize = columnSize || o.columnSize;
        mergeCols = mergeCols || o.mergeCols;
        TDs = TDs || {};
        Ws = Ws || {};
        start = start || 0;
        rowSize || (rowSize = o.rowSize);
        var frag = document.createDocumentFragment();
        var rows = BI.sortBy(this._rowBuffer.getRowsWithUpdatedBuffer());
        if (rows[0] > 0) {
            frag.appendChild($("<tr>").height((o.rowSize + 1) * rows[0])[0]);
        }
        BI.each(rows, function (pos, i) {
            var tr = $("<tr>").addClass((i & 1) === 0 ? "odd" : "even");
            BI.each(items[i], function (j, row) {
                if (!map[i]) {
                    map[i] = {};
                }
                if (!TDs[i]) {
                    TDs[i] = {};
                }
                if (!Ws[i]) {
                    Ws[i] = {};
                }
                map[i][j] = row;

                createOneEl(i, j);
            });

            function createOneEl(r, c) {
                var width = self._calculateWidth(columnSize[c]);
                if (width > 1.05 && c === columnSize.length - 1) {
                    width--;
                }
                var height = self._calculateHeight(rowSize);
                if (!TDs[r][c]) {
                    var td = $("<td>").attr("height", height)
                        .attr("width", width).css({"width": width, "height": height, "position": "relative"})
                        .addClass((c & 1) === 0 ? "odd-col" : "even-col")
                        .addClass(r === 0 ? "first-row" : "")
                        .addClass(c === 0 ? "first-col" : "")
                        .addClass(c === rows.length - 1 ? "last-col" : "");
                    var w = BI.createWidget(map[r][c], {
                        type: "bi.table_cell",
                        textAlign: "left",
                        width: BI.isNumeric(width) ? width : "",
                        height: BI.isNumeric(height) ? height : "",
                        _row: r,
                        _col: c + start
                    });
                    w.element.css("position", "relative");
                    td.append(w.element);
                } else {
                    var td = TDs[r][c];
                    var w = Ws[r][c];
                }
                tr.append(td);
                preCol[c] = td;
                preCol[c].__mergeRows = [r];
                preCW[c] = w;
                preRow[r] = td;
                preRow[r].__mergeCols = [c];
                preRW[r] = w;
                TDs[r][c] = td;
                Ws[r][c] = w;
            }

            frag.appendChild(tr[0]);
        });

        if (rows[rows.length - 1] < items.length - 1) {
            frag.appendChild($("<tr>").height((o.rowSize + 1) * (items.length - rows[rows.length - 1] - 1))[0]);
        }
        return frag;
    },

    _createNormalFixTable: function () {
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
            cls: "scroll-bottom-right",
            scrollable: true,
            items: [this.tableContainer]
        });

        this.bottomRight = BI.createWidget({
            type: "bi.adaptive",
            cls: "bottom-right",
            element: this.element,
            scrollable: false,
            items: [this.scrollContainer]
        });
        var scrolling, scrollX;
        this.scrollContainer.element.mousewheel(function (event, delta, deltaX, deltaY) {
            var inf = self._getScrollOffsetAndDur(event);
            if (deltaY < 0 || deltaY > 0) {
                var ele = self.scrollContainer.element;
                if (scrolling) {
                    ele[0].scrollTop = scrolling;
                }

                scrolling = ele[0].scrollTop - delta * inf.offset;
                var stopPropagation = false;
                var st = ele[0].scrollTop;
                ele[0].scrollTop = scrolling;
                if (ele[0].scrollTop !== st) {
                    stopPropagation = true;
                }
                ele[0].scrollTop = st;
                self._animateScrollTo(ele, ele[0].scrollTop, scrolling, inf.dur, "linear", {
                    onStart: function () {
                    },
                    onUpdate: function (top) {
                        self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, top);
                    },
                    onComplete: function () {
                        self._scroll(scrolling);
                        self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, scrolling);
                        scrolling = null;
                    }
                });
                //var scrollTop = self.scrollContainer.element[0].scrollTop = self.scrollContainer.element[0].scrollTop - delta * offset;
                //self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, scrollTop);
                if (stopPropagation === true) {
                    event.stopPropagation();
                    return false;
                }
            }
        });

        var scrollTop = 0, scrollLeft = 0;
        this.scrollContainer.element.scroll(function (e) {
            var change = false;
            var scrollElement = self.scrollContainer.element;
            if (scrollElement.scrollTop() != scrollTop) {
                if (Math.abs(scrollElement.scrollTop() - scrollTop) > 0.1) {
                    e.stopPropagation();
                    change = true;
                }
                scrollTop = scrollElement.scrollTop();
            }
            if (scrollElement.scrollLeft() != scrollLeft) {
                if (Math.abs(scrollElement.scrollLeft() - scrollLeft) > 0.1) {
                    e.stopPropagation();
                    change = true;
                }
                scrollLeft = scrollElement.scrollLeft();
            }
            self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL);
            if (change === true) {
                e.stopPropagation();
                //return false;
            }
            return false;
        });
        this._resize = function () {
            if (self.element.is(":visible")) {
                self.setColumnSize(o.columnSize);
            }
        };
        BI.Resizers.add(this.getName(), function (e) {
            if (self.element.is(":visible") && BI.isWindow(e.target)) {
                self._resize();
                self.fireEvent(BI.FixTable.EVENT_TABLE_RESIZE);
            }
        });
        BI.nextTick(function () {
            if (self.element.is(":visible")) {
                self.fireEvent(BI.FixTable.EVENT_TABLE_AFTER_INIT);
            }
        });
    },

    _init: function () {
        BI.FixTable.superclass._init.apply(this, arguments);
    },

    setVerticalScroll: function (scrollTop) {
        var o = this.options;
        if (o.isNeedFreeze) {
            if (this.scrollBottomRight.element[0].scrollTop !== scrollTop) {
                this.scrollBottomRight.element[0].scrollTop = scrollTop;
                this._scrollBounce(scrollTop);
            }
            if (this.scrollBottomLeft.element[0].scrollTop !== scrollTop) {
                this.scrollBottomLeft.element[0].scrollTop = scrollTop;
                this._scrollBounce(scrollTop);
            }
        } else {
            if (this.scrollContainer.element[0].scrollTop !== scrollTop) {
                this.scrollContainer.element[0].scrollTop = scrollTop;
            }
        }
    },

    _initScroller: function () {
        if (this._isNeedFix) {
            var o = this.options;
            var viewHeight = this.bottomRight && this.bottomRight.element.height();
            this._helper = new BI.TableScrollHelper(o.items.length, o.rowSize + 1, viewHeight || 0);
            this._rowBuffer = new BI.TableRowBuffer(o.items.length, o.rowSize + 1, viewHeight || 0, BI.bind(this._helper.getRowPosition, this._helper));
        }
    },

    populate: function (items, header) {
        var self = this, o = this.options;
        this.options.items = items || [];
        if (header) {
            this.options.header = header;
        }
        this.empty();
        if (o.items[0] && o.items[0].length * o.items.length > 1000) {
            this._isNeedFix = true;
        } else {
            this._isNeedFix = false;
        }
        this._initScroller();
        if (this.options.isNeedFreeze) {
            this._createFreezeFixTable();
            this._scrollBounce(0);
        } else {
            this._createNormalFixTable();
        }
    },

    destroy: function () {
        BI.FixTable.superclass.destroy.apply(this, arguments);
    }
})
;
BI.FixTable.EVENT_TABLE_AFTER_INIT = "EVENT_TABLE_AFTER_INIT";
BI.FixTable.EVENT_TABLE_RESIZE = "EVENT_TABLE_RESIZE";
BI.FixTable.EVENT_TABLE_SCROLL = "EVENT_TABLE_SCROLL";
BI.FixTable.EVENT_TABLE_BEFORE_COLUMN_RESIZE = "EVENT_TABLE_BEFORE_COLUMN_RESIZE";
BI.FixTable.EVENT_TABLE_COLUMN_RESIZE = "EVENT_TABLE_COLUMN_RESIZE";
BI.FixTable.EVENT_TABLE_AFTER_COLUMN_RESIZE = "EVENT_TABLE_AFTER_COLUMN_RESIZE";

BI.FixTable.EVENT_TABLE_BEFORE_REGION_RESIZE = "EVENT_TABLE_BEFORE_REGION_RESIZE";
BI.FixTable.EVENT_TABLE_REGION_RESIZE = "EVENT_TABLE_REGION_RESIZE";
BI.FixTable.EVENT_TABLE_AFTER_REGION_RESIZE = "EVENT_TABLE_AFTER_REGION_RESIZE";
$.shortcut("bi.fix_table", BI.FixTable);
