/**
 *
 * Created by GUY on 2016/5/26.
 * @class BI.SequenceTableTreeNumber
 * @extends BI.Widget
 */
BI.SequenceTableTreeNumber = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.SequenceTableTreeNumber.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-sequence-table-tree-number",
            isNeedFreeze: false,
            startSequence: 1,//开始的序号
            scrollTop: 0,
            headerRowSize: 25,
            rowSize: 25,

            header: [],
            items: [], //二维数组

            //交叉表头
            crossHeader: [],
            crossItems: []
        });
    },

    _init: function () {
        BI.SequenceTableTreeNumber.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.vCurr = 1;
        this.hCurr = 1;
        this.renderedCells = [];
        this.renderedKeys = [];

        var header = BI.createWidget({
            type: "bi.label",
            cls: "sequence-table-title",
            textAlign: "left",
            forceCenter: true,
            text: BI.i18nText("BI-Number_Index"),
            hgap: 5
        });
        this.container = BI.createWidget({
            type: "bi.absolute",
            width: 60,
            scrollable: false
        });

        this.scrollContainer = BI.createWidget({
            type: "bi.vertical",
            scrollable: false,
            scrolly: false,
            items: [this.container]
        });

        this.layout = BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: header,
                height: o.headerRowSize * o.header.length
            }, {
                el: this.scrollContainer
            }]
        });
        //缓存第一行对应的序号
        this.start = this.options.startSequence;
        this.cache = {};
        this._nextState();

        this._populate();
    },

    _getStartSequence: function (nodes) {
        var self = this;
        var start = this.start;
        var cnt = this.start;

        function track(node) {
            self.cache[node.text || node.value] = cnt++;
        }

        BI.each(nodes, function (i, node) {
            if (BI.isNotEmptyArray(node.children)) {
                BI.each(node.children, function (index, child) {
                    if (index === 0) {
                        if (self.cache[child.text || child.value]) {
                            start = cnt = self.cache[child.text || child.value];
                        }
                    }
                    track(child)
                });
            }
        });
        this.start = cnt;
        return start;
    },

    _formatNumber: function (nodes) {
        var self = this, o = this.options;
        var result = [];
        var count = this._getStartSequence(nodes);

        function getLeafCount(node) {
            var cnt = 0;
            if (BI.isNotEmptyArray(node.children)) {
                BI.each(node.children, function (index, child) {
                    cnt += getLeafCount(child);
                });
                if (/**node.children.length > 1 && **/BI.isNotEmptyArray(node.values)) {
                    cnt++;
                }
            } else {
                cnt++;
            }
            return cnt;
        }

        var start = 0, top = 0;
        BI.each(nodes, function (i, node) {
            if (BI.isArray(node.children)) {
                BI.each(node.children, function (index, child) {
                    var cnt = getLeafCount(child);
                    result.push({
                        text: count++,
                        start: start,
                        top: top,
                        cnt: cnt,
                        cls: "sequence-table-number",
                        height: cnt * o.rowSize
                    });
                    start += cnt;
                    top += cnt * o.rowSize;
                });
                if (BI.isNotEmptyArray(node.values)) {
                    result.push({
                        text: BI.i18nText("BI-Summary_Values"),
                        start: start++,
                        top: top,
                        cnt: 1,
                        cls: "sequence-table-number sequence-table-summary",
                        height: o.rowSize
                    });
                    top += o.rowSize;
                }
            }
        });
        return result;
    },

    _layout: function () {
        var self = this, o = this.options;
        var headerHeight = this._getHeaderHeight();
        var items = this.layout.attr("items");
        if (o.isNeedFreeze === false) {
            items[0].height = 0;
        } else if (o.isNeedFreeze === true) {
            items[0].height = headerHeight;
        }
        this.layout.attr("items", items);
        this.layout.resize();
    },

    _getHeaderHeight: function () {
        var o = this.options;
        return o.headerRowSize * (o.crossHeader.length + (o.header.length > 0 ? 1 : 0));
    },

    _nextState: function () {
        var o = this.options;
        this.numbers = this._formatNumber(o.items);
        var intervalTree = BI.PrefixIntervalTree.uniform(this.numbers.length, 0);
        BI.each(this.numbers, function (i, number) {
            intervalTree.set(i, number.height);
        });
        this.intervalTree = intervalTree;
    },

    _prevState: function () {
        var self = this, o = this.options;
        var firstChild;
        BI.some(o.items, function (i, node) {
            if (BI.isNotEmptyArray(node.children)) {
                return BI.some(node.children, function (j, child) {
                    firstChild = child;
                    return true;
                });
            }
        });
        if (firstChild) {
            this.start = this.cache[firstChild.text || firstChild.value];
        } else {
            this.start = 1;
        }
        this._nextState();
    },

    _calculateChildrenToRender: function () {
        var self = this, o = this.options;

        var renderedCells = [], renderedKeys = [];
        let index = this.intervalTree.greatestLowerBound(o.scrollTop);
        let offsetTop = -(o.scrollTop - (index > 0 ? this.intervalTree.sumTo(index - 1) : 0));
        let height = offsetTop;
        var bodyHeight = o.height - this._getHeaderHeight();
        while (height < bodyHeight && index < this.numbers.length) {
            renderedKeys.push(index);
            offsetTop += this.numbers[index].height;
            height += this.numbers[index].height;
            index++;
        }

        BI.each(renderedKeys, function (i, index) {
            var contains = BI.deepContains(self.renderedKeys, index);
            if (contains === true) {
                if (self.numbers[index].height !== self.renderedCells[index]._height) {
                    self.renderedCells[index]._height = o.rowSize;
                    self.renderedCells[index].el.setHeight(self.numbers[index].height);
                }
                if (self.numbers[index].top !== self.renderedCells[index].top) {
                    self.renderedCells[index].top = self.numbers[index].top;
                    self.renderedCells[index].el.element.css("top", self.numbers[index].top + "px");
                }
                renderedCells.push(self.renderedCells[index]);
            } else {
                var child = BI.createWidget(BI.extend({
                    type: "bi.label",
                    width: 60,
                    textAlign: "left",
                    hgap: 5
                }, self.numbers[index]));
                renderedCells.push({
                    el: child,
                    left: 0,
                    top: self.numbers[index].top,
                    _height: self.numbers[index].height
                });
            }
        });

        //已存在的， 需要添加的和需要删除的
        var existSet = {}, addSet = {}, deleteArray = [];
        BI.each(renderedKeys, function (i, key) {
            if (BI.deepContains(self.renderedKeys, key)) {
                existSet[i] = key;
            } else {
                addSet[i] = key;
            }
        });
        BI.each(this.renderedKeys, function (i, key) {
            if (BI.deepContains(existSet, key)) {
                return;
            }
            if (BI.deepContains(addSet, key)) {
                return;
            }
            deleteArray.push(i);
        });
        BI.each(deleteArray, function (i, index) {
            self.renderedCells[index].el.destroy();
        });
        var addedItems = [];
        BI.each(addSet, function (index) {
            addedItems.push(renderedCells[index])
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.container,
            items: addedItems
        });
        this.renderedCells = renderedCells;
        this.renderedKeys = renderedKeys;

        this.container.setHeight(this.intervalTree.sumUntil(this.numbers.length))
    },

    _restore: function () {
        this.vCurr = 1;
        this.start = this.options.startSequence;
        this.cache = {};
    },

    _populate: function () {
        this._layout();
        this._calculateChildrenToRender();
    },

    setVerticalScroll: function (scrollTop) {
        if (this.options.scrollTop !== scrollTop) {
            this.options.scrollTop = scrollTop;
            this.scrollContainer.element.scrollTop(scrollTop);
        }
    },

    getVerticalScroll: function () {
        return this.options.scrollTop;
    },

    setVPage: function (v) {
        if (v <= 1) {
            this._clear();
        } else if (v === this.vCurr + 1) {
            this._nextState();
        } else if (v === this.vCurr - 1) {
            this._prevState();
        }
        this.vCurr = v;
    },

    setHPage: function (v) {
        if (v !== this.hCurr) {
            this._prevState();
        }
        this.hCurr = v;
    },

    restore: function () {
        this._restore();
        this.options.scrollTop = 0;
    },

    populate: function (items, header, crossItems, crossHeader) {
        var o = this.options;
        if (items) {
            o.items = items;
        }
        if (header) {
            o.header = header;
        }
        if (crossItems) {
            o.crossItems = crossItems;
        }
        if (crossHeader) {
            o.crossHeader = crossHeader;
        }
        this._populate();
    }
});
$.shortcut('bi.sequence_table_tree_number', BI.SequenceTableTreeNumber);