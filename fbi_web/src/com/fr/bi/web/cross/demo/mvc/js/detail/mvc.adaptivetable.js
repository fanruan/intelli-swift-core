AdaptiveTableView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(AdaptiveTableView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-adaptive-table bi-mvc-layout"
        })
    },

    _init: function () {
        AdaptiveTableView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        //var items = [[{
        //    text: "第一行第一列111111111"
        //}, {
        //    text: "第一行第二列"
        //}, {
        //    text: "第一行第三列"
        //}], [{
        //    text: "第二行第一列"
        //}, {
        //    text: "第二行第二列"
        //}, {
        //    text: "第二行第三列"
        //}], [{
        //    text: "第二行第一列"
        //}, {
        //    text: "第三行第二列"
        //}, {
        //    text: "第三行第三列"
        //}], [{
        //    text: "第二行第一列"
        //}, {
        //    text: "第四行第二列"
        //}, {
        //    text: "第四行第三列"
        //}], [{
        //    text: "第五行第一列"
        //}, {
        //    text: "第五行第二列"
        //}, {
        //    text: "第五行第三列"
        //}], [{
        //    text: "第六行第一列"
        //}, {
        //    text: "第六行第二列"
        //}, {
        //    text: "第六行第三列"
        //}], [{
        //    text: "第七行第一列"
        //}, {
        //    text: "第七行第二列"
        //}, {
        //    text: "第七行第三列"
        //}], [{
        //    text: "第八行第一列"
        //}, {
        //    text: "第八行第二列"
        //}, {
        //    text: "第八行第三列"
        //}], [{
        //    text: "第九行第一列"
        //}, {
        //    text: "第九行第二列"
        //}, {
        //    text: "第九行第三列"
        //}], [{
        //    text: "第十行第一列"
        //}, {
        //    text: "第十行第二列"
        //}, {
        //    text: "第十行第三列"
        //}], [{
        //    text: "第十一行第一列"
        //}, {
        //    text: "第十一行第二列"
        //}, {
        //    text: "第十一行第三列"
        //}], [{
        //    text: "第十二行第一列"
        //}, {
        //    text: "第十二行第二列"
        //}, {
        //    text: "第十二行第三列"
        //}], [{
        //    text: "第十三行第一列"
        //}, {
        //    text: "第十三行第二列"
        //}, {
        //    text: "第十三行第三列"
        //}], [{
        //    text: "第十四行第一列"
        //}, {
        //    text: "第十四行第二列"
        //}, {
        //    text: "第十四行第三列"
        //}], [{
        //    text: "第十五行第一列"
        //}, {
        //    text: "第十五行第二列"
        //}, {
        //    text: "第十五行第三列"
        //}], [{
        //    text: "第十六行第一列"
        //}, {
        //    text: "第十六行第二列"
        //}, {
        //    text: "第十六行第三列"
        //}], [{
        //    text: "第十七行第一列"
        //}, {
        //    text: "第十七行第二列"
        //}, {
        //    text: "第十七行第三列"
        //}], [{
        //    text: "第十八行第一列"
        //}, {
        //    text: "第十八行第二列"
        //}, {
        //    text: "第十八行第三列"
        //}]];
        //
        //var header = [[{
        //    text: "表头1"
        //}, {
        //    text: "表头2"
        //}, {
        //    text: "表头3"
        //}]];

        var items = [{
            children: [{
                text: "节点1",
                children: [{
                    text: "子节点1",
                    children: [{
                        text: "叶节点1",
                        values: [{text: 11}, {text: 12}, {text: 11}, {text: 12}, {text: 11}, {text: 12}, {text: 112}]
                    }, {
                        text: "叶节点2",
                        values: [{text: 21}, {text: 22}, {text: 21}, {text: 22}, {text: 21}, {text: 22}, {text: 122}]
                    }],
                    values: [{text: 101}, {text: 102}, {text: 101}, {text: 102}, {text: 101}, {text: 102}, {text: 1102}]
                }, {
                    text: "子节点2",
                    children: [{
                        text: "叶节点3",
                        values: [{text: 31}, {text: 32}, {text: 31}, {text: 32}, {text: 31}, {text: 32}, {text: 132}]
                    }, {
                        text: "叶节点4",
                        values: [{text: 41}, {text: 42}, {text: 41}, {text: 42}, {text: 41}, {text: 42}, {text: 142}]
                    }],
                    values: [{text: 201}, {text: 202}, {text: 201}, {text: 202}, {text: 201}, {text: 202}, {text: 1202}]
                }, {
                    text: "子节点3",
                    children: [{
                        text: "叶节点5",
                        values: [{text: 51}, {text: 52}, {text: 51}, {text: 52}, {text: 51}, {text: 52}, {text: 152}]
                    }],
                    values: [{text: 301}, {text: 302}, {text: 301}, {text: 302}, {text: 301}, {text: 302}, {text: 1302}]
                }],
                values: [{text: 1001}, {text: 1002}, {text: 1001}, {text: 1002}, {text: 1001}, {text: 1002}, {text: 11002}]
            }, {
                text: "节点2",
                values: [{text: 2001}, {text: 2002}, {text: 2001}, {text: 2002}, {text: 2001}, {text: 2002}, {text: 12002}]
            }],
            values: [{text: 12001}, {text: 12002}, {text: 12001}, {text: 12002}, {text: 12001}, {text: 12002}, {text: 112002}]
        }];

        var header = [{
            text: "header1"
        }, {
            text: "header2"
        }, {
            text: "header3"
        }, {
            text: "金额",
            tag: 1
        }, {
            text: "金额",
            tag: 2
        }, {
            text: "金额",
            tag: 3
        }, {
            text: "金额",
            tag: 4
        }, {
            text: "金额",
            tag: 5
        }, {
            text: "金额",
            tag: 6
        }, {
            text: "金额",
            tag: 7
        }];

        var crossHeader = [{
            text: "cross1"
        }, {
            text: "cross2"
        }];

        var crossItems = [{
            children: [{
                text: "节点1",
                children: [{
                    text: "子节点1"
                }, {
                    text: "子节点2"
                }],
                values: [0]
            }, {
                text: "节点2",
                children: [{
                    text: "子节点3"
                }, {
                    text: "子节点4"
                }],
                values: [0]
            }],
            values: [0]
        }];

        var table1 = BI.createWidget({
            type: "bi.adaptive_table",
            el: {
                type: "bi.table_tree"
            },
            columnSize: ["", "", "", "", "", "", "", "", "", ""],
            header: header,
            items: items,
            crossHeader: crossHeader,
            crossItems: crossItems
        });
        var table2 = BI.createWidget({
            type: "bi.adaptive_table",
            el: {
                type: "bi.layer_tree_table"
            },
            isNeedFreeze: true,
            isNeedMerge: true,
            freezeCols: [0, 1, 2],
            mergeCols: [0, 1, 2],
            columnSize: ["", "", "", "", "", "", "", "", "", ""],
            header: header,
            items: items,
            crossHeader: crossHeader,
            crossItems: crossItems
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: {
                    type: "bi.grid",
                    columns: 2,
                    rows: 1,
                    items: [[{
                        el: table1
                    }, {
                        el: table2
                    }]]
                },
                left: 10,
                right: 10,
                top: 10,
                bottom: 10
            }]
        })
    }
});

AdaptiveTableModel = BI.inherit(BI.Model, {});