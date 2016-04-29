TableView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(TableView.superclass._defaultConfig.apply(this, arguments),{
            baseCls: "bi-table bi-mvc-layout"
        })
    },

    _init: function(){
        TableView.superclass._init.apply(this, arguments);
    },

    _createTable1: function(){
        return {
            type: "bi.table",
            items: BI.createItems([
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ],
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ],
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ],
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ],
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ],
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ],
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ]
            ], {
                type: "bi.layout"
            }),
            columnSize: [100, "fill", 200],
            rowSize: [10, 30, 50, 70, 90, 110, 130],
            hgap: 20,
            vgap: 10
        }
    },

    _createTable2: function(){
        return {
            type: "bi.table",
            items: BI.createItems([
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ],
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ],
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ],
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ],
                [
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    },
                    {
                        el: {
                            cls: "layout-bg" + BI.random(1, 8)
                        }
                    }
                ]
            ], {
                type: "bi.layout"
            }),
            columnSize: [0.4, 0.5, "fill"],
            rowSize: [90, 70, 50, 30, 10]
        }
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            columns: 1,
            rows: 1,
            items: [
                {
                    column: 0,
                    row: 0,
                    el: this._createTable1()
                }
                //, {
                //    column: 0,
                //    row: 1,
                //    el: this._createTable2()
                //}
            ]
        })
    }
})

TableModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(TableModel.superclass._defaultConfig.apply(this, arguments),{

        })
    },

    _init: function(){
        TableModel.superclass._init.apply(this, arguments);
    }
})