/**
 * Created by fay on 2016/10/11.
 */
BI.SelectTreeLabel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectTreeLabel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-tree-label",
            wId: ""
        })
    },

    _init: function () {
        BI.SelectTreeLabel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.treeLabel = BI.createWidget({
            type: "bi.tree_label",
            element: this.element,
            itemsCreator: function (op, callback) {
                if(BI.isEmptyObject(op)) {
                    callback({});
                } else {
                        callback({
                        //     items: [
                        //         {id: 1, value: "南京", text: "南京", pId: null},
                        //         {id: 2, value: "苏州", text: "苏州", pId: null},
                        //         {id: 3, value: "无锡", text: "无锡", pId: null},
                        //         {id: 11, value: "玄武区", text: "玄武区", pId: 1},
                        //         {id: 21, value: "工业园区", text: "工业园区", pId: 2},
                        //         {id: 31, value: "南长区", text: "南长区", pId: 3},
                        //         {id: 12, value: "江宁区", text: "江宁区", pId: 1},
                        //         {id: 111, value: "40", text: "40", pId: 11},
                        //         {id: 112, value: "60", text: "60", pId: 11},
                        //         {id: 211, value: "60", text: "60", pId: 21},
                        //         {id: 121, value: "40", text: "40", pId: 12},
                        //         {id: 1111, value: "70-89", text: "70-89", pId: 111},
                        //         {id: 1112, value: "89-100", text: "89-100", pId: 111},
                        //         {id: 2111, value: "70-89", text: "70-89", pId: 211},
                        //         {id: 2112, value: "89-100", text: "89-100", pId: 211},
                        //         {id: 1121, value: "70-89", text: "70-89", pId: 112},
                        //         {id: 1122, value: "89-100", text: "89-100", pId: 112}
                        //     ],
                        //     titles: [{
                        //         text: "城市"
                        //     },{
                        //         text: "地区"
                        //     },{
                        //         text: "面积"
                        //     },{
                        //         text: "其他"
                        //     }]
                        })
                }
            }
        });

        this.treeLabel.on(BI.TreeLabel.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectTreeLabel.EVENT_CONFIRM, arguments);
        });
    },

    setValue: function (v) {
        var self = this, o = this.options;
        var dimensions = BI.Utils.getAllDimDimensionIDs(o.wId);
        var titles = [];
        BI.each(dimensions, function (idx, dId) {
            var temp = BI.Utils.getDimensionNameByID(dId);
            titles.push({
                text: temp,
                title: temp
            })
        });
        this.treeLabel.populate({
            items: [
                // [{id: 1, value: "南京", text: "南京", pId: null},
                //     {id: 2, value: "苏州", text: "苏州", pId: null},
                //     {id: 3, value: "无锡", text: "无锡", pId: null}],
                // [{id: 11, value: "玄武区", text: "玄武区", pId: 1},
                //     {id: 21, value: "工业园区", text: "工业园区", pId: 2},
                //     {id: 31, value: "南长区", text: "南长区", pId: 3},
                //     {id: 12, value: "江宁区", text: "江宁区", pId: 1}],
                // [{id: 111, value: "40", text: "40", pId: 11},
                //     {id: 112, value: "60", text: "60", pId: 11},
                //     {id: 211, value: "60", text: "60", pId: 21},
                //     {id: 121, value: "40", text: "40", pId: 12}],
                // [{id: 1111, value: "70-89", text: "70-89", pId: 111},
                //     {id: 1112, value: "89-100", text: "89-100", pId: 111},
                //     {id: 2111, value: "70-89", text: "70-89", pId: 211},
                //     {id: 2112, value: "89-100", text: "89-100", pId: 211},
                //     {id: 1121, value: "70-89", text: "70-89", pId: 112},
                //     {id: 1122, value: "89-100", text: "89-100", pId: 112}]
            ],
            titles: titles,
            selectedValues: v
        });
    },

    getValue: function () {
        return this.treeLabel.getValue();
    }
});
BI.SelectTreeLabel.EVENT_CONFIRM = "SelectTreeLabel.EVENT_CONFIRM";
$.shortcut('bi.select_tree_label', BI.SelectTreeLabel);