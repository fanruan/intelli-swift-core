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
            headerRowSize: 37,
            footerRowSize: 37,
            rowSize: 30,

            header: [],
            footer: false,
            items: [], //二维数组

            //交叉表头
            crossHeader: [],
            crossItems: []
        });
    },

    _init: function () {
        BI.SequenceTableTreeNumber.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var header = BI.createWidget({
            type: "bi.label",
            cls: "sequence-table-title",
            textAlign: "left",
            forceCenter: true,
            text: BI.i18nText("BI-Number_Index"),
            hgap: 5
        });
        this.buttonGroup = BI.createWidget({
            type: "bi.button_group",
            layouts: [{
                type: "bi.vertical",
                scrolly: false,
                scrollx: false,
                scrollable: false
            }]
        });

        this.layout = BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: header,
                height: o.headerRowSize + 1
            }, {
                el: {type: "bi.layout"},
                height: 0
            }, {
                el: this.buttonGroup
            }]
        });
        //缓存第一行对应的序号
        this.start = this.options.startSequence;
        this.cache = {};
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
                if (BI.isNotEmptyArray(node.values)) {
                    cnt++;
                }
            } else {
                cnt++;
            }
            return cnt;
        }

        var start = 0;
        BI.each(nodes, function (i, node) {
            if (BI.isNotEmptyArray(node.children)) {
                BI.each(node.children, function (index, child) {
                    var cnt = getLeafCount(child);
                    result.push({
                        text: count++,
                        start: start,
                        cnt: cnt,
                        cls: "sequence-table-number",
                        height: cnt * o.rowSize + (cnt - 1)
                    });
                    start += cnt;
                });
                if (BI.isNotEmptyArray(node.values)) {
                    result.push({
                        text: BI.i18nText("BI-Summary_Values"),
                        start: start++,
                        cnt: 1,
                        cls: "sequence-table-number sequence-table-summary",
                        height: o.rowSize
                    });
                }
            }
        });
        return result;
    },

    _formatItems: function () {
        var o = this.options;
        var result = [];
        if (o.isNeedFreeze === false) {
            result.push({
                type: "bi.label",
                cls: "sequence-table-title",
                textAlign: "left",
                forceCenter: true,
                height: (o.crossHeader.length + 1) * (o.headerRowSize || o.rowSize),
                text: BI.i18nText("BI-Number_Index"),
                hgap: 5
            })
        }
        var numbers = this._formatNumber(o.items);
        result = result.concat(BI.map(numbers, function (i, num) {
            var cls = num.cls;
            if (BI.isOdd(num.start)) {
                cls += " even";
            } else {
                cls += " odd";
            }
            return BI.extend(num, {
                type: "bi.label",
                textAlign: "left",
                hgap: 5,
                cls: cls + (i === numbers.length - 1 ? " last" : "")
            })
        }));

        return result;
    },

    _layout: function () {
        var self = this, o = this.options;
        var headerHeight = o.headerRowSize * (o.crossHeader.length + 1);
        var items = this.layout.attr("items");
        if (o.isNeedFreeze === false) {
            items[0].height = 0;
            items[1].height = 0;
        } else if (o.isNeedFreeze === true) {
            items[0].height = headerHeight + 1 + o.crossHeader.length;
            items[1].height = 1;
        }
        this.layout.attr("items", items);
        this.layout.resize();
    },

    _populate: function () {
        this._layout();
        this.buttonGroup.populate(this._formatItems());
    },

    setVerticalScroll: function (scroll) {
        this.buttonGroup.element.scrollTop(scroll);
    },

    setVPage: function (v) {
        if (v <= 1) {
            this.cache = {};
            this.start = this.options.startSequence;
        }
    },

    populate: function (items, header, crossItems, crossHeader, startSequence) {
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
        if (BI.isNotNull(startSequence)) {
            o.startSequence = startSequence;
        }
        this._populate();
    }
});
$.shortcut('bi.sequence_table_tree_number', BI.SequenceTableTreeNumber);