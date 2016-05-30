SimpleSearcherView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SimpleSearcherView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-simple-searcher bi-mvc-layout"
        })
    },

    _init: function () {
        SimpleSearcherView.superclass._init.apply(this, arguments);
    },

    _createSearcher: function () {
        var itemsCreator = function (op, populate) {
            if (!op.node) {
                populate([
                    {
                        id: 1,
                        type: "bi.select_data_level0_node",
                        text: "合同回款信息--",
                        value: 1,
                        isParent: true
                    }, {
                        id: 2,
                        type: "bi.select_data_level0_node",
                        text: "合同信息--",
                        value: 2,
                        isParent: true
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
                        text: "回款金额--",
                        value: 11
                    }, {
                        pId: 1,
                        id: 12,
                        type: "bi.select_data_level0_item",
                        fieldType: BICst.COLUMN.STRING,
                        text: "记录人--",
                        value: 12
                    }, {
                        pId: 1,
                        id: 13,
                        type: "bi.select_data_level1_date_node",
                        text: "付款时间--",
                        value: 13,
                        isParent: true
                    }, {
                        pId: 1,
                        id: 14,
                        type: "bi.select_data_level1_node",
                        text: "合同信息--",
                        value: 14,
                        isParent: true
                    }
                ]);
                return;
            }
            if (op.node.id === 2) {
                populate([{
                    pId: 2,
                    id: 21,
                    type: "bi.select_data_level0_item",
                    fieldType: BICst.COLUMN.NUMBER,
                    text: "购买数量--",
                    value: 21
                }]);
                return;
            }
            if (op.node.id === 13) {
                populate([
                    {
                        pId: 13,
                        id: 131,
                        type: "bi.select_data_level1_item",
                        fieldType: BICst.COLUMN.DATE,
                        text: "年份--",
                        value: 131
                    }, {
                        pId: 13,
                        id: 132,
                        type: "bi.select_data_level1_item",
                        fieldType: BICst.COLUMN.DATE,
                        text: "季度--",
                        value: 132
                    }, {
                        pId: 13,
                        id: 133,
                        type: "bi.select_data_level1_item",
                        fieldType: BICst.COLUMN.DATE,
                        text: "月--",
                        value: 133
                    }, {
                        pId: 13,
                        id: 134,
                        type: "bi.select_data_level1_item",
                        fieldType: BICst.COLUMN.DATE,
                        text: "星期--",
                        value: 134
                    }, {
                        pId: 13,
                        id: 135,
                        type: "bi.select_data_level1_item",
                        fieldType: BICst.COLUMN.DATE,
                        text: "日期--",
                        value: 135
                    }
                ]);
                return;
            }
            if (op.node.id === 14) {
                populate([{
                    pId: 14,
                    id: 141,
                    type: "bi.select_data_level1_item",
                    fieldType: BICst.COLUMN.NUMBER,
                    text: "购买数量--",
                    value: 141
                }]);
                return;
            }
        };
        var searcher = BI.createWidget({
            type: "bi.simple_searcher",
            width: 200,
            height: 600,
            cls: "mvc-border",
            itemsCreator: itemsCreator
        });
        searcher.populate();
        return searcher;
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
                text: "搜索功能面板"
            }, {
                el: this._createSearcher()
            }]
        });
    }
});

SimpleSearcherModel = BI.inherit(BI.Model, {});