SelectDataSearchResultView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectDataSearchResultView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-select-data-search-result bi-mvc-layout"
        })
    },

    _init: function () {
        SelectDataSearchResultView.superclass._init.apply(this, arguments);
    },

    _createSegment: function () {
        return this.segment = BI.createWidget({
            type: "bi.select_data_search_segment"
        })
    },

    _createSearchResult: function(){
        return this.resultPane = BI.createWidget({
            type: "bi.select_data_search_result_pane",
            height: 500,
            cls: "mvc-border",
            itemsCreator: function (op, populate) {
                var searchOp = "search";
                if (!op.node) {
                    populate([
                        {
                            id: 1,
                            type: "bi.select_data_level0_node",
                            text: "合同回款信息" + searchOp,
                            value: 1,
                            isParent: true,
                            open: true
                        }, {
                            id: 2,
                            type: "bi.select_data_level0_node",
                            text: "合同信息" + searchOp,
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
                            text: "回款金额" + searchOp,
                            value: 11
                        }, {
                            pId: 1,
                            id: 12,
                            type: "bi.select_data_level0_item",
                            fieldType: BICst.COLUMN.STRING,
                            text: "记录人" + searchOp,
                            value: 12
                        }, {
                            pId: 1,
                            id: 13,
                            type: "bi.select_data_level1_date_node",
                            text: "付款时间" + searchOp,
                            value: 13,
                            isParent: true
                        }, {
                            pId: 1,
                            id: 14,
                            type: "bi.select_data_level1_node",
                            text: "合同信息" + searchOp,
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
                        text: "购买数量" + searchOp,
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
                            text: "年份" + searchOp,
                            value: 131
                        }, {
                            pId: 13,
                            id: 132,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "季度" + searchOp,
                            value: 132
                        }, {
                            pId: 13,
                            id: 133,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "月" + searchOp,
                            value: 133
                        }, {
                            pId: 13,
                            id: 134,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "星期" + searchOp,
                            value: 134
                        }, {
                            pId: 13,
                            id: 135,
                            type: "bi.select_data_level1_item",
                            fieldType: BICst.COLUMN.DATE,
                            text: "日期" + searchOp,
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
                        text: "购买数量" + searchOp,
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
                text: "搜索选项"
            }, {
                el: this._createSegment()
            }, {
                type: "bi.button",
                height: 30,
                text: "getValue",
                level: "success",
                handler: function () {
                    BI.Msg.alert("", self.segment.getValue());
                }
            }, {
                type: "bi.button",
                height: 30,
                text: "setValue",
                level: "warning",
                handler: function () {
                    self.segment.setValue(BI.SelectDataSearchSegment.SECTION_ALL | BI.SelectDataSearchSegment.SECTION_TABLE);
                }
            }, {
                type: "bi.label",
                height: 50,
                text: "搜索结果面板"
            }, {
                el: this._createSearchResult()
            }]
        })
    }
});

SelectDataSearchResultModel = BI.inherit(BI.Model, {});