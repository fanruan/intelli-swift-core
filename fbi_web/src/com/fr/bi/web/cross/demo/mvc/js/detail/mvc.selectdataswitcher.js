SelectDataSwitcherView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectDataSwitcherView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-select-data-switcher bi-mvc-layout"
        })
    },

    _init: function () {
        SelectDataSwitcherView.superclass._init.apply(this, arguments);
    },

    _createSwitcher: function () {
        return this.tree = BI.createWidget({
            type: "bi.select_data_switcher",
            cls: "mvc-border",
            height: 400,
            packages: [{
                id: 1,
                text: "第一项",
                value: 1,
                isParent: true
            }, {
                text: "第二项",
                value: 2
            }, {
                id: 3,
                text: "第三项",
                value: 1,
                isParent: true,
                open: true
            }, {
                id: 11,
                pId: 1,
                text: "子项1",
                value: 11
            }, {
                id: 12,
                pId: 1,
                text: "子项2",
                value: 12
            }, {
                id: 13,
                pId: 1,
                text: "子项3",
                value: 13
            }, {
                id: 31,
                pId: 3,
                text: "子项1",
                value: 31
            }, {
                id: 32,
                pId: 3,
                text: "子项2",
                value: 32
            }, {
                id: 33,
                pId: 3,
                text: "子项3",
                value: 33
            }],
            itemsCreator: function (op, populate) {
                var packageName = op.packageName;
                if (!op.node) {
                    populate([
                        {
                            id: 1,
                            type: "bi.select_data_level0_node",
                            text: "合同回款信息" + packageName,
                            value: 1,
                            isParent: true,
                            open: true
                        }, {
                            id: 2,
                            type: "bi.select_data_level0_node",
                            text: "合同信息" + packageName,
                            value: 2,
                            isParent: true,
                            open: true
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
                            text: "回款金额" + packageName,
                            value: 11
                        }, {
                            pId: 1,
                            id: 12,
                            type: "bi.select_data_level0_item",
                            fieldType: BICst.COLUMN.STRING,
                            text: "记录人" + packageName,
                            value: 12
                        }, {
                            pId: 1,
                            id: 13,
                            type: "bi.select_data_level1_date_node",
                            text: "付款时间" + packageName,
                            value: 13,
                            isParent: true
                        }, {
                            pId: 1,
                            id: 14,
                            type: "bi.select_data_level1_node",
                            text: "合同信息" + packageName,
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
                        text: "购买数量" + packageName,
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
                            text: "年份" + packageName,
                            value: 131
                        }, {
                            pId: 13,
                            id: 132,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "季度" + packageName,
                            value: 132
                        }, {
                            pId: 13,
                            id: 133,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "月" + packageName,
                            value: 133
                        }, {
                            pId: 13,
                            id: 134,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "星期" + packageName,
                            value: 134
                        }, {
                            pId: 13,
                            id: 135,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "日期" + packageName,
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
                        text: "购买数量" + packageName,
                        value: 141
                    }]);
                    return;
                }
            }
        })
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
                text: "切换业务包字段"
            }, {
                el: this._createSwitcher()
            }]
        })
    }
});

SelectDataSwitcherModel = BI.inherit(BI.Model, {});