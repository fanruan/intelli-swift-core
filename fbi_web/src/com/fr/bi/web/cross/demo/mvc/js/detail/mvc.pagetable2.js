PageTable2View = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(PageTable2View.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-page-table bi-mvc-layout"
        })
    },

    _init: function () {
        PageTable2View.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var items = [{
            children: [{
                type: "bi.page_table_cell",
                text: "节点1",
                children: [{
                    type: "bi.page_table_cell",
                    text: "子节点1",
                    children: [{
                        type: "bi.page_table_cell",
                        text: "叶节点1",
                        values: [{type: "bi.page_table_cell", text: 11}, {
                            type: "bi.page_table_cell",
                            text: 12
                        }, {type: "bi.page_table_cell", text: 11}, {
                            type: "bi.page_table_cell",
                            text: 12
                        }, {type: "bi.page_table_cell", text: 11}, {
                            type: "bi.page_table_cell",
                            text: 12
                        }, {type: "bi.page_table_cell", text: 112}]
                    }, {
                        type: "bi.page_table_cell",
                        text: "叶节点2",
                        values: [{type: "bi.page_table_cell", text: 21}, {
                            type: "bi.page_table_cell",
                            text: 22
                        }, {type: "bi.page_table_cell", text: 21}, {
                            type: "bi.page_table_cell",
                            text: 22
                        }, {type: "bi.page_table_cell", text: 21}, {
                            type: "bi.page_table_cell",
                            text: 22
                        }, {type: "bi.page_table_cell", text: 122}]
                    }],
                    values: [{type: "bi.page_table_cell", text: 101}, {
                        type: "bi.page_table_cell",
                        text: 102
                    }, {type: "bi.page_table_cell", text: 101}, {
                        type: "bi.page_table_cell",
                        text: 102
                    }, {type: "bi.page_table_cell", text: 101}, {
                        type: "bi.page_table_cell",
                        text: 102
                    }, {type: "bi.page_table_cell", text: 1102}]
                }, {
                    type: "bi.page_table_cell",
                    text: "子节点2",
                    children: [{
                        type: "bi.page_table_cell",
                        text: "叶节点3",
                        values: [{type: "bi.page_table_cell", text: 31}, {
                            type: "bi.page_table_cell",
                            text: 32
                        }, {type: "bi.page_table_cell", text: 31}, {
                            type: "bi.page_table_cell",
                            text: 32
                        }, {type: "bi.page_table_cell", text: 31}, {
                            type: "bi.page_table_cell",
                            text: 32
                        }, {type: "bi.page_table_cell", text: 132}]
                    }, {
                        type: "bi.page_table_cell",
                        text: "叶节点4",
                        values: [{type: "bi.page_table_cell", text: 41}, {
                            type: "bi.page_table_cell",
                            text: 42
                        }, {type: "bi.page_table_cell", text: 41}, {
                            type: "bi.page_table_cell",
                            text: 42
                        }, {type: "bi.page_table_cell", text: 41}, {
                            type: "bi.page_table_cell",
                            text: 42
                        }, {type: "bi.page_table_cell", text: 142}]
                    }],
                    values: [{type: "bi.page_table_cell", text: 201}, {
                        type: "bi.page_table_cell",
                        text: 202
                    }, {type: "bi.page_table_cell", text: 201}, {
                        type: "bi.page_table_cell",
                        text: 202
                    }, {type: "bi.page_table_cell", text: 201}, {
                        type: "bi.page_table_cell",
                        text: 202
                    }, {type: "bi.page_table_cell", text: 1202}]
                }, {
                    type: "bi.page_table_cell",
                    text: "子节点3",
                    children: [{
                        type: "bi.page_table_cell",
                        text: "叶节点5",
                        values: [{type: "bi.page_table_cell", text: 51}, {
                            type: "bi.page_table_cell",
                            text: 52
                        }, {type: "bi.page_table_cell", text: 51}, {
                            type: "bi.page_table_cell",
                            text: 52
                        }, {type: "bi.page_table_cell", text: 51}, {
                            type: "bi.page_table_cell",
                            text: 52
                        }, {type: "bi.page_table_cell", text: 152}]
                    }],
                    values: [{type: "bi.page_table_cell", text: 301}, {
                        type: "bi.page_table_cell",
                        text: 302
                    }, {type: "bi.page_table_cell", text: 301}, {
                        type: "bi.page_table_cell",
                        text: 302
                    }, {type: "bi.page_table_cell", text: 301}, {
                        type: "bi.page_table_cell",
                        text: 302
                    }, {type: "bi.page_table_cell", text: 1302}]
                }],
                values: [{type: "bi.page_table_cell", text: 1001}, {
                    type: "bi.page_table_cell",
                    text: 1002
                }, {type: "bi.page_table_cell", text: 1001}, {
                    type: "bi.page_table_cell",
                    text: 1002
                }, {type: "bi.page_table_cell", text: 1001}, {
                    type: "bi.page_table_cell",
                    text: 1002
                }, {type: "bi.page_table_cell", text: 11002}]
            }, {
                type: "bi.page_table_cell",
                text: "节点1",
                children: [{
                    type: "bi.page_table_cell",
                    text: "子节点1",
                    children: [{
                        type: "bi.page_table_cell",
                        text: "叶节点1",
                        values: [{type: "bi.page_table_cell", text: 11}, {
                            type: "bi.page_table_cell",
                            text: 12
                        }, {type: "bi.page_table_cell", text: 11}, {
                            type: "bi.page_table_cell",
                            text: 12
                        }, {type: "bi.page_table_cell", text: 11}, {
                            type: "bi.page_table_cell",
                            text: 12
                        }, {type: "bi.page_table_cell", text: 112}]
                    }, {
                        type: "bi.page_table_cell",
                        text: "叶节点2",
                        values: [{type: "bi.page_table_cell", text: 21}, {
                            type: "bi.page_table_cell",
                            text: 22
                        }, {type: "bi.page_table_cell", text: 21}, {
                            type: "bi.page_table_cell",
                            text: 22
                        }, {type: "bi.page_table_cell", text: 21}, {
                            type: "bi.page_table_cell",
                            text: 22
                        }, {type: "bi.page_table_cell", text: 122}]
                    }],
                    values: [{type: "bi.page_table_cell", text: 101}, {
                        type: "bi.page_table_cell",
                        text: 102
                    }, {type: "bi.page_table_cell", text: 101}, {
                        type: "bi.page_table_cell",
                        text: 102
                    }, {type: "bi.page_table_cell", text: 101}, {
                        type: "bi.page_table_cell",
                        text: 102
                    }, {type: "bi.page_table_cell", text: 1102}]
                }, {
                    type: "bi.page_table_cell",
                    text: "子节点2",
                    children: [{
                        type: "bi.page_table_cell",
                        text: "叶节点3",
                        values: [{type: "bi.page_table_cell", text: 31}, {
                            type: "bi.page_table_cell",
                            text: 32
                        }, {type: "bi.page_table_cell", text: 31}, {
                            type: "bi.page_table_cell",
                            text: 32
                        }, {type: "bi.page_table_cell", text: 31}, {
                            type: "bi.page_table_cell",
                            text: 32
                        }, {type: "bi.page_table_cell", text: 132}]
                    }, {
                        type: "bi.page_table_cell",
                        text: "叶节点4",
                        values: [{type: "bi.page_table_cell", text: 41}, {
                            type: "bi.page_table_cell",
                            text: 42
                        }, {type: "bi.page_table_cell", text: 41}, {
                            type: "bi.page_table_cell",
                            text: 42
                        }, {type: "bi.page_table_cell", text: 41}, {
                            type: "bi.page_table_cell",
                            text: 42
                        }, {type: "bi.page_table_cell", text: 142}]
                    }],
                    values: [{type: "bi.page_table_cell", text: 201}, {
                        type: "bi.page_table_cell",
                        text: 202
                    }, {type: "bi.page_table_cell", text: 201}, {
                        type: "bi.page_table_cell",
                        text: 202
                    }, {type: "bi.page_table_cell", text: 201}, {
                        type: "bi.page_table_cell",
                        text: 202
                    }, {type: "bi.page_table_cell", text: 1202}]
                }, {
                    type: "bi.page_table_cell",
                    text: "子节点3",
                    children: [{
                        type: "bi.page_table_cell",
                        text: "叶节点5",
                        values: [{type: "bi.page_table_cell", text: 51}, {
                            type: "bi.page_table_cell",
                            text: 52
                        }, {type: "bi.page_table_cell", text: 51}, {
                            type: "bi.page_table_cell",
                            text: 52
                        }, {type: "bi.page_table_cell", text: 51}, {
                            type: "bi.page_table_cell",
                            text: 52
                        }, {type: "bi.page_table_cell", text: 152}]
                    }],
                    values: [{type: "bi.page_table_cell", text: 301}, {
                        type: "bi.page_table_cell",
                        text: 302
                    }, {type: "bi.page_table_cell", text: 301}, {
                        type: "bi.page_table_cell",
                        text: 302
                    }, {type: "bi.page_table_cell", text: 301}, {
                        type: "bi.page_table_cell",
                        text: 302
                    }, {type: "bi.page_table_cell", text: 1302}]
                }],
                values: [{type: "bi.page_table_cell", text: 1001}, {
                    type: "bi.page_table_cell",
                    text: 1002
                }, {type: "bi.page_table_cell", text: 1001}, {
                    type: "bi.page_table_cell",
                    text: 1002
                }, {type: "bi.page_table_cell", text: 1001}, {
                    type: "bi.page_table_cell",
                    text: 1002
                }, {type: "bi.page_table_cell", text: 11002}]
            }, {
                type: "bi.page_table_cell",
                text: "节点3",
                values: [{type: "bi.page_table_cell", text: 2001}, {
                    type: "bi.page_table_cell",
                    text: 2002
                }, {type: "bi.page_table_cell", text: 2001}, {
                    type: "bi.page_table_cell",
                    text: 2002
                }, {type: "bi.page_table_cell", text: 2001}, {
                    type: "bi.page_table_cell",
                    text: 2002
                }, {type: "bi.page_table_cell", text: 12002}]
            }], values: [{type: "bi.page_table_cell", text: 12001}, {
                type: "bi.page_table_cell",
                text: 12002
            }, {type: "bi.page_table_cell", text: 12001}, {
                type: "bi.page_table_cell",
                text: 12002
            }, {type: "bi.page_table_cell", text: 12001}, {
                type: "bi.page_table_cell",
                text: 12002
            }, {type: "bi.page_table_cell", text: 112002}]
        }];

        var header = [{
            type: "bi.page_table_cell",
            text: "header1"
        }, {
            type: "bi.page_table_cell",
            text: "header2"
        }, {
            type: "bi.page_table_cell",
            text: "header3"
        }, {
            type: "bi.page_table_cell",
            text: "金额",
            tag: 1
        }, {
            type: "bi.page_table_cell",
            text: "金额",
            tag: 2
        }, {
            type: "bi.page_table_cell",
            text: "金额",
            tag: 3
        }, {
            type: "bi.page_table_cell",
            text: "金额",
            tag: 4
        }, {
            type: "bi.page_table_cell",
            text: "金额",
            tag: 5
        }, {
            type: "bi.page_table_cell",
            text: "金额",
            tag: 6
        }, {
            type: "bi.page_table_cell",
            text: "金额",
            tag: 7
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
                text: "节点1",
                children: [{
                    type: "bi.page_table_cell",
                    text: "子节点1"
                }, {
                    type: "bi.page_table_cell",
                    text: "子节点2"
                }],
                values: [0]
            }, {
                type: "bi.page_table_cell",
                text: "节点2",
                children: [{
                    type: "bi.page_table_cell",
                    text: "子节点3"
                }, {
                    type: "bi.page_table_cell",
                    text: "子节点4"
                }],
                values: [0]
            }],
            values: [0]
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
            hasHNext: function (page) {
                return page < 3;
            },
            isNeedFreeze: true,
            freezeCols: [0, 1, 2],
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
            columnSize: ["", "", "", "", "", "", "", "", "", ""],
            isNeedMerge: true,
            mergeCols: [0, 1, 2],
            regionColumnSize: [200, 'fill'],
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

PageTable2Model = BI.inherit(BI.Model, {});