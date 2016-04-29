SelectDataTreeView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectDataTreeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-select-data-tree bi-mvc-layout"
        })
    },

    _init: function () {
        SelectDataTreeView.superclass._init.apply(this, arguments);
    },

    _createExpander: function () {
        return this.expander = BI.createWidget({
            type: "bi.select_data_expander",
            el: {
                type: "bi.select_data_level0_node",
                text: "可展开节点"
            },
            popup: {
                itemsCreator: function (op, populate) {
                    populate([
                        {
                            type: "bi.select_data_level0_item",
                            text: "项目1",
                            value: 1
                        }, {
                            type: "bi.select_data_level0_item",
                            text: "项目2",
                            value: 2
                        }, {
                            type: "bi.select_data_level0_item",
                            text: "项目3",
                            value: 3
                        }, {
                            type: "bi.select_data_level0_item",
                            text: "项目4",
                            value: 4
                        }
                    ])
                }
            }
        })
    },

    _createDateExpander: function () {
        return this.dateexpander = BI.createWidget({
            type: "bi.select_data_expander",
            el: {
                type: "bi.select_data_level1_date_node",
                text: "付款时间"
            },
            popup: {
                itemsCreator: function (op, populate) {
                    populate([
                        {
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "年份",
                            value: 1
                        }, {
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "季度",
                            value: 2
                        }, {
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "月",
                            value: 3
                        }, {
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "星期",
                            value: 4
                        }, {
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "日期",
                            value: 5
                        }
                    ])
                }
            }
        })
    },

    _createTree: function () {
        this.tree = BI.createWidget({
            type: "bi.select_data_tree",
            height: 300,
            el: {
                el: {
                    behaviors: {
                        "highlight": function(val, ob){
                            if(val === 12){
                                return true;
                            }
                            return false;
                        },

                        "redmark": function(val, ob){
                            return true;
                        }
                    }
                }
            },
            itemsCreator: function (op, populate) {
                if (!op.node) {
                    populate([
                        {
                            id: 1,
                            type: "bi.select_data_level0_node",
                            text: "合同回款信息",
                            value: 1,
                            isParent: true,
                            //open: true
                        }, {
                            id: 2,
                            type: "bi.select_data_level0_node",
                            text: "合同信息",
                            value: 2,
                            isParent: true,
                            //open: true
                        }
                    ]);
                    return;
                }
                if (op.node.id === 1) {
                    populate([
                        {
                            pId: 1,
                            id: 11,
                            type: "bi.select_data_level0_item",
                            fieldType: BICst.COLUMN.NUMBER,
                            text: "回款金额",
                            value: 11
                        }, {
                            pId: 1,
                            id: 12,
                            type: "bi.select_data_level0_item",
                            fieldType: BICst.COLUMN.STRING,
                            text: "记录人",
                            value: 12
                        }, {
                            pId: 1,
                            id: 13,
                            type: "bi.select_data_level1_date_node",
                            text: "付款时间",
                            value: 13,
                            isParent: true
                        }, {
                            pId: 1,
                            id: 14,
                            type: "bi.select_data_level1_node",
                            text: "合同信息",
                            value: 14,
                            isParent: true
                        }
                    ]);
                    return;
                }
                if(op.node.id === 2){
                    populate([{
                        pId: 2,
                        id: 21,
                        type: "bi.select_data_level0_item",
                        fieldType: BICst.COLUMN.NUMBER,
                        text: "购买数量",
                        value: 21
                        }]);
                    return;
                }
                if(op.node.id === 13){
                    populate([
                        {
                            pId: 13,
                            id: 131,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "年份",
                            value: 131
                        }, {
                            pId: 13,
                            id: 132,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "季度",
                            value: 132
                        }, {
                            pId: 13,
                            id: 133,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "月",
                            value: 133
                        }, {
                            pId: 13,
                            id: 134,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "星期",
                            value: 134
                        }, {
                            pId: 13,
                            id: 135,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "日期",
                            value: 135
                        }
                    ]);
                    return;
                }
                if(op.node.id === 14){
                    populate([{
                        pId: 14,
                        id: 141,
                        type: "bi.select_data_level1_item",
                        fieldType: BICst.COLUMN.NUMBER,
                        text: "购买数量",
                        value: 141
                    }]);
                    return;
                }
            }
        });
        this.tree.doBehavior("回款");
        return this.tree;
    },

    _render: function (vessel) {
        var self = this;
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [{
                type: "bi.label",
                height: 50,
                text: "基础组件"
            }, {
                type: "bi.select_data_level0_item",
                text: "第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长第一层字段很长很长"
            }, {
                type: "bi.select_data_level1_item",
                text: "第二层字段"
            }, {
                type: "bi.select_data_level0_node",
                text: "第一层节点"
            }, {
                type: "bi.select_data_level1_node",
                text: "第二层节点"
            }, {
                type: "bi.label",
                height: 50,
                text: "字段列表"
            }, {
                type: "bi.select_data_loader",
                items: [
                    {
                        type: "bi.select_data_level0_item",
                        text: "项目1",
                        value: 1
                    }, {
                        type: "bi.select_data_level0_item",
                        text: "项目2",
                        value: 2
                    }, {
                        type: "bi.select_data_level0_item",
                        text: "项目3",
                        value: 3
                    }, {
                        type: "bi.select_data_level0_item",
                        text: "项目4",
                        value: 4
                    }
                ]
            }, {
                type: "bi.label",
                height: 50,
                text: "可展开字段列表"
            }, {
                el: this._createExpander()
            }, {
                type: "bi.button",
                text: "getValue",
                height: 30,
                handler: function () {
                    BI.Msg.alert("", JSON.stringify(self.expander.getValue()))
                }
            }, {
                type: "bi.label",
                height: 50,
                text: "可展开的日期字段列表"
            }, {
                el: this._createDateExpander()
            }, {
                type: "bi.label",
                height: 50,
                text: "字段树"
            }, {
                el: this._createTree()
            }]
        })
    }
});

SelectDataTreeModel = BI.inherit(BI.Model, {});