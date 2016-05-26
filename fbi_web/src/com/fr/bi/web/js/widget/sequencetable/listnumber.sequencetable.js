/**
 *
 * Created by GUY on 2016/5/26.
 * @class BI.SequenceTableListNumber
 * @extends BI.Widget
 */
BI.SequenceTableListNumber = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.SequenceTableListNumber.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-sequence-table-list-number",
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
        BI.SequenceTableListNumber.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var header = BI.createWidget({
            type: "bi.label",
            cls: "sequence-table-title",
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
        this._populate();
    },

    _formatItems: function () {
        var o = this.options;
        var result = [];
        if (o.isNeedFreeze === false) {
            result.push({
                type: "bi.label",
                cls: "sequence-table-title",
                forceCenter: true,
                height: o.header.length * (o.headerRowSize || o.rowSize),
                text: BI.i18nText("BI-Number_Index"),
                hgap: 5
            })
        }
        BI.count(o.startSequence, o.startSequence + o.items.length, function (num) {
            result.push({
                type: "bi.label",
                height: o.rowSize,
                hgap: 5,
                text: num,
                cls: "sequence-table-number" + (num === o.startSequence + o.items.length - 1 ? " last" : "")
            })
        });
        return result;
    },

    _layout: function () {
        var self = this, o = this.options;
        var headerHeight = o.headerRowSize * o.header.length;
        var items = this.layout.attr("items");
        if (o.isNeedFreeze === false) {
            items[0].height = 0;
            items[1].height = 0;
        } else if (o.isNeedFreeze === true) {
            items[0].height = headerHeight + 1;
            items[1].height = 1;
        }
        this.layout.attr("items", items);
        this.layout.resize();
    },

    _populate: function () {
        this._layout();
        this.buttonGroup.populate(this._formatItems());
        this.setVerticalScroll(0);
    },

    setVerticalScroll: function (scroll) {
        this.buttonGroup.element.scrollTop(scroll);
    },

    populate: function (items, header, startSequence) {
        var o = this.options;
        if (items) {
            o.items = items;
        }
        if (header) {
            o.header = header;
        }
        if (BI.isNotNull(startSequence)) {
            o.startSequence = startSequence;
        }
        this._populate();
    }
});
$.shortcut('bi.sequence_table_list_number', BI.SequenceTableListNumber);