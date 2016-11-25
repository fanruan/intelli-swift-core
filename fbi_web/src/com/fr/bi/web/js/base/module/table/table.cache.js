/**
 *
 * 表格
 *
 * Created by GUY on 2016/11/24.
 * @class BI.CacheTable
 * @extends BI.Table
 */
BI.CacheTable = BI.inherit(BI.Table, {

    _defaultConfig: function () {
        return BI.extend(BI.CacheTable.superclass._defaultConfig.apply(this, arguments), {
            afterScroll: BI.emptyFn
        })
    },

    _createBottomLeftBody: function () {
        var body = this.bottomLeftBody = this._body();
        if (!this._isNeedCache) {
            body.element.append(this._createCells(this.bottomLeftItems, this.columnLeft, this.mergeLeft, this.bottomLeftBodyTds, this.bottomLeftBodyItems));
        }
        return body;
    },

    _createBottomRightBody: function () {
        var body = this.bottomRightBody = this._body();
        if (!this._isNeedCache) {
            body.element.append(this._createCells(this.bottomRightItems, this.columnRight, this.mergeRight, this.bottomRightBodyTds, this.bottomRightBodyItems, this.columnLeft.length));
        }
        return body;
    },

    _createBody: function () {
        var self = this, o = this.options;
        this.body = this._body();
        if (!this._isNeedCache) {
            this.body.element.append(this._createCells(o.items, null, null, this.bodyTds, this.bodyItems));
        }
        return this.body;
    },

    _scroll: function (scrollTop) {
        console.log(scrollTop);
        if (this._isNeedCache) {
            var self = this, o = this.options;
            var pos = this._helper.scrollTo(scrollTop);
            this._rowBuffer.getRows(pos.index || 0, pos.offset || 0);
            if (o.isNeedFreeze === true) {
                this._createBodyCells(this.bottomLeftItems, this.columnLeft, this.mergeLeft, this.bottomLeftBodyTds, this.bottomLeftBodyItems, 0, null, pos.index, pos.offset, {
                    parent: this.bottomLeftBody,
                    cached: this.cachedLeft
                });
                this._createBodyCells(this.bottomRightItems, this.columnRight, this.mergeRight, this.bottomRightBodyTds, this.bottomRightBodyItems, this.columnLeft.length, null, pos.index, pos.offset, {
                    parent: this.bottomRightBody,
                    cached: this.cachedRight
                });
                o.afterScroll();
            }
            if (o.isNeedFreeze === false) {
                this._createBodyCells(o.items, o.columnSize, o.mergeCols, this.bodyTds, this.bodyItems, 0, o.rowSize, pos.index, pos.offset, {
                    parent: this.body,
                    cached: this.cached
                });
                o.afterScroll();
            }
        }
    },

    resize: function () {
        var self = this;
        BI.nextTick(function () {
            self._initScroller();
            self._scroll(0);
            self._resize && self._resize();
        });
    },

    _initFreezeScroll: function () {
        var self = this;
        scroll(this.scrollBottomRight.element, this.scrollTopRight.element, this.scrollBottomLeft.element);
        scroll(this.scrollBottomLeft.element, this.scrollTopLeft.element, this.scrollBottomRight.element);

        var scrollDebounce = BI.debounce(function (scrollElement, scrollTopElement, otherElement, scroll) {
            scrollElement[0].scrollTop = scroll;
            otherElement[0].scrollTop = scroll;
            self._scroll(scrollElement[0].scrollTop);
            self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, scrollElement[0].scrollTop);
        }, 0);

        function scroll(scrollElement, scrollTopElement, otherElement) {
            var scrolling, scrollingX;
            var fn = function (event, delta, deltaX, deltaY) {
                var inf = self._getScrollOffsetAndDur(event);
                if (deltaY < 0 || deltaY > 0) {
                    // if (scrolling) {
                    //     scrollElement[0].scrollTop = scrolling;
                    // }
                    if (Math.abs(scrolling - (scrollElement[0].scrollTop - delta * inf.offset)) < 0.1) {
                        return;
                    }
                    scrolling = scrollElement[0].scrollTop - delta * inf.offset;
                    var stopPropagation = false;
                    var st = scrollElement[0].scrollTop;
                    scrollElement[0].scrollTop = scrolling;
                    if (scrollElement[0].scrollTop !== st) {
                        stopPropagation = true;
                    }
                    scrollElement[0].scrollTop = st;
                    // otherElement[0].scrollTop = scrolling;
                    scrollDebounce(scrollElement, scrollTopElement, otherElement, scrolling);
                    // self._scroll(scrollElement[0].scrollTop);
                    // self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, scrollElement[0].scrollTop);

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
        var scrollDebounce = BI.debounce(function (scroll) {
            var ele = self.scrollBottomRight.element;
            ele[0].scrollTop = scroll;
            self._scroll(ele[0].scrollTop);
            self.fireEvent(BI.FixTable.EVENT_TABLE_SCROLL, ele[0].scrollTop);
        }, 0);
        var scrolling, scrollX;
        this.scrollBottomRight.element.mousewheel(function (event, delta, deltaX, deltaY) {
            var inf = self._getScrollOffsetAndDur(event);
            if (deltaY < 0 || deltaY > 0) {
                var ele = self.scrollBottomRight.element;
                // if (scrolling) {
                //     ele[0].scrollTop = scrolling;
                // }

                scrolling = ele[0].scrollTop - delta * inf.offset;
                var stopPropagation = false;
                var st = ele[0].scrollTop;
                ele[0].scrollTop = scrolling;
                if (ele[0].scrollTop !== st) {
                    stopPropagation = true;
                }
                scrollDebounce(scrolling);
                if (stopPropagation === true) {
                    event.stopPropagation();
                    return false;
                }
            }
        });
    },

    _createBodyCells: function (items, columnSize, mergeCols, TDs, Ws, start, rowSize, index, offset, context) {
        if (!this._isNeedCache) {
            return this._createCells.apply(this, arguments);
        }
        var self = this, o = this.options, preCol = {}, preRow = {}, preRW = {}, preCW = {}, map = {};
        columnSize = columnSize || o.columnSize;
        mergeCols = mergeCols || o.mergeCols;
        TDs = TDs || {};
        Ws = Ws || {};
        start = start || 0;
        rowSize || (rowSize = o.rowSize);
        var rows = BI.sortBy(this._rowBuffer.getRowsWithUpdatedBuffer());
        BI.DOM.hang(context.cached);
        context.parent.element.empty();
        if (rows[0] > 0) {
            $("<tr>").height((o.rowSize + 1) * (rows[0] || 0)).appendTo(context.parent.element);
        }
        BI.each(rows, function (pos, i) {
            if (!context.cached[i]) {
                var tr = context.cached[i] = $("<tr>").addClass((i & 1) === 0 ? "odd" : "even");
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
                });
            }
            context.cached[i].appendTo(context.parent.element);
        });

        if (rows[rows.length - 1] < items.length - 1) {
            $("<tr>").height((o.rowSize + 1) * (items.length - rows[rows.length - 1] - 1)).appendTo(context.parent.element);
        }
    },

    _init: function () {
        BI.CacheTable.superclass._init.apply(this, arguments);
        this._initCache();
    },

    _initCache: function () {
        this.cachedLeft = {};
        this.cachedRight = {};
        this.cached = {};
    },

    setVerticalScroll: function (scrollTop) {
        var o = this.options;
        if (o.isNeedFreeze) {
            if (this.scrollBottomRight.element[0].scrollTop !== scrollTop) {
                this.scrollBottomRight.element[0].scrollTop = scrollTop;
                this._scroll(scrollTop);
            }
            if (this.scrollBottomLeft.element[0].scrollTop !== scrollTop) {
                this.scrollBottomLeft.element[0].scrollTop = scrollTop;
                this._scroll(scrollTop);
            }
        } else {
            if (this.scrollBottomRight.element[0].scrollTop !== scrollTop) {
                this.scrollBottomRight.element[0].scrollTop = scrollTop;
            }
        }
    },

    _initScroller: function () {
        if (this._isNeedCache) {
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
            this._isNeedCache = true;
        } else {
            this._isNeedCache = false;
        }
        this._initCache();
        this._initScroller();
        if (this.options.isNeedFreeze) {
            this._createFreezeTable();
            BI.nextTick(function () {
                self._scroll(0);
            });
        } else {
            this._createNormalTable();
        }
    },

    destroy: function () {
        BI.CacheTable.superclass.destroy.apply(this, arguments);
    }
})
;
BI.CacheTable.EVENT_TABLE_AFTER_INIT = "EVENT_TABLE_AFTER_INIT";
BI.CacheTable.EVENT_TABLE_RESIZE = "EVENT_TABLE_RESIZE";
BI.CacheTable.EVENT_TABLE_SCROLL = "EVENT_TABLE_SCROLL";
BI.CacheTable.EVENT_TABLE_BEFORE_COLUMN_RESIZE = "EVENT_TABLE_BEFORE_COLUMN_RESIZE";
BI.CacheTable.EVENT_TABLE_COLUMN_RESIZE = "EVENT_TABLE_COLUMN_RESIZE";
BI.CacheTable.EVENT_TABLE_AFTER_COLUMN_RESIZE = "EVENT_TABLE_AFTER_COLUMN_RESIZE";

BI.CacheTable.EVENT_TABLE_BEFORE_REGION_RESIZE = "EVENT_TABLE_BEFORE_REGION_RESIZE";
BI.CacheTable.EVENT_TABLE_REGION_RESIZE = "EVENT_TABLE_REGION_RESIZE";
BI.CacheTable.EVENT_TABLE_AFTER_REGION_RESIZE = "EVENT_TABLE_AFTER_REGION_RESIZE";
$.shortcut("bi.cache_table", BI.CacheTable);
