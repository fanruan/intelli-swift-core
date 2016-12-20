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

    _createBottomLeftBody: function () {
        var body = this.bottomLeftBody = this._body();
        if (!this._isNeedFix) {
            body.element.append(this._createCells(this.bottomLeftItems, this.columnLeft, this.mergeLeft, this.bottomLeftBodyTds, this.bottomLeftBodyItems));
        }
        return body;
    },

    _createBottomRightBody: function () {
        var body = this.bottomRightBody = this._body();
        if (!this._isNeedFix) {
            body.element.append(this._createCells(this.bottomRightItems, this.columnRight, this.mergeRight, this.bottomRightBodyTds, this.bottomRightBodyItems, this.columnLeft.length));
        }
        return body;
    },

    _createBody: function () {
        var self = this, o = this.options;
        this.body = this._body();
        if (!this._isNeedFix) {
            this.body.element.append(this._createCells(o.items, null, null, this.bodyTds, this.bodyItems));
        }
        return this.body;
    },

    _initFreezeScroll: function () {
        var self = this;
        scroll(this.scrollBottomRight.element, this.scrollTopRight.element, this.scrollBottomLeft.element);
        scroll(this.scrollBottomLeft.element, this.scrollTopLeft.element, this.scrollBottomRight.element);

        function scroll(scrollElement, scrollTopElement, otherElement) {
            var scrolling, scrollingX;
            var fn = function (event, delta, deltaX, deltaY) {
                var inf = self._getScrollOffsetAndDur(event);
                if (deltaY < 0 || deltaY > 0) {
                    if (!self._isNeedFix && scrolling) {
                        scrollElement[0].scrollTop = scrolling;
                    }
                    scrolling = scrollElement[0].scrollTop - delta * inf.offset;
                    if (Math.abs(scrollElement[0].scrollTop - scrolling) < 0.1) {
                        return;
                    }
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

                    if (stopPropagation === true) {
                        event.stopPropagation();
                        return false;
                    }
                    return;
                }
            };
            scrollElement.mousewheel(fn);
        }
    },

    _initNormalScroll: function () {
        var self = this;
        var scrolling, scrollX;
        this.scrollBottomRight.element.mousewheel(function (event, delta, deltaX, deltaY) {
            var inf = self._getScrollOffsetAndDur(event);
            if (deltaY < 0 || deltaY > 0) {
                var ele = self.scrollBottomRight.element;
                if (!self._isNeedFix && scrolling) {
                    ele[0].scrollTop = scrolling;
                }

                scrolling = ele[0].scrollTop - delta * inf.offset;
                if (Math.abs(ele[0].scrollTop - scrolling) < 0.1) {
                    return;
                }
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
                        self._scrollBounce(scrolling);
                        self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, scrolling);
                        scrolling = null;
                    }
                });
                //var scrollTop = self.scrollBottomRight.element[0].scrollTop = self.scrollBottomRight.element[0].scrollTop - delta * offset;
                //self.fireEvent(BI.Table.EVENT_TABLE_SCROLL, scrollTop);
                if (stopPropagation === true) {
                    event.stopPropagation();
                    return false;
                }
            }
        });
    },

    _scroll: function (scrollTop) {
        var self = this, o = this.options;
        if (this._isNeedFix) {
            var pos = this._helper.scrollTo(scrollTop);
            this._rowBuffer.getRows(pos.index || 0, pos.offset || 0);
            if (o.isNeedFreeze === true) {
                this.bottomLeftBody.element.html(this._createBodyCells(this.bottomLeftItems, this.columnLeft, this.mergeLeft, this.bottomLeftBodyTds, this.bottomLeftBodyItems, 0, null, pos.index, pos.offset));
                this.bottomRightBody.element.html(this._createBodyCells(this.bottomRightItems, this.columnRight, this.mergeRight, this.bottomRightBodyTds, this.bottomRightBodyItems, this.columnLeft.length, null, pos.index, pos.offset));
                o.afterScroll();
            }
            if (o.isNeedFreeze === false) {
                this.body.element.html(this._createBodyCells(o.items, o.columnSize, o.mergeCols, this.bodyTds, this.bodyItems, 0, o.rowSize, pos.index, pos.offset));
            }
        }
    },

    _scrollBounce: function (top) {
        if (this._isNeedFix) {
            var self = this, o = this.options;
            if (!this.__scrollBounce) {
                this.__scrollBounce = BI.debounce(BI.bind(this._scroll, this), 30);
            }
            this.__scrollBounce(top);
        }
    },

    resize: function () {
        var self = this;
        this._resize && this._resize();
        if (this._isNeedFix) {
            BI.nextTick(function () {
                self._initScroller();
                self._scroll(0);
            });
        }
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
            if (this.scrollBottomRight.element[0].scrollTop !== scrollTop) {
                this.scrollBottomRight.element[0].scrollTop = scrollTop;
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
            this._createFreezeTable();
        } else {
            this._createNormalTable();
        }
        BI.nextTick(function () {
            self._scroll(0);
        });
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
