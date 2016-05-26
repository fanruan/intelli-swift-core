SequenceTableView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SequenceTableView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-sequence-table bi-mvc-layout"
        })
    },

    _init: function () {
        SequenceTableView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var items = [[{
            text: "第一行第一列"
        }, {
            text: "第一行第一列"
        }, {
            text: "第一行第三列"
        }], [{
            text: "第二行第一列"
        }, {
            text: "第二行第二列"
        }, {
            text: "第二行第三列"
        }], [{
            text: "第二行第一列"
        }, {
            text: "第三行第二列"
        }, {
            text: "第三行第三列"
        }], [{
            text: "第二行第一列"
        }, {
            text: "第四行第二列"
        }, {
            text: "第四行第三列"
        }], [{
            text: "第五行第一列"
        }, {
            text: "第五行第二列"
        }, {
            text: "第五行第三列"
        }], [{
            text: "第六行第一列"
        }, {
            text: "第六行第二列"
        }, {
            text: "第六行第三列"
        }], [{
            text: "第七行第一列"
        }, {
            text: "第七行第二列"
        }, {
            text: "第七行第三列"
        }], [{
            text: "第八行第一列"
        }, {
            text: "第八行第二列"
        }, {
            text: "第八行第三列"
        }], [{
            text: "第九行第一列"
        }, {
            text: "第九行第二列"
        }, {
            text: "第九行第三列"
        }], [{
            text: "第十行第一列"
        }, {
            text: "第十行第二列"
        }, {
            text: "第十行第三列"
        }], [{
            text: "第十一行第一列"
        }, {
            text: "第十一行第二列"
        }, {
            text: "第十一行第三列"
        }], [{
            text: "第十二行第一列"
        }, {
            text: "第十二行第二列"
        }, {
            text: "第十二行第三列"
        }], [{
            text: "第十三行第一列"
        }, {
            text: "第十三行第二列"
        }, {
            text: "第十三行第三列"
        }], [{
            text: "第十四行第一列"
        }, {
            text: "第十四行第二列"
        }, {
            text: "第十四行第三列"
        }], [{
            text: "第十五行第一列"
        }, {
            text: "第十五行第二列"
        }, {
            text: "第十五行第三列"
        }], [{
            text: "第十六行第一列"
        }, {
            text: "第十六行第二列"
        }, {
            text: "第十六行第三列"
        }], [{
            text: "第十七行第一列"
        }, {
            text: "第十七行第二列"
        }, {
            text: "第十七行第三列"
        }], [{
            text: "第十八行第一列"
        }, {
            text: "第十八行第二列"
        }, {
            text: "第十八行第三列"
        }]];

        var header = [[{
            text: "表头1"
        }, {
            text: "表头2"
        }, {
            text: "表头3"
        }]];

        var table1 = BI.createWidget({
            type: "bi.sequence_table",
            isNeedFreeze: true,
            freezeCols: [0, 1],
            columnSize: ["", "", ""],
            isNeedMerge: true,
            mergeCols: [0, 1],
            header: header,
            items: items
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: {
                    type: "bi.grid",
                    columns: 1,
                    rows: 1,
                    items: [[{
                        el: table1
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

SequenceTableModel = BI.inherit(BI.Model, {});