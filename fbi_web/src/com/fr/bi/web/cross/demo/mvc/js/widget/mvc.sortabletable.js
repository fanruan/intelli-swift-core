SortableTableView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SortableTableView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-skip-pager bi-mvc-layout"
        })
    },

    _init: function () {
        SortableTableView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var header = [[{
            text: "表头1"
        }, {
            text: "表头2"
        }, {
            text: "表头3"
        }]];

        var items = [[{
            text: "第一行第一列"
        }, {
            text: "第一行第一列"
        }, {
            text: "第一行第一列"
        }], [{
            text: "第一行第一列"
        }, {
            text: "第一行第一列"
        }, {
            text: "第一行第一列"
        }], [{
            text: "第三行第一列"
        }, {
            text: "第三行第二列"
        }, {
            text: "第三行第三列"
        }], [{
            text: "第四行第一列"
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

        var sortabletable = BI.createWidget({
            type: "bi.sortable_table",
            rowSize: 25,
            headerRowSize: 30,
            //isResizeAdapt: false,
            items: items,
            header: header
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 200,
            vgap: 50,
            items: [sortabletable, {
                type: "bi.button",
                value: "getValue",
                handler: function(){
                    BI.Msg.alert("", group.getValue());
                }
            }]
        })
    }
});

SortableTableModel = BI.inherit(BI.Model, {});