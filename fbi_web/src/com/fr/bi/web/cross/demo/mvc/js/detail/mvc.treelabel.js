/**
 * Created by fay on 2016/9/18.
 */
TreeLabelView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TreeLabelView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: ""
        })
    },

    _init: function () {
        TreeLabelView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var tree = [
            [{id: 1, value: "南京", text: "南京", pId: null},
                {id: 2, value: "苏州", text: "苏州", pId: null},
                {id: 3, value: "无锡", text: "无锡", pId: null}],
            [{id: 11, value: "玄武区", text: "玄武区", pId: 1},
                {id: 21, value: "工业园区", text: "工业园区", pId: 2},
                {id: 31, value: "南长区", text: "南长区", pId: 3},
                {id: 12, value: "江宁区", text: "江宁区", pId: 1}],
            [{id: 111, value: "40", text: "40", pId: 11},
                {id: 112, value: "60", text: "60", pId: 11},
                {id: 211, value: "60", text: "60", pId: 21},
                {id: 112, value: "40", text: "40", pId: 12}],
            [{id: 1111, value: "70-89", text: "70-89", pId: 111},
                {id: 1112, value: "89-100", text: "89-100", pId: 111},
                {id: 2111, value: "70-89", text: "70-89", pId: 211},
                {id: 2112, value: "89-100", text: "89-100", pId: 211},
                {id: 1121, value: "70-89", text: "70-89", pId: 112},
                {id: 1122, value: "89-100", text: "89-100", pId: 112}]
        ];
        var treeLabel = BI.createWidget({
            type: "bi.tree_label",
            titles: ["城市", "区域", "价格", "面积"],
            itemsCreator: function (options, callback) {
                if (BI.isEmptyObject(options)) {
                    callback({items: tree});
                    return;
                }
                var ids = options.id,
                    floor = options.floor,
                    value = options.value,
                    selectedValues = options.selectedValues,
                    result = [],
                    temp = [];
                if (ids) {
                    for (var i = floor + 1; i < tree.length; i++) {
                        temp = [];
                        BI.each(tree[i], function (idx, node) {
                            if (contains(ids, node)) {
                                temp.push(node);
                                if (BI.contains(selectedValues[i], node.value)) {
                                    result.unshift(node);
                                } else {
                                    result.push(node);
                                }
                            }
                        });
                        ids = temp;
                    }
                    callback({items: result});
                } else {
                    callback({
                        items: [
                            {id: 1, value: "南京", text: "南京", pId: null},
                            {id: 2, value: "苏州", text: "苏州", pId: null},
                            {id: 3, value: "无锡", text: "无锡", pId: null},
                            {id: 11, value: "玄武区", text: "玄武区", pId: 1},
                            {id: 21, value: "工业园区", text: "工业园区", pId: 2},
                            {id: 31, value: "南长区", text: "南长区", pId: 3},
                            {id: 12, value: "江宁区", text: "江宁区", pId: 1},
                            {id: 111, value: "40", text: "40", pId: 11},
                            {id: 112, value: "60", text: "60", pId: 11},
                            {id: 211, value: "60", text: "60", pId: 21},
                            {id: 112, value: "40", text: "40", pId: 12},
                            {id: 1111, value: "70-89", text: "70-89", pId: 111},
                            {id: 1112, value: "89-100", text: "89-100", pId: 111},
                            {id: 2111, value: "70-89", text: "70-89", pId: 211},
                            {id: 2112, value: "89-100", text: "89-100", pId: 211},
                            {id: 1121, value: "70-89", text: "70-89", pId: 112},
                            {id: 1122, value: "89-100", text: "89-100", pId: 112}
                        ]
                    });
                    // temp = [];
                    // for (var i = floor+1; i < selectedValues.length; i++) {
                    //     var preValues = selectedValues[i-1];
                    // }
                }

                function contains(ids, node) {
                    if (BI.isArray(ids)) {
                        for (var i = 0; i < ids.length; i++) {
                            if (ids[i].id === node.pId) {
                                return true;
                            }
                        }
                    } else {
                        if (ids === node.pId) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        });

        var button = BI.createWidget({
            type: 'bi.button',
            text: '取值',
            level: 'common',
            height: 30,
            width: 60,
            handler: function () {
                BI.Msg.alert('提示', JSON.stringify(treeLabel.getValue()));
            }
        });

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [treeLabel, button]
        })
    }
});

TreeLabelModel = BI.inherit(BI.Model, {});