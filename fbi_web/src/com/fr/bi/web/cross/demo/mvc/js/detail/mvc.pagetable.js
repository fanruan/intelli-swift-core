PageTableView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(PageTableView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-page-table bi-mvc-layout"
        })
    },

    _init: function () {
        PageTableView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var items = [{
            children: [{
                type: "bi.page_table_cell",
                text: "node1",
                children: [{
                    type: "bi.page_table_cell",
                    text: "childnode1",
                    values: [{type: "bi.page_table_cell", text: 101}, {
                        type: "bi.page_table_cell",
                        text: 102
                    }, {type: "bi.page_table_cell", text: 101}]
                }, {
                    type: "bi.page_table_cell",
                    text: "childnode2",
                    values: [{type: "bi.page_table_cell", text: 201}, {
                        type: "bi.page_table_cell",
                        text: 202
                    }, {type: "bi.page_table_cell", text: 201}]
                }],
                values: [{type: "bi.page_table_cell", text: 1001}, {
                    type: "bi.page_table_cell",
                    text: 1002
                }, {type: "bi.page_table_cell", text: 1001}]
            }], values: [{type: "bi.page_table_cell", text: 12001}, {
                type: "bi.page_table_cell",
                text: 12002
            }, {type: "bi.page_table_cell", text: 12001}]
        }];

        var header = [{
            type: "bi.page_table_cell",
            text: "header1"
        }, {
            type: "bi.page_table_cell",
            text: "header2"
        },{
            type: "bi.page_table_cell",
            text: "jine",
            tag: 1
        }, {
            type: "bi.page_table_cell",
            text: "jine",
            tag: 2
        }, {
            type: "bi.page_table_cell",
            text: "jine",
            tag: 3
        }];

        var crossHeader = [{
            type: "bi.page_table_cell",
            text: "cross1"
        }, {
            type: "bi.page_table_cell",
            text: "cross2"
        }];

        var crossItems = [{
            children: [{
                type: "bi.page_table_cell",
                text: "node1",
                children: [{
                    type: "bi.page_table_cell",
                    text: "childnode1"
                }, {
                    type: "bi.page_table_cell",
                    text: "childnode2"
                }, {
                    type: "bi.page_table_cell",
                    text: BI.i18nText("BI-Summary_Values")
                }]
            }]
        }];

        var table1 = BI.createWidget({
            type: "bi.page_table",
            el: {
                el: {
                    el: {
                        type: "bi.table_tree"
                    }
                }
            },
            itemsCreator: function (op, populate) {
                var vpage = op.vpage || "";
                var hpage = op.hpage || "";
                BI.each(header, function (i, h) {
                    h.text = h.text + "V" + vpage + "H" + hpage;
                });
                BI.delay(function () {
                    populate(items, header, crossItems, crossHeader);
                }, 1000);
            },
            columnSize: ["", "", "", "", ""],
            isNeedMerge: true,
            mergeCols: [0, 1,],
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

PageTableModel = BI.inherit(BI.Model, {});