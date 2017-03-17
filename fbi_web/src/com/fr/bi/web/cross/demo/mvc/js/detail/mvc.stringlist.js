/**
 * Created by zcf on 2016/12/14.
 */
StringListView = BI.inherit(BI.View, {

    _defaultConfig: function () {
        return BI.extend(StringListView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: ""
        })
    },

    _init: function () {
        StringListView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var self = this, o = this.options;

        var button = BI.createWidget({
            type: "bi.button",
            text: "populate",
            height: 30,
            width: 30,
            handler:function () {

            }
        });
        var trigger = BI.createWidget({
            type: "bi.multi_string_list",
            itemsCreator: BI.bind(this._itemsCreator, this),
            width: o.width,
            height: o.height
        });

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [button, trigger]
        })
    },

    _itemsCreator: function (options, callback) {
        callback({
            items: [{
                text: "aaa",
                value: "aaa",
                title: "aaa"
            }, {
                text: "bbb",
                value: "bbb",
                title: "bbb"
            }, {
                text: "ccc",
                value: "ccc",
                title: "ccc"
            }, {
                text: "ddd",
                value: "ddd",
                title: "ddd"
            }, {
                text: "eee",
                value: "eee",
                title: "eee"
            }],
            hasNext: true
        })
    }
});
StringListModel = BI.inherit(BI.Model, {});